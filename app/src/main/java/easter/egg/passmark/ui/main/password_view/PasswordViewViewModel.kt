package easter.egg.passmark.ui.main.password_view

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import easter.egg.passmark.data.models.password.PasswordData
import easter.egg.passmark.data.storage.database.PasswordDao
import easter.egg.passmark.data.supabase.api.PasswordApi
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.security.PasswordCryptographyHandler
import easter.egg.passmark.utils.testing.PassMarkConfig
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

    fun delete(passwordData: PasswordData) {
        _deleteDialogState.value = ScreenState.Loading()
        viewModelScope.launch {
            _deleteDialogState.value = PassMarkConfig.holdForDelay(
                task = {
                    try {
                        if (passwordData.cloudId != null) {
                            passwordApi.deletePassword(passwordId = passwordData.cloudId)
                        } else {
                            passwordDao.deleteById(localId = passwordData.localId!!)
                        }
                        ScreenState.Loaded(Unit)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        ScreenState.ApiError.fromException(e = e)
                    }
                }
            )
        }
    }

    private var _lastUpdatedTimeBeforeCall: Long? = null
    val lastUpdatedTimeBeforeCall: Long? get() = _lastUpdatedTimeBeforeCall

    private var _updated = false
    suspend fun updateUsageStats(
        passwordData: PasswordData,
        passwordCryptographyHandler: PasswordCryptographyHandler,
        onComplete: (PasswordData) -> Unit
    ) {
        if (_updated) {
            Log.d(TAG, "usage has been updated")
        } else {
            this._lastUpdatedTimeBeforeCall = passwordData.lastUsed
            val now = System.currentTimeMillis()
            try {
                val newPasswordData: PasswordData = when {
                    passwordData.localId != null -> {
                        passwordDao.updateUsageStat(
                            lastUsed = now,
                            usedCount = passwordData.usedCount + 1,
                            localId = passwordData.localId
                        )

                        passwordDao.getById(id = passwordData.localId)

                    }

                    passwordData.cloudId != null -> {
                        passwordApi.updateUsageStat(
                            lastUsed = now,
                            usedCount = passwordData.usedCount + 1,
                            cloudId = passwordData.cloudId
                        )
                    }

                    else -> throw IllegalStateException("either the cloud or local ID should be non-null")
                }.toPassword(passwordCryptographyHandler = passwordCryptographyHandler)

                onComplete(newPasswordData)
                _updated = true
                Log.d(TAG, "password usage updated")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val _authenticatedActionOnHold: MutableStateFlow<AuthenticatedActions?> =
        MutableStateFlow(null)
    val authenticatedActionOnHold: StateFlow<AuthenticatedActions?> get() = _authenticatedActionOnHold

    fun performAuthenticatedAction(authenticatedAction: AuthenticatedActions) {
        this._authenticatedActionOnHold.value = authenticatedAction
    }

    fun clearAuthenticatedActionState() {
        this._authenticatedActionOnHold.value = null
    }
}

enum class AuthenticatedActions { EDIT, COPY_PASSWORD, VIEW_HISTORY, DELETE_PASSWORD }