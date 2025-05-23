package easter.egg.passmark.ui.main.password_view

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import easter.egg.passmark.data.models.content.password.Password
import easter.egg.passmark.data.storage.database.PasswordDao
import easter.egg.passmark.data.supabase.api.PasswordApi
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.security.PasswordCryptographyHandler
import easter.egg.passmark.utils.testing.TestTags
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordViewViewModel @Inject constructor(
    val passwordApi: PasswordApi,
    val passwordDao: PasswordDao
) : ViewModel() {
    private val TAG = this::class.simpleName
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
            delay(TestTags.TIME_OUT)
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

    private var _updated = false
    suspend fun updateUsageStats(
        password: Password,
        passwordCryptographyHandler: PasswordCryptographyHandler,
        onComplete: (Password) -> Unit
    ) {
        if (_updated) {
            Log.d(TAG, "usage has been updated")
        } else {
            val now = System.currentTimeMillis()
            try {
                val newPassword: Password = when {
                    password.localId != null -> {
                        passwordDao.updateUsageStat(
                            lastUsed = now,
                            usedCount = password.usedCount + 1,
                            localId = password.localId
                        )

                        passwordDao.getById(id = password.localId)

                    }

                    password.cloudId != null -> {
                        passwordApi.updateUsageStat(
                            lastUsed = now,
                            usedCount = password.usedCount + 1,
                            cloudId = password.cloudId
                        )
                    }

                    else -> throw IllegalStateException("either the cloud or local ID should be non-null")
                }.toPassword(passwordCryptographyHandler = passwordCryptographyHandler)

                onComplete(newPassword)
                _updated = true
                Log.d(TAG, "password usage updated")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}