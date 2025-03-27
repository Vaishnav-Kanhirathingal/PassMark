package easter.egg.passmark.ui.sections.user_edit

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import easter.egg.passmark.data.supabase.account.SupabaseAccountHelper
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.security.KeyStoreHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.security.spec.KeySpec
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

@HiltViewModel
class UserEditViewModel @Inject constructor(
    private val supabaseAccountHelper: SupabaseAccountHelper
) : ViewModel() {
    private val TAG = this::class.simpleName

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
            val password = this@UserEditViewModel.masterPasswordText.value
            val authId = this@UserEditViewModel.supabaseAccountHelper.getId()
            Log.d(TAG, "password = $password")
            val newState: ScreenState<UserEditButtonClickResult> = try {
                delay(timeMillis = 3_000) // TODO: remove
                val keyStoreHandler = KeyStoreHandler(authId = authId)
                if (isNewUser) {
                    TODO(
                        "create a new key, " +
                                "create puzzle from key and store user to supabase, " +
                                "save key to storage"
                    )
                } else {
                    TODO(
                        "check if the password solves the puzzle. " +
                                "if yes, save to storage" +
                                "else, set state to password incorrect"
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                ScreenState.ApiError.fromException(e = e)
            }
            this@UserEditViewModel._screenState.value = newState
        }
    }
}

class UserEditButtonClickResult(val passwordIsIncorrect: Boolean)