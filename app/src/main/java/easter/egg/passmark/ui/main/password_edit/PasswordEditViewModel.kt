package easter.egg.passmark.ui.main.password_edit

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import easter.egg.passmark.data.models.content.Password
import easter.egg.passmark.data.models.content.Vault
import easter.egg.passmark.data.models.content.password.PasswordData
import easter.egg.passmark.data.storage.database.PasswordDao
import easter.egg.passmark.data.supabase.api.PasswordApi
import easter.egg.passmark.di.supabase.SupabaseModule
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.extensions.nullIfBlank
import easter.egg.passmark.utils.security.PasswordCryptographyHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordEditViewModel @Inject constructor(
    val passwordApi: PasswordApi,
    val passwordDao: PasswordDao
) : ViewModel() {
    private val TAG = this::class.simpleName

    companion object {
        @Composable
        fun getTestViewModel(): PasswordEditViewModel {
            return PasswordEditViewModel(
                passwordApi = PasswordApi(supabaseClient = SupabaseModule.mockClient),
                passwordDao = PasswordDao.getDao()
            )
        }
    }

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
            this.saveToLocalOnly.value = password.localId != null
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

    /** tracks if delete has been completed in case of failure to avoid further issues */
    private var _deleteCompleted: Boolean = false
    fun savePassword(
        passwordCryptographyHandler: PasswordCryptographyHandler
    ) {
        _screenState.value = ScreenState.Loading()
        val now = System.currentTimeMillis()
        val saveToStorage = this.saveToLocalOnly.value

        val passwordCapsuleToSave = Password(
            cloudId = if (saveToStorage) null else _oldPassword?.cloudId,
            localId = if (saveToStorage) _oldPassword?.localId else null,
            vaultId = selectedVault.value?.id,
            data = PasswordData(
                title = title.value,
                email = email.value.nullIfBlank(),
                userName = userName.value.nullIfBlank(),
                password = password.value,
                website = website.value.nullIfBlank(),
                notes = notes.value.nullIfBlank(),
                useFingerPrint = useFingerPrint.value,
            ),
            lastUsed = _oldPassword?.lastUsed ?: now,
            created = _oldPassword?.created ?: now,
            lastModified = now,
            usedCount = 0,
        ).toPasswordCapsule(passwordCryptographyHandler = passwordCryptographyHandler)

        viewModelScope.launch {
            // TODO: test everything
            val newState: ScreenState<Password> = try {

                val savedPasswordCapsule = if (saveToStorage) {
                    _oldPassword?.cloudId
                        ?.takeUnless { _deleteCompleted }
                        ?.let { id -> // deletes cloud version if switching to local
                            Log.d(TAG, "deleting cloud version")
                            passwordApi.deletePassword(passwordId = id)
                            _deleteCompleted = true
                        }

                    val id = passwordDao.upsert(passwordCapsule = passwordCapsuleToSave).toInt()
                    passwordDao.getById(id = id)
                } else {
                    _oldPassword?.localId
                        ?.takeUnless { _deleteCompleted }
                        ?.let { localId -> // deletes local version if switching to cloud
                            passwordDao.deleteById(localId = localId)
                            _deleteCompleted = true
                        }
                    passwordApi.savePassword(passwordCapsule = passwordCapsuleToSave)
                }

                val res = savedPasswordCapsule.toPassword(
                    passwordCryptographyHandler = passwordCryptographyHandler
                )
                this@PasswordEditViewModel._deleteCompleted = false
                ScreenState.Loaded(result = res)
            } catch (e: Exception) {
                e.printStackTrace()
                ScreenState.ApiError.fromException(e = e)
            }
            this@PasswordEditViewModel._screenState.value = newState
        }
    }
}