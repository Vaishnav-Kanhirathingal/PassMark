package easter.egg.passmark.ui.sections.user_edit

import androidx.lifecycle.ViewModel
import easter.egg.passmark.utils.ScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserEditViewModel : ViewModel() {
    private val _masterPasswordText: MutableStateFlow<String> = MutableStateFlow(value = "")
    val masterPasswordText: StateFlow<String> get() = _masterPasswordText
    fun updateMasterPasswordText(value: String) {
        this._masterPasswordText.value = value
    }

    private val _visible: MutableStateFlow<Boolean> = MutableStateFlow(value = true)
    val visible: StateFlow<Boolean> get() = _visible
    fun updateVisibility() {
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
}