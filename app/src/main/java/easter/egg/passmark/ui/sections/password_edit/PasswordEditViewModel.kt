package easter.egg.passmark.ui.sections.password_edit

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PasswordEditViewModel : ViewModel() {
    private val _title: MutableStateFlow<String> = MutableStateFlow("")
    val title: StateFlow<String> get() = _title
    fun updateTitle(newValue: String) {
        this._title.value = newValue
    }

    private val _email: MutableStateFlow<String> = MutableStateFlow("")
    val email: StateFlow<String> get() = _email
    fun updateEmail(newValue: String) {
        this._email.value = newValue
    }

    private val _userName: MutableStateFlow<String> = MutableStateFlow("")
    val userName: StateFlow<String> get() = _userName
    fun updateUserName(newValue: String) {
        this._userName.value = newValue
    }

    private val _password: MutableStateFlow<String> = MutableStateFlow("")
    val password: StateFlow<String> get() = _password
    fun updatePassword(newValue: String) {
        this._password.value = newValue
    }

    private val _website: MutableStateFlow<String> = MutableStateFlow("")
    val website: StateFlow<String> get() = _website
    fun updateWebsite(newValue: String) {
        this._website.value = newValue
    }

    private val _notes: MutableStateFlow<String> = MutableStateFlow("")
    val notes: StateFlow<String> get() = _notes
    fun updateNotes(newValue: String) {
        this._notes.value = newValue
    }

    private val _useFingerPrint: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val useFingerPrint: StateFlow<Boolean> get() = _useFingerPrint
    fun updateUseFingerPrint(newValue: Boolean) {
        this._useFingerPrint.value = newValue
    }
}