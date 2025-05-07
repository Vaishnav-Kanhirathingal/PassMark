package easter.egg.passmark.ui.main.change_password

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import easter.egg.passmark.data.storage.PassMarkDataStore
import easter.egg.passmark.data.storage.database.PasswordDao
import easter.egg.passmark.data.supabase.account.SupabaseAccountHelper
import easter.egg.passmark.data.supabase.api.PasswordApi
import easter.egg.passmark.data.supabase.api.UserApi
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.security.PasswordCryptographyHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangeMasterPasswordViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userApi: UserApi,
    private val passwordApi: PasswordApi,
    private val passwordDao: PasswordDao,
    private val supabaseAccountHelper: SupabaseAccountHelper
) : ViewModel() {
    companion object {
        const val INCORRECT_PASSWORD_ERROR_MESSAGE = "INCORRECT_PASSWORD_ERROR_MESSAGE"
    }

    val oldPassword: MutableStateFlow<String> = MutableStateFlow("")
    val newPassword: MutableStateFlow<String> = MutableStateFlow("")
    val newPasswordRepeated: MutableStateFlow<String> = MutableStateFlow("")

    private val _showError: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showError: StateFlow<Boolean> get() = _showError
    fun triggerErrorFlag() {
        this._showError.value = true
    }

    private val _showWrongPasswordError: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showWrongPasswordError: StateFlow<Boolean> get() = _showWrongPasswordError

    private val _screenState: MutableStateFlow<ScreenState<Unit>> =
        MutableStateFlow(ScreenState.PreCall())
    val screenState: StateFlow<ScreenState<Unit>> get() = _screenState

    private val _currentReEncryptionStates: MutableStateFlow<ReEncryptionStates> =
        MutableStateFlow(value = ReEncryptionStates.entries.first())
    val currentReEncryptionStates: StateFlow<ReEncryptionStates> get() = _currentReEncryptionStates

    fun changePassword(
        isSilent: Boolean
    ) {
        if (!isSilent) {
            _screenState.value = ScreenState.Loading()
        }
        viewModelScope.launch {
            val newState: ScreenState<Unit> = try {
                ReEncryptionStates.entries
                    .subList(
                        fromIndex = currentReEncryptionStates.value.ordinal,
                        toIndex = ReEncryptionStates.entries.lastIndex + 1
                    )
                    .forEach { currentState: ReEncryptionStates ->
                        this@ChangeMasterPasswordViewModel._currentReEncryptionStates.value =
                            currentState
                        delay(timeMillis = 1_000L)
                        performAction(currentState)
                    }
                ScreenState.Loaded(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                if (e.message == INCORRECT_PASSWORD_ERROR_MESSAGE) {
                    this@ChangeMasterPasswordViewModel._currentReEncryptionStates.value =
                        ReEncryptionStates.entries.first()
                    this@ChangeMasterPasswordViewModel._showWrongPasswordError.value = true
                    ScreenState.PreCall()
                } else {
                    ScreenState.ApiError.fromException(e = e)
                }
            }
            this@ChangeMasterPasswordViewModel._screenState.value = newState
        }
    }

    private var _oldCryptoHandler: PasswordCryptographyHandler? = null
    private var _newCryptoHandler: PasswordCryptographyHandler? = null

    // TODO: test entirely
    private suspend fun performAction(
        reEncryptionStates: ReEncryptionStates
    ) {
        when (reEncryptionStates) {
            ReEncryptionStates.VERIFYING_PASSWORD -> {
                val user = userApi.getUser()!!
                val oldHandler = PasswordCryptographyHandler(
                    password = oldPassword.value,
                    initializationVector = user.encryptionKeyInitializationVector
                )
                val solved: Boolean = try {
                    oldHandler.solvesPuzzle(user.passwordPuzzleEncrypted)
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }

                if (solved) {
                    val newHandler = PasswordCryptographyHandler(
                        password = newPassword.value,
                        initializationVector = PasswordCryptographyHandler.getNewInitializationVector()
                    )
                    this._oldCryptoHandler = oldHandler
                    this._newCryptoHandler = newHandler
                } else {
                    throw Exception(INCORRECT_PASSWORD_ERROR_MESSAGE)
                }
            }

            ReEncryptionStates.REMOTE_PASSWORD_DATABASE -> {
                passwordApi.reEncryptAllPasswords(
                    oldPasswordCryptographyHandler = this._oldCryptoHandler!!,
                    newPasswordCryptographyHandler = this._newCryptoHandler!!
                )
            }

            ReEncryptionStates.LOCAL_PASSWORD_DATABASE -> {
                val oldList = passwordDao.getAll()
                val newList = oldList.map { oldPass ->
                    oldPass.copy(
                        data = this._newCryptoHandler!!.encryptPasswordData(
                            passwordData = this._oldCryptoHandler!!.decryptPasswordData(
                                passwordData = oldPass.data
                            )
                        )
                    )
                }
                newList.forEach { passwordDao.upsert(passwordCapsule = it) }
            }

            ReEncryptionStates.USER_PUZZLE_AND_IV -> {
                userApi.setUser(
                    user = userApi.getUser()!!.copy(
                        passwordPuzzleEncrypted = _newCryptoHandler!!.getEncryptedPuzzle(),
                        encryptionKeyInitializationVector = _newCryptoHandler!!.initializationVectorAsString
                    )
                )
            }

            ReEncryptionStates.LOCAL_PASSWORD_PURGING -> {
                PassMarkDataStore(
                    context = context,
                    authId = supabaseAccountHelper.getId()
                ).resetPassword()
            }
        }
    }
}

enum class ReEncryptionStates {
    VERIFYING_PASSWORD,
    REMOTE_PASSWORD_DATABASE,
    LOCAL_PASSWORD_DATABASE,
    USER_PUZZLE_AND_IV,
    LOCAL_PASSWORD_PURGING;

    fun getSubtitle(): String {
        return when (this) {
            VERIFYING_PASSWORD -> "Verifying the password entered"
            REMOTE_PASSWORD_DATABASE -> "Re-encrypting local password database"
            LOCAL_PASSWORD_DATABASE -> "Re-encrypting remote password database"
            USER_PUZZLE_AND_IV -> "Making additional changes"
            LOCAL_PASSWORD_PURGING -> "Removing old password details"
        }.let { "(${this.ordinal + 1}/${entries.size}) : $it" }
    }
}