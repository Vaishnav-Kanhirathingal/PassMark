package easter.egg.passmark.ui.main.password_edit

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import easter.egg.passmark.data.models.Vault
import easter.egg.passmark.data.models.password.PasswordData
import easter.egg.passmark.data.models.password.sensitive.SensitiveContent
import easter.egg.passmark.data.storage.SettingsDataStore
import easter.egg.passmark.data.storage.database.PasswordDao
import easter.egg.passmark.data.supabase.api.PasswordApi
import easter.egg.passmark.di.supabase.SupabaseModule
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.extensions.nullIfBlank
import easter.egg.passmark.utils.security.PasswordCryptographyHandler
import easter.egg.passmark.utils.testing.PassMarkConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordEditViewModel @Inject constructor(
    private val passwordApi: PasswordApi,
    private val passwordDao: PasswordDao,
    private val dataStore: SettingsDataStore
) : ViewModel() {
    private val TAG = this::class.simpleName

    companion object {
        @Composable
        fun getTestViewModel(): PasswordEditViewModel {
            return PasswordEditViewModel(
                passwordApi = PasswordApi(supabaseClient = SupabaseModule.mockClient),
                passwordDao = PasswordDao.getTestingDao(),
                dataStore = SettingsDataStore(LocalContext.current)
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

    private val _vaultSelectionSheetVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val vaultSelectionSheetVisible: StateFlow<Boolean> get() = _vaultSelectionSheetVisible
    fun showVaultSelectionSheet() {
        this._vaultSelectionSheetVisible.value = true
    }

    fun hideVaultSelectionSheet() {
        this._vaultSelectionSheetVisible.value = false
    }

    // TODO: on account reset, only delete user associated password room data
    //---------------------------------------------------------------------------------loaded-values
    private var _oldPasswordData: PasswordData? = null
    private var _loaded = false

    fun loadInitialData(
        passwordData: PasswordData?,
        vault: Vault?
    ) {
        if (_loaded) {
            Log.d(TAG, "password already loaded")
        } else {
            if (passwordData == null) {
                viewModelScope.launch {
                    this@PasswordEditViewModel.useFingerPrint.value =
                        dataStore.getBiometricEnabledFlow().first()
                    this@PasswordEditViewModel.saveToLocalOnly.value =
                        dataStore.getOfflineStorageFlow().first()
                }
            } else {
                this.title.value = passwordData.data.title
                this.email.value = passwordData.data.email ?: ""
                this.userName.value = passwordData.data.userName ?: ""
                this.password.value = passwordData.data.password
                this.website.value = passwordData.data.website ?: ""
                this.notes.value = passwordData.data.notes ?: ""
                this.useFingerPrint.value = passwordData.data.useFingerPrint
                this.saveToLocalOnly.value = passwordData.localId != null
                this._oldPasswordData = passwordData
            }
            this._selectedVault.value = vault
            _loaded = true
        }
    }
    //-----------------------------------------------------------------------------------------state

    /** result should be the title of the password stored */
    private val _screenState: MutableStateFlow<ScreenState<PasswordData>> =
        MutableStateFlow(ScreenState.PreCall())
    val screenState: StateFlow<ScreenState<PasswordData>> get() = _screenState

    /** tracks if delete has been completed in case of failure to avoid further issues */
    private var _deleteCompleted: Boolean = false
    fun savePassword(
        passwordCryptographyHandler: PasswordCryptographyHandler
    ) {
        _screenState.value = ScreenState.Loading()
        val now = System.currentTimeMillis()
        val saveToStorage = this.saveToLocalOnly.value

        val passwordDataCapsuleToSave = PasswordData(
            cloudId = if (saveToStorage) null else _oldPasswordData?.cloudId,
            localId = if (saveToStorage) _oldPasswordData?.localId else null,
            vaultId = selectedVault.value?.id,
            data = SensitiveContent(
                title = title.value,
                email = email.value.nullIfBlank(),
                userName = userName.value.nullIfBlank(),
                password = password.value,
                website = website.value.nullIfBlank(),
                notes = notes.value.nullIfBlank(),
                useFingerPrint = useFingerPrint.value,
                passwordHistory = this._oldPasswordData?.let {
                    if (it.data.password != password.value) {
                        it.data.passwordHistory.toMutableList().apply {
                            this.add(element = it.currentPasswordAsPasswordHistory())
                        }
                    } else {
                        it.data.passwordHistory
                    }
                } ?: listOf()
            ),
            lastUsed = _oldPasswordData?.lastUsed ?: now,
            created = _oldPasswordData?.created ?: now,
            lastModified = now,
            usedCount = 0,
        ).toPasswordCapsule(passwordCryptographyHandler = passwordCryptographyHandler)

        viewModelScope.launch {
            val newState: ScreenState<PasswordData> = PassMarkConfig.holdForDelay(
                task = {
                    try {
                        val savedPasswordCapsule = if (saveToStorage) {
                            _oldPasswordData?.cloudId
                                ?.takeUnless { _deleteCompleted }
                                ?.let { id -> // deletes cloud version if switching to local
                                    Log.d(TAG, "deleting cloud version")
                                    passwordApi.deletePassword(passwordId = id)
                                    _deleteCompleted = true
                                }

                            val id =
                                passwordDao.upsert(passwordCapsule = passwordDataCapsuleToSave)
                                    .toInt()
                            passwordDao.getById(id = id)
                        } else {
                            _oldPasswordData?.localId
                                ?.takeUnless { _deleteCompleted }
                                ?.let { localId -> // deletes local version if switching to cloud
                                    passwordDao.deleteById(localId = localId)
                                    _deleteCompleted = true
                                }
                            passwordApi.savePassword(passwordCapsule = passwordDataCapsuleToSave)
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
                }
            )
            this@PasswordEditViewModel._screenState.value = newState
        }
    }
}