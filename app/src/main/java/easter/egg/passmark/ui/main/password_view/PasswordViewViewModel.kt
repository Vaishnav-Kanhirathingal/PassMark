package easter.egg.passmark.ui.main.password_view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import easter.egg.passmark.data.models.content.Password
import easter.egg.passmark.data.storage.database.PasswordDao
import easter.egg.passmark.data.supabase.api.PasswordApi
import easter.egg.passmark.utils.ScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordViewViewModel @Inject constructor(
    val passwordApi: PasswordApi,
    val passwordDao: PasswordDao
) : ViewModel() {
    private val _deleteDialogVisibility: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val deleteDialogVisibility: StateFlow<Boolean> = _deleteDialogVisibility

    fun setDeleteDialogVisibility(visibility: Boolean) {
        _deleteDialogVisibility.value = visibility
    }

    private val _deleteDialogState: MutableStateFlow<ScreenState<Unit>> =
        MutableStateFlow(ScreenState.PreCall())
    val deleteDialogState: StateFlow<ScreenState<Unit>> = _deleteDialogState

    fun delete(password: Password) {
        _deleteDialogState.value = ScreenState.Loading()
        viewModelScope.launch {
            try {
                if (password.cloudId != null) {
                    passwordApi.deletePassword(passwordId = password.cloudId)
                } else {
                    passwordDao.deleteById(localId = password.localId!!)
                }
                ScreenState.Loaded(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                ScreenState.ApiError.fromException(e = e)
            }.let { newState: ScreenState<Unit> -> _deleteDialogState.value = newState }
        }
    }
}