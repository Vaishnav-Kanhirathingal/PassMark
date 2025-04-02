package easter.egg.passmark.ui.sections.loader

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import easter.egg.passmark.data.supabase.account.SupabaseAccountHelper
import easter.egg.passmark.data.supabase.api.UserApi
import easter.egg.passmark.utils.ScreenState
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoaderViewModel @Inject constructor(
    val supabaseAccountHelper: SupabaseAccountHelper, // TODO: use this instead
    val userApi: UserApi
) : ViewModel() {
    private val TAG = this::class.simpleName

    private val _screenState: MutableStateFlow<ScreenState<UserState>> =
        MutableStateFlow(value = ScreenState.PreCall())
    val screenState: StateFlow<ScreenState<UserState>> get() = _screenState

    private val sessionListener = viewModelScope.launch {
        supabaseAccountHelper.getSessionStatus().collect {
            when (it) {
                is SessionStatus.Authenticated -> this@LoaderViewModel.verifyKeyState()
                SessionStatus.Initializing -> {
                    this@LoaderViewModel._screenState.value = ScreenState.Loading()
                }

                is SessionStatus.RefreshFailure, is SessionStatus.NotAuthenticated -> {
                    this@LoaderViewModel._screenState.value =
                        ScreenState.Loaded(result = UserState.DOES_NOT_EXIST)
                }
            }
        }
    }

    private fun verifyKeyState() {
        viewModelScope.launch {
            this@LoaderViewModel.sessionListener.cancel()
            this@LoaderViewModel._screenState.value = try {
                val user = userApi.getUser()
                if (user == null) {
                    ScreenState.Loaded(UserState.NEW_USER)
                } else {
                    TODO(
                        "fetch the key from datastore and verify validity. " +
                                "IF KEY IS IN DATASTORE AND IS CORRECT -> EXISTS_WITH_KEY_IN_STORAGE " +
                                "ELSE -> EXISTS_WITHOUT_KEY_IN_STORAGE"
                    )
                }
            } catch (e: Exception) {
                ScreenState.ApiError.fromException(e = e)
            }
        }
    }
}

enum class UserState {
    DOES_NOT_EXIST,
    NEW_USER,
    EXISTS_WITHOUT_KEY_IN_STORAGE,
    EXISTS_WITH_KEY_IN_STORAGE
}