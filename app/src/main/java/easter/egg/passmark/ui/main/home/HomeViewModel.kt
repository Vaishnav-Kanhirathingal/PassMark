package easter.egg.passmark.ui.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import easter.egg.passmark.data.models.content.Vault
import easter.egg.passmark.data.supabase.api.VaultApi
import easter.egg.passmark.utils.ScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val vaultApi: VaultApi
) : ViewModel() {
    val vaultDialogState: VaultDialogState = VaultDialogState(
        _isVisible = MutableStateFlow(false),
        _text = MutableStateFlow(""),
        _apiCallState = MutableStateFlow(ScreenState.PreCall())
    )

    fun createNewVault() {
        val vault = Vault(
            name = vaultDialogState.text.value,
            iconChoice = vaultDialogState.iconChoice.value
        )
        viewModelScope.launch {
            val newState: ScreenState<Vault> = try {
                val receivedVault = vaultApi.upsert(
                    vault = vault
                )
                ScreenState.Loaded(result = receivedVault)
            } catch (e: Exception) {
                e.printStackTrace()
                ScreenState.ApiError.fromException(e = e)
            }
            vaultDialogState.setScreenState(
                newState = newState
            )
        }
    }
}

class VaultDialogState(
    private val _isVisible: MutableStateFlow<Boolean> = MutableStateFlow(false),
    private val _text: MutableStateFlow<String> = MutableStateFlow(""),
    private val _apiCallState: MutableStateFlow<ScreenState<Vault>> = MutableStateFlow(ScreenState.PreCall<Vault>()),
    private val _iconChoice: MutableStateFlow<Int> = MutableStateFlow(0)
) {

    val isVisible: StateFlow<Boolean> get() = _isVisible
    val text: StateFlow<String> get() = _text
    val apiCallState: StateFlow<ScreenState<Vault>> get() = _apiCallState
    val iconChoice: MutableStateFlow<Int> = _iconChoice

    fun showDialog() {
        this._isVisible.value = true
    }

    fun resetAndDismiss() {
        this._isVisible.value = false
        this._text.value = ""
        this._apiCallState.value = ScreenState.PreCall()
        this._iconChoice.value = 0
    }

    fun setScreenState(newState: ScreenState<Vault>) {
        this._apiCallState.value = newState
    }

    fun updateText(text: String) {
        this._text.value = text
    }
}