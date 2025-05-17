package easter.egg.passmark.ui.auth

import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {
    private var _passwordJustUsed: Boolean = false
    val passwordJustUsed: Boolean get() = _passwordJustUsed
    fun updatePasswordUsed() {
        this._passwordJustUsed = true
    }
}