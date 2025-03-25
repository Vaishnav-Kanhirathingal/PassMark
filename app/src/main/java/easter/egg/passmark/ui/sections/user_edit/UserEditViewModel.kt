package easter.egg.passmark.ui.sections.user_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import easter.egg.passmark.utils.ScreenState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserEditViewModel : ViewModel() {
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

    private val _screenState: MutableStateFlow<ScreenState<UserEditButtonClickResult>> =
        MutableStateFlow(ScreenState.PreCall())
    val screenState: StateFlow<ScreenState<UserEditButtonClickResult>> get() = _screenState

    fun onButtonPress(isNewUser: Boolean) {
        _screenState.value = ScreenState.Loading()
        viewModelScope.launch {
            val newState: ScreenState<UserEditButtonClickResult> = try {
                delay(timeMillis = 10_000) // TODO: remove
                if (isNewUser) {
                    TODO("create a new key and save to storage")
                } else {
                    TODO("check if the password solves the puzzle. if yes, save to storage")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                ScreenState.ApiError.fromException(e = e)
            }
            this@UserEditViewModel._screenState.value = newState
        }
    }
}

class UserEditButtonClickResult(val passwordIsIncorrect:Boolean)