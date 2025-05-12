package easter.egg.passmark.ui.main.home

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import easter.egg.passmark.data.models.content.PasswordSortingOptions
import easter.egg.passmark.data.models.content.Vault
import easter.egg.passmark.data.storage.database.PasswordDao
import easter.egg.passmark.data.supabase.api.VaultApi
import easter.egg.passmark.di.supabase.SupabaseModule
import easter.egg.passmark.utils.ScreenState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val vaultApi: VaultApi,
    private val passwordDao: PasswordDao
) : ViewModel() {
    // TODO: store sorting preferences
    companion object {
        @Composable
        fun getTestViewModel(): HomeViewModel =
            HomeViewModel(
                vaultApi = (VaultApi(supabaseClient = SupabaseModule.mockClient)),
                passwordDao = PasswordDao.getTestingDao()
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
    private val _passwordSortingOption: MutableStateFlow<PasswordSortingOptions> =
        MutableStateFlow(PasswordSortingOptions.CREATED)
    val passwordSortingOption: StateFlow<PasswordSortingOptions> = _passwordSortingOption
    fun updatePasswordSortingOption(passwordSortingOptions: PasswordSortingOptions) {
        this._passwordSortingOption.value = passwordSortingOptions
    }

    //-------------------------------------------------------------------------------------ascending
    private val _increasingOrder: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val increasingOrder: StateFlow<Boolean> get() = _increasingOrder
    fun updateIncreasingOrder(asc: Boolean) {
        this._increasingOrder.value = asc
    }

    //-----------------------------------------------------------------------------------search-text
    private val _searchText: MutableStateFlow<String?> = MutableStateFlow(null)
    val searchText: StateFlow<String?> get() = _searchText
    fun updateSearchText(str: String?) {
        this._searchText.value = str
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
            val newState: ScreenState<VaultDialogResult> = try {
                val receivedVault: Vault = when (action) {
                    VaultDialogActionOptions.UPDATE -> vaultApi.upsert(vault = vault)
                    VaultDialogActionOptions.DELETE -> {
                        val deleted = vaultApi.delete(vault = vault)
                        passwordDao.deleteByVaultId(vaultId = vault.id!!)
                        Log.d(
                            TAG,
                            "deleted = ${
                                GsonBuilder().setPrettyPrinting().create().toJson(deleted)
                            }"
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
            vaultDialogState.setScreenState(
                newState = newState
            )
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
    val securityChoices: SecurityChoices
)

enum class SecurityChoices {
    BIOMETRICS, MASTER_PASSWORD
}
