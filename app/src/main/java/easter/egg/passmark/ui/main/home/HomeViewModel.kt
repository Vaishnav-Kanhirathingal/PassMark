package easter.egg.passmark.ui.main.home

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import easter.egg.passmark.data.models.Vault
import easter.egg.passmark.data.models.password.PasswordData
import easter.egg.passmark.data.models.password.PasswordSortingOptions
import easter.egg.passmark.data.storage.SettingsDataStore
import easter.egg.passmark.data.storage.database.PasswordDao
import easter.egg.passmark.data.supabase.api.PasswordApi
import easter.egg.passmark.data.supabase.api.VaultApi
import easter.egg.passmark.di.supabase.SupabaseModule
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.testing.PassMarkConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val vaultApi: VaultApi,
    private val passwordApi: PasswordApi,
    private val passwordDao: PasswordDao,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {
    companion object {
        @Composable
        fun getTestViewModel(): HomeViewModel =
            HomeViewModel(
                vaultApi = (VaultApi(supabaseClient = SupabaseModule.mockClient)),
                passwordDao = PasswordDao.getTestingDao(),
                settingsDataStore = SettingsDataStore(context = LocalContext.current),
                passwordApi = PasswordApi(supabaseClient = SupabaseModule.mockClient)
            )
    }

    private val TAG = this::class.simpleName

    //-----------------------------------------------------------------------------vault-id-selected
    private val _vaultIdSelected: MutableStateFlow<Int?> = MutableStateFlow(null)
    val vaultIdSelected: StateFlow<Int?> get() = _vaultIdSelected
    fun updateVaultIdSelected(id: Int?) {
        this._vaultIdSelected.value = id
    }

    //---------------------------------------------------------------------------------pass-sort-opt
    @Composable
    fun getPasswordSortingOption(): State<PasswordSortingOptions> = settingsDataStore
        .getSortingOptionFlow()
        .collectAsState(initial = PasswordSortingOptions.CREATED)

    fun updatePasswordSortingOption(passwordSortingOptions: PasswordSortingOptions) {
        viewModelScope.launch { settingsDataStore.setSortingOption(passwordSortingOptions = passwordSortingOptions) }
    }

    //------------------------------------------------------------------------------------increasing
    @Composable
    fun getIncreasingOrder(): State<Boolean> = settingsDataStore
        .getIsIncreasingOrder()
        .collectAsState(initial = true)

    fun updateIncreasingOrder(asc: Boolean) {
        viewModelScope.launch { settingsDataStore.setIncreasingOrder(increasing = asc) }
    }

    //-----------------------------------------------------------------------------------search-text
    private val _searchText: MutableStateFlow<String?> = MutableStateFlow(null)
    val searchText: StateFlow<String?> get() = _searchText
    fun updateSearchText(str: String?) {
        this._searchText.value = str
    }

    //--------------------------------------------------------------------------password-sheet-state
    private val _passwordSheetState: MutableStateFlow<PasswordData?> = MutableStateFlow(null)
    val passwordSheetState: StateFlow<PasswordData?> get() = _passwordSheetState

    fun openPasswordOptionSheet(passwordData: PasswordData) {
        this._passwordSheetState.value = passwordData
    }

    fun dismissPasswordOptionSheet() {
        this._passwordSheetState.value = null
    }

    //-----------------------------------------------------------------------------password-deletion
    private val _deletePasswordScreenState: MutableStateFlow<ScreenState<PasswordData>> =
        MutableStateFlow(ScreenState.PreCall())

    val deletePasswordScreenState: StateFlow<ScreenState<PasswordData>> get() = _deletePasswordScreenState

    fun deletePassword() {
        this._deletePasswordScreenState.value = ScreenState.Loading()
        viewModelScope.launch {
            this@HomeViewModel._deletePasswordScreenState.value =
                PassMarkConfig.holdForDelay {
                    try {
                        val password =
                            this@HomeViewModel.passwordSheetState.value!!.also { password ->
                                if (password.localId == null && password.cloudId == null) {
                                    throw IllegalStateException("both local and cloud id are null")
                                }
                                password.cloudId?.let { cloudId ->
                                    passwordApi.deletePassword(
                                        passwordId = cloudId
                                    )
                                }
                                password.localId?.let { localId -> passwordDao.deleteById(localId = localId) }
                            }
                        ScreenState.Loaded(result = password)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        ScreenState.ApiError.fromException(e = e)
                    }
                }
        }
    }

    fun resetPasswordDeleteState() {
        this._deletePasswordScreenState.value = ScreenState.PreCall()
    }

    //----------------------------------------------------------------------------vault-dialog-state
    val vaultDialogState: VaultDialogState = VaultDialogState()

    private val _dialogButtonPressed: MutableStateFlow<VaultDialogActionOptions> =
        MutableStateFlow(VaultDialogActionOptions.UPDATE)
    val dialogButtonPressed: StateFlow<VaultDialogActionOptions> get() = _dialogButtonPressed

    fun performVaultAction(
        action: VaultDialogActionOptions
    ) {
        val vault = vaultDialogState.fetchNewVault()
        _dialogButtonPressed.value = action
        vaultDialogState.setScreenState(
            newState = ScreenState.Loading()
        )
        viewModelScope.launch {
            val newState: ScreenState<VaultDialogResult> = PassMarkConfig.holdForDelay(
                task = {
                    try {
                        val receivedVault: Vault = when (action) {
                            VaultDialogActionOptions.UPDATE -> vaultApi.upsert(vault = vault)
                            VaultDialogActionOptions.DELETE -> {
                                val deleted = vaultApi.delete(vault = vault)
                                passwordDao.deleteByVaultId(vaultId = vault.id!!)
                                Log.d(
                                    TAG, "deleted = " +
                                            GsonBuilder().setPrettyPrinting().create()
                                                .toJson(deleted)
                                )
                                deleted
                            }
                        }

                        ScreenState.Loaded(
                            result = VaultDialogResult(
                                vault = receivedVault,
                                action = action
                            )
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        ScreenState.ApiError.fromException(e = e)
                    }
                }
            )

            vaultDialogState.setScreenState(newState = newState)
        }
    }

    val securityPromptState: MutableStateFlow<SecurityPromptState?> = MutableStateFlow(null)
}

class VaultDialogState {
    private val _isVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _text: MutableStateFlow<String> = MutableStateFlow("")
    private val _apiCallState: MutableStateFlow<ScreenState<VaultDialogResult>> =
        MutableStateFlow(ScreenState.PreCall())
    private val _iconChoice: MutableStateFlow<Int> = MutableStateFlow(0)
    private val _vaultId: MutableStateFlow<Int?> = MutableStateFlow(null)

    val isVisible: StateFlow<Boolean> get() = _isVisible
    val text: StateFlow<String> get() = _text
    val apiCallState: StateFlow<ScreenState<VaultDialogResult>> get() = _apiCallState
    val iconChoice: MutableStateFlow<Int> = _iconChoice
    val isAlreadyAVault: Flow<Boolean> get() = _vaultId.map { it != null }

    fun showDialog(
        vault: Vault?
    ) {
        this._isVisible.value = true
        this._text.value = vault?.name ?: ""
        this._iconChoice.value = vault?.iconChoice ?: Vault.iconList.indices.random()
        this._vaultId.value = vault?.id
    }

    fun resetAndDismiss() {
        this._isVisible.value = false
        this._text.value = ""
        this._apiCallState.value = ScreenState.PreCall()
        this._iconChoice.value = 0
        this._vaultId.value = null
    }

    fun setScreenState(newState: ScreenState<VaultDialogResult>) {
        this._apiCallState.value = newState
    }

    fun updateText(text: String) {
        this._text.value = text
    }

    fun updateIconChoice(choice: Int) {
        this._iconChoice.value = choice
    }

    fun fetchNewVault(): Vault {
        return Vault(
            id = _vaultId.value,
            name = text.value,
            iconChoice = iconChoice.value
        )
    }
}

data class VaultDialogResult(
    val vault: Vault,
    val action: VaultDialogActionOptions
)

enum class VaultDialogActionOptions { UPDATE, DELETE }

class SecurityPromptState(
    val password: String,
    val action: PasswordOptionChoices
)

enum class PasswordOptionChoices { EDIT, COPY }
