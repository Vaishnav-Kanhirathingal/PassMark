package easter.egg.passmark.ui.main.change_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import easter.egg.passmark.data.supabase.api.VaultApi
import easter.egg.passmark.utils.ScreenState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    val vaultApi: VaultApi
) : ViewModel() {
    val oldPassword: MutableStateFlow<String> = MutableStateFlow("")
    val newPassword: MutableStateFlow<String> = MutableStateFlow("")
    val newPasswordRepeated: MutableStateFlow<String> = MutableStateFlow("")

    private val _showError: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showError: StateFlow<Boolean> get() = _showError
    fun triggerErrorFlag() {
        this._showError.value = true
    }

    private val _screenState: MutableStateFlow<ScreenState<Unit>> =
        MutableStateFlow(ScreenState.PreCall())
    val screenState: StateFlow<ScreenState<Unit>> get() = _screenState

    private val _currentReEncryptionStates: MutableStateFlow<ReEncryptionStates> =
        MutableStateFlow(value = ReEncryptionStates.entries.first())
    val currentReEncryptionStates: StateFlow<ReEncryptionStates> get() = _currentReEncryptionStates

    fun changePassword(
        isSilent: Boolean
    ) {
        if (!isSilent) {
            _screenState.value = ScreenState.Loading()
        }
        viewModelScope.launch {
            val newState: ScreenState<Unit> = try {
                ReEncryptionStates.entries
                    .subList(
                        fromIndex = currentReEncryptionStates.value.ordinal,
                        toIndex = ReEncryptionStates.entries.lastIndex + 1
                    )
                    .forEach { currentState: ReEncryptionStates ->
                        delay(timeMillis = 800L)
                        performAction(currentState)
                    }
                ScreenState.Loaded(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                ScreenState.ApiError.fromException(e = e)
            }
            this@ChangePasswordViewModel._screenState.value = newState
        }
    }

    private fun performAction(
        reEncryptionStates: ReEncryptionStates
    ) {
        when (reEncryptionStates) {
            ReEncryptionStates.VERIFYING_PASSWORD -> TODO()
            ReEncryptionStates.REMOTE_PASSWORD_DATABASE -> TODO()
            ReEncryptionStates.LOCAL_PASSWORD_DATABASE -> TODO()
            ReEncryptionStates.USER_PUZZLE -> TODO()
            ReEncryptionStates.LOCAL_PASSWORD_PURGING -> TODO()
        }
    }
}

enum class ReEncryptionStates {
    VERIFYING_PASSWORD,
    REMOTE_PASSWORD_DATABASE,
    LOCAL_PASSWORD_DATABASE,
    USER_PUZZLE,
    LOCAL_PASSWORD_PURGING;

    fun getSubtitle(): String {
        return when (this) {
            VERIFYING_PASSWORD -> "Verifying your current password"
            REMOTE_PASSWORD_DATABASE -> "Re-encrypting local password database"
            LOCAL_PASSWORD_DATABASE -> "Re-encrypting remote password database"
            USER_PUZZLE -> "Removing old password details"
            LOCAL_PASSWORD_PURGING -> "Making additional changes"
        }
    }
}