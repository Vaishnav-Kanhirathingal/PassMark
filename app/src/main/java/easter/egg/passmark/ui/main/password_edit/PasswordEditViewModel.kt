package easter.egg.passmark.ui.main.password_edit

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import easter.egg.passmark.data.models.content.Password
import easter.egg.passmark.data.models.content.PasswordData
import easter.egg.passmark.data.supabase.api.PasswordApi
import easter.egg.passmark.utils.ScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordEditViewModel @Inject constructor(
    val passwordApi: PasswordApi
) : ViewModel() {
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
        val now = System.currentTimeMillis()
        val password = Password(
            id = null,
            vaultId = null,
            data = PasswordData(
                title = title.value,
                email = email.value,
                userName = userName.value,
                password = password.value,
                website = website.value,
                notes = notes.value,
                useFingerPrint = useFingerPrint.value,
                saveToLocalOnly = saveToLocalOnly.value,
            ),
            lastUsed = now,
            created = now,
            lastModified = now
        )
        viewModelScope.launch {
            val newState: ScreenState<Unit> = try {
//                passwordApi.savePassword(
//                    password = password
//                )
//                ScreenState.Loaded(result = Unit)
                TODO()
            } catch (e: Exception) {
                e.printStackTrace()
                ScreenState.ApiError.fromException(e = e)
            }
            this@PasswordEditViewModel._screenState.value = newState
        }
    }
}

class ContentErrors(
    val titleEmpty: Boolean,
    val passwordIsEmpty: Boolean,
    val emailFormattingIncorrect: Boolean,
) {
    companion object {
        fun fromData(
            title: String,
            password: String,
            email: String
        ) = ContentErrors(
            titleEmpty = title.isEmpty(),
            passwordIsEmpty = password.isEmpty(),
            emailFormattingIncorrect = !email.let {
                it.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(it).matches()
            }
        )
    }

    val requirementsMet: Boolean get() = !(titleEmpty || passwordIsEmpty || emailFormattingIncorrect)

    private var toastShown = false

    fun getToastText(): String? =
        if (requirementsMet) null
        else
            (
                    (if (titleEmpty) "Title empty, " else "") +
                            (if (emailFormattingIncorrect) "Email formatting incorrect, " else "") +
                            (if (passwordIsEmpty) "Password empty, " else "")
                    ).dropLast(n = 2)
}