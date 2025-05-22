package easter.egg.passmark.ui.auth.loader

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import easter.egg.passmark.data.storage.PassMarkDataStore
import easter.egg.passmark.data.supabase.account.SupabaseAccountHelper
import easter.egg.passmark.data.supabase.api.UserApi
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.security.PasswordCryptographyHandler
import easter.egg.passmark.utils.testing.TestTags
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoaderViewModel @Inject constructor(
    val supabaseAccountHelper: SupabaseAccountHelper,
    val userApi: UserApi,
    @ApplicationContext val applicationContext: Context
) : ViewModel() {
    private val TAG = this::class.simpleName

    private val _screenState: MutableStateFlow<ScreenState<UserState>> =
        MutableStateFlow(value = ScreenState.PreCall())
    val screenState: StateFlow<ScreenState<UserState>> get() = _screenState

    init {
        viewModelScope.launch {
            supabaseAccountHelper.getSessionStatus().collect {
                when (it) {
                    is SessionStatus.Authenticated -> {
                        this@LoaderViewModel.verifyKeyState()
                        this.cancel()
                    }

                    SessionStatus.Initializing -> {
                        this@LoaderViewModel._screenState.value = ScreenState.Loading()
                    }

                    is SessionStatus.RefreshFailure, is SessionStatus.NotAuthenticated -> {
                        this@LoaderViewModel._screenState.value =
                            ScreenState.Loaded(result = UserState.NOT_LOGGED_IN)
                    }
                }
            }
        }
    }

    fun forceVerify() {
        this@LoaderViewModel._screenState.value = ScreenState.Loading()
        viewModelScope.launch { verifyKeyState() }
    }

    private suspend fun verifyKeyState() {
        delay(TestTags.TIME_OUT)
        this@LoaderViewModel._screenState.value = try {
            val user = userApi.getUser()
            val userState: UserState =
                if (user == null) {
                    UserState.NEW_USER
                } else {
                    val password: String? = PassMarkDataStore(
                        context = applicationContext,
                        authId = supabaseAccountHelper.getId()
                    ).fetchPassword().first()
                    if (password == null) {
                        UserState.EXISTS_WITHOUT_KEY_IN_STORAGE
                    } else {
                        PasswordCryptographyHandler(
                            password = password,
                            initializationVector = user.encryptionKeyInitializationVector
                        )
                            .solvesPuzzle(apiProvidedEncryptedPuzzle = user.passwordPuzzleEncrypted)
                            .let { puzzleSolved ->
                                if (puzzleSolved) UserState.EXISTS_WITH_KEY_IN_STORAGE
                                else UserState.EXISTS_WITHOUT_KEY_IN_STORAGE
                            }
                    }
                }
            Log.d(TAG, "user state = ${userState.name}")
            ScreenState.Loaded(userState)
        } catch (e: Exception) {
            e.printStackTrace()
            ScreenState.ApiError.fromException(e = e)
        }
    }
}

enum class UserState {
    NOT_LOGGED_IN,
    NEW_USER,
    EXISTS_WITHOUT_KEY_IN_STORAGE,
    EXISTS_WITH_KEY_IN_STORAGE
}