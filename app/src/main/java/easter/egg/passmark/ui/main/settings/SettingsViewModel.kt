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

    private var _currentStage: DeletionStages = DeletionStages.entries.first()
    val currentStage: DeletionStages get() = _currentStage

    fun deleteEverything() {
        _screenState.value = ScreenState.Loading()
        viewModelScope.launch {
            try {
                DeletionStages.entries
                    .subList(
                        fromIndex = currentStage.ordinal,
                        toIndex = DeletionStages.entries.lastIndex + 1
                    )
                    .forEach {
                        this@SettingsViewModel._currentStage = it
                        performTask(deletionStages = it)
                    }
                ScreenState.Loaded(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                ScreenState.ApiError.fromException(e = e)
            }.let { newState: ScreenState<Unit> ->
                this@SettingsViewModel._screenState.value = newState
            }
        }
    }

    private fun performTask(deletionStages: DeletionStages): Unit = when (deletionStages) {
        DeletionStages.LOCAL_PASSWORDS -> TODO("delete local passwords")
        DeletionStages.GLOBAL_PASSWORDS_WITH_VAULTS -> TODO("delete global passwords with vaults")
        DeletionStages.USER_TABLE_ITEM -> TODO("delete user table entry")
        DeletionStages.DELETE_PASSWORD -> TODO("delete password")
        DeletionStages.SUPABASE_USER_DELETE -> TODO("supabase user account delete")
        DeletionStages.SUPABASE_LOGOUT -> TODO("supabase user logout")
    }

}

enum class DeletionStages {
    LOCAL_PASSWORDS,
    GLOBAL_PASSWORDS_WITH_VAULTS,
    USER_TABLE_ITEM,
    DELETE_PASSWORD,
    SUPABASE_USER_DELETE,
    SUPABASE_LOGOUT,
}