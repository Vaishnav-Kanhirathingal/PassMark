package easter.egg.passmark.ui.auth.master_key

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import easter.egg.passmark.data.models.User
import easter.egg.passmark.data.storage.PassMarkDataStore
import easter.egg.passmark.data.supabase.account.SupabaseAccountHelper
import easter.egg.passmark.data.supabase.api.UserApi
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.security.PasswordCryptographyHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.security.SecureRandom
import javax.inject.Inject

// TODO: rename
@HiltViewModel
class MasterKeyViewModel @Inject constructor(
    private val supabaseAccountHelper: SupabaseAccountHelper,
    private val userApi: UserApi
) : ViewModel() {
    private val TAG = this::class.simpleName

    private val _masterPasswordText: MutableStateFlow<String> = MutableStateFlow(value = "")
    val masterPasswordText: StateFlow<String> get() = _masterPasswordText
    fun updateMasterPasswordText(value: String) {
        this._masterPasswordText.value = value
    }

    private val _visible: MutableStateFlow<Boolean> = MutableStateFlow(value = true)
    val visible: StateFlow<Boolean> get() = _visible
    fun switchVisibility() {
        this._visible.value = !this._visible.value
    }

    private val _showError: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showError: StateFlow<Boolean> get() = _showError
    fun updateShowError() {
        this._showError.value = true
    }

    private val _screenState: MutableStateFlow<ScreenState<Unit>> =
        MutableStateFlow(ScreenState.PreCall())
    val screenState: StateFlow<ScreenState<Unit>> get() = _screenState

    fun onButtonPress(
        isNewUser: Boolean,
        context: Context
    ) {
        _screenState.value = ScreenState.Loading()
        viewModelScope.launch {
            val password = this@MasterKeyViewModel.masterPasswordText.value
            Log.d(TAG, "password = $password")

            val newState: ScreenState<Unit> =
                try {
                    val authId = this@MasterKeyViewModel.supabaseAccountHelper.getId()
                    val dataStoreHandler = PassMarkDataStore(context = context, authId = authId)
                    if (isNewUser) {
                        setUpNewUser(
                            password = password,
                            dataStoreHandler = dataStoreHandler
                        )
                    } else {
                        verifyUser(
                            password = password,
                            dataStoreHandler = dataStoreHandler,
                            userApi = userApi
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    ScreenState.ApiError.fromException(e = e)
                }
            this@MasterKeyViewModel._screenState.value = newState
        }
    }

    @Throws(Exception::class)
    private suspend fun setUpNewUser(
        password: String,
        dataStoreHandler: PassMarkDataStore,
    ): ScreenState.Loaded<Unit> {
        val passwordCryptographyHandler = PasswordCryptographyHandler(
            password = password,
            initializationVector = ByteArray(size = 16).also { SecureRandom().nextBytes(it) }
        )
        userApi.setUser(
            user = User(
                passwordPuzzleEncrypted = passwordCryptographyHandler.getEncryptedPuzzle(),
                encryptionKeyInitializationVector = passwordCryptographyHandler.initializationVectorAsString
            )
        )
        dataStoreHandler.savePassword(password = password)
        return ScreenState.Loaded(Unit)
    }

    @Throws(Exception::class)
    private suspend fun verifyUser(
        password: String,
        dataStoreHandler: PassMarkDataStore,
        userApi: UserApi
    ): ScreenState<Unit> {
        val user = userApi.getUser()!!
        val passwordCryptographyHandler = PasswordCryptographyHandler(
            password = password,
            initializationVector = user.encryptionKeyInitializationVector
        )
        val isPasswordCorrect = passwordCryptographyHandler.solvesPuzzle(
            apiProvidedEncryptedPuzzle = user.passwordPuzzleEncrypted
        )
        return if (isPasswordCorrect) {
            dataStoreHandler.savePassword(password)
            ScreenState.Loaded(Unit)
        } else {
            ScreenState.ApiError.SomethingWentWrong(alternateToastMessage = "Password is incorrect")
        }
    }
}

enum class PasswordTextState {
    TOO_SMALL, OK_LENGTH, TOO_LARGE;

    fun getMessage(): String = when (this) {
        TOO_SMALL -> "Password should be more than 8 in length"
        OK_LENGTH -> "Password is of correct length"
        TOO_LARGE -> "Password should be less than 32 in length"
    }

    companion object {

        fun getEState(password: String): PasswordTextState {
            return password.length.let {
                when {
                    it >= 32 -> TOO_LARGE
                    it <= 8 -> TOO_SMALL
                    else -> OK_LENGTH
                }
            }
        }
    }
}