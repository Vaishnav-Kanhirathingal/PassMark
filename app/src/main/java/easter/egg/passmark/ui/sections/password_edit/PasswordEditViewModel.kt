package easter.egg.passmark.ui.sections.password_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import easter.egg.passmark.utils.ScreenState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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

    private val _saveToLocalOnly: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val saveToLocalOnly: StateFlow<Boolean> get() = _saveToLocalOnly
    fun updateSaveToLocalOnly(newValue: Boolean) {
        this._saveToLocalOnly.value = newValue
    }

    private val _screenState: MutableStateFlow<ScreenState<Unit>> =
        MutableStateFlow(ScreenState.PreCall())
    val screenState: StateFlow<ScreenState<Unit>> get() = _screenState

    fun savePassword() {
        _screenState.value = ScreenState.Loading()
        viewModelScope.launch {
            val newState: ScreenState<Unit> = try {
                delay(10_000)
                throw Exception("todo")
                TODO()
            } catch (e: Exception) {
                e.printStackTrace()
                ScreenState.ApiError.SomethingWentWrong()
            }
            this@PasswordEditViewModel._screenState.value = newState
        }
    }
}