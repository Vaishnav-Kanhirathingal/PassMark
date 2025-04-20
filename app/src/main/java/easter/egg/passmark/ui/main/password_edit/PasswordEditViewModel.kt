package easter.egg.passmark.ui.main.password_edit

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import easter.egg.passmark.data.models.content.Password
import easter.egg.passmark.data.models.content.PasswordData
import easter.egg.passmark.data.models.content.Vault
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
    private val TAG = this::class.simpleName

    //-------------------------------------------------------------------------------------UI-States
    val title: MutableStateFlow<String> = MutableStateFlow("")
    val email: MutableStateFlow<String> = MutableStateFlow("")
    val userName: MutableStateFlow<String> = MutableStateFlow("")
    val password: MutableStateFlow<String> = MutableStateFlow("")
    val website: MutableStateFlow<String> = MutableStateFlow("")
    val notes: MutableStateFlow<String> = MutableStateFlow("")
    val useFingerPrint: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val saveToLocalOnly: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val _showFieldError: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showFieldError: StateFlow<Boolean> get() = _showFieldError
    fun updateShowFieldError() {
        if (!this._showFieldError.value) {
            this._showFieldError.value = true
        }
    }

    private val _selectedVault: MutableStateFlow<Vault?> = MutableStateFlow(null)
    val selectedVault: StateFlow<Vault?> get() = _selectedVault
    fun updateSelectedVault(vault: Vault?) {
        this._selectedVault.value = vault
    }

    //---------------------------------------------------------------------------------loaded-values
    private var _oldPassword: Password? = null
    private var _loaded = false

    fun loadInitialData(
        vault: Vault?
    ) {
        if (_loaded) {
            Log.d(TAG, "vault already loaded")
        } else {
            this._selectedVault.value = vault
            _loaded = true
        }
    }

    fun loadInitialData(
        password: Password,
        vault: Vault?
    ) {
        if (_loaded) {
            Log.d(TAG, "password already loaded")
        } else {
            this.title.value = password.data.title
            this.email.value = password.data.email ?: ""
            this.userName.value = password.data.userName ?: ""
            this.password.value = password.data.password
            this.website.value = password.data.website ?: ""
            this.notes.value = password.data.notes ?: ""
            this.useFingerPrint.value = password.data.useFingerPrint
            this.saveToLocalOnly.value = password.data.saveToLocalOnly
            this._selectedVault.value = vault
            this._oldPassword = password
            _loaded = true
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
            id = _oldPassword?.id,
            vaultId = selectedVault.value?.id,
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
            lastUsed = _oldPassword?.lastUsed ?: now,
            created = _oldPassword?.created ?: now,
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