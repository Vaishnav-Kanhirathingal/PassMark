package easter.egg.passmark.ui.main.change_password

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import easter.egg.passmark.data.supabase.api.VaultApi
import easter.egg.passmark.utils.ScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    val vaultApi: VaultApi
) : ViewModel() {
    val oldPassword: MutableStateFlow<String> = MutableStateFlow("")
    val newPassword: MutableStateFlow<String> = MutableStateFlow("")
    val newPasswordRepeated: MutableStateFlow<String> = MutableStateFlow("")

    private val _changePasswordCallState: MutableStateFlow<ScreenState<Unit>> =
        MutableStateFlow(ScreenState.PreCall())
    val changePasswordCallState: StateFlow<ScreenState<Unit>?> get() = _changePasswordCallState


    fun changePassword() {
        TODO()
    }
}