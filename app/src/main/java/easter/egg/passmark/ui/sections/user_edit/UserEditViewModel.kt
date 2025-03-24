package easter.egg.passmark.ui.sections.user_edit

import androidx.lifecycle.ViewModel
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
}