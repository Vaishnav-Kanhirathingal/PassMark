package easter.egg.passmark.ui.main.password_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import easter.egg.passmark.data.models.content.Password
import easter.egg.passmark.data.models.content.PasswordData
import easter.egg.passmark.data.supabase.api.PasswordApi
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.extensions.nullIfBlank
import easter.egg.passmark.utils.security.PasswordCryptographyHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordEditViewModel @Inject constructor(
    val passwordApi: PasswordApi
) : ViewModel() {
    private val _title: MutableStateFlow<String> = MutableStateFlow("")
    val title: StateFlow<String> get() = _title
    fun updateTitle(newValue: String) {
        this._title.value = newValue
    }

    private val _email: MutableStateFlow<String> = MutableStateFlow("")
    val email: StateFlow<String> get() = _email
    fun updateEmail(newValue: String) {
        this._email.value = newValue
    }

    private val _userName: MutableStateFlow<String> = MutableStateFlow("")
    val userName: StateFlow<String> get() = _userName
    fun updateUserName(newValue: String) {
        this._userName.value = newValue
    }

    private val _password: MutableStateFlow<String> = MutableStateFlow("")
    val password: StateFlow<String> get() = _password
    fun updatePassword(newValue: String) {
        this._password.value = newValue
    }

    private val _website: MutableStateFlow<String> = MutableStateFlow("")
    val website: StateFlow<String> get() = _website
    fun updateWebsite(newValue: String) {
        this._website.value = newValue
    }

    private val _notes: MutableStateFlow<String> = MutableStateFlow("")
    val notes: StateFlow<String> get() = _notes
    fun updateNotes(newValue: String) {
        this._notes.value = newValue
    }

    private val _useFingerPrint: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val useFingerPrint: StateFlow<Boolean> get() = _useFingerPrint
    fun updateUseFingerPrint(newValue: Boolean) {
        this._useFingerPrint.value = newValue
    }

    private val _saveToLocalOnly: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val saveToLocalOnly: StateFlow<Boolean> get() = _saveToLocalOnly
    fun updateSaveToLocalOnly(newValue: Boolean) {
        this._saveToLocalOnly.value = newValue
    }

    private val _showFieldError: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showFieldError: StateFlow<Boolean> get() = _showFieldError
    fun updateShowFieldError() {
        if (!this._showFieldError.value) {
            this._showFieldError.value = true
        }
    }

    //-----------------------------------------------------------------------------------------state

    /** result should be the title of the password stored */
    private val _screenState: MutableStateFlow<ScreenState<Password>> =
        MutableStateFlow(ScreenState.PreCall())
    val screenState: StateFlow<ScreenState<Password>> get() = _screenState

    fun savePassword(
        passwordCryptographyHandler: PasswordCryptographyHandler
    ) {
        _screenState.value = ScreenState.Loading()
        val now = System.currentTimeMillis()
        val password = Password(
            id = null,
            vaultId = null,
            data = PasswordData(
                title = title.value,
                email = email.value.nullIfBlank(),
                userName = userName.value.nullIfBlank(),
                password = password.value,
                website = website.value.nullIfBlank(),
                notes = notes.value.nullIfBlank(),
                useFingerPrint = useFingerPrint.value,
                saveToLocalOnly = saveToLocalOnly.value,
            ),
            lastUsed = now,
            created = now,
            lastModified = now,
            usedCount = 0
        )
        viewModelScope.launch {
            val newState: ScreenState<Password> = try {
                val res = passwordApi.savePassword(
                    passwordCapsule = password.toPasswordCapsule(
                        passwordCryptographyHandler = passwordCryptographyHandler
                    )
                )
                ScreenState.Loaded(result = res.toPassword(passwordCryptographyHandler = passwordCryptographyHandler))
            } catch (e: Exception) {
                e.printStackTrace()
                ScreenState.ApiError.fromException(e = e)
            }
            this@PasswordEditViewModel._screenState.value = newState
        }
    }
}