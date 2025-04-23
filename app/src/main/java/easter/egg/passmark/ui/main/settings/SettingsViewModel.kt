package easter.egg.passmark.ui.main.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import easter.egg.passmark.data.storage.SettingsDataStore
import easter.egg.passmark.utils.ScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    val settingsDataStore: SettingsDataStore
) : ViewModel() {
    private val _screenState: MutableStateFlow<ScreenState<Unit>> =
        MutableStateFlow(ScreenState.PreCall())
    val screenState: StateFlow<ScreenState<Unit>> get() = _screenState

    fun deleteEverything(
        startPoint: DeletionStages
    ) {
        _screenState.value = ScreenState.Loading()
        viewModelScope.launch {
            try {
                fun check(deletionStages: DeletionStages): Boolean {
                    return startPoint.ordinal <= deletionStages.ordinal
                }
                when {
                    (startPoint.ordinal <= DeletionStages.LOCAL_PASSWORDS.ordinal) -> {
                        TODO("delete local passwords")
                    }

                    (startPoint.ordinal <= DeletionStages.GLOBAL_PASSWORDS_WITH_VAULTS.ordinal) -> {
                        TODO("delete global passwords with vaults")
                    }

                    (startPoint.ordinal <= DeletionStages.USER_TABLE_ITEM.ordinal) -> {
                        TODO("delete user table entry")
                    }

                    (startPoint.ordinal <= DeletionStages.SUPABASE_USER_DELETE.ordinal) -> {
                        TODO("supabase user account delete")
                    }

                    (startPoint.ordinal <= DeletionStages.SUPABASE_LOGOUT.ordinal) -> {
                        TODO("supabase user logout")
                    }

                    (startPoint.ordinal <= DeletionStages.FINISHED_PROCESS.ordinal) -> {
                        TODO("success")
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

enum class DeletionStages {
    LOCAL_PASSWORDS,
    GLOBAL_PASSWORDS_WITH_VAULTS,
    USER_TABLE_ITEM,
    SUPABASE_USER_DELETE,
    SUPABASE_LOGOUT,
    FINISHED_PROCESS
}