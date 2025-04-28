package easter.egg.passmark.ui.main.settings

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import easter.egg.passmark.data.storage.PassMarkDataStore
import easter.egg.passmark.data.storage.SettingsDataStore
import easter.egg.passmark.data.storage.database.PasswordDao
import easter.egg.passmark.data.supabase.account.SupabaseAccountHelper
import easter.egg.passmark.data.supabase.api.UserApi
import easter.egg.passmark.utils.ScreenState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    val settingsDataStore: SettingsDataStore,
    private val supabaseAccountHelper: SupabaseAccountHelper,
    private val userApi: UserApi,
    private val passwordDao: PasswordDao
) : ViewModel() {
    private val TAG = this::class.simpleName

    private val _screenState: MutableStateFlow<ScreenState<Unit>> =
        MutableStateFlow(ScreenState.PreCall())
    val screenState: StateFlow<ScreenState<Unit>> get() = _screenState

    private val _currentStage: MutableStateFlow<DeletionStages> =
        MutableStateFlow(DeletionStages.entries.first())
    val currentStage: StateFlow<DeletionStages> get() = _currentStage

    fun deleteEverything(
        silent: Boolean
    ) {
        if (!silent) {
            _screenState.value = ScreenState.Loading()
        }
        viewModelScope.launch {
            try {
                DeletionStages.entries
                    .subList(
                        fromIndex = currentStage.value.ordinal,
                        toIndex = DeletionStages.entries.lastIndex + 1
                    )
                    .forEach {
                        this@SettingsViewModel._currentStage.value = it
                        Log.d(TAG, "about to perform ${it.name}")
                        delay(timeMillis = 800L)
                        performTask(deletionStages = it)
                    }
                ScreenState.Loaded(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                ScreenState.ApiError.SomethingWentWrong(
                    alternateToastMessage = when (ScreenState.ApiError.fromException<Unit>(e = e)) {
                        is ScreenState.ApiError.NetworkError -> "Network error. Retrying..."
                        is ScreenState.ApiError.SomethingWentWrong -> "Something went wrong. Retrying..."
                    }
                )
            }.let { newState: ScreenState<Unit> ->
                this@SettingsViewModel._screenState.value = newState
            }
        }
    }

    private suspend fun performTask(
        deletionStages: DeletionStages
    ): Any = when (deletionStages) {
        DeletionStages.LOCAL_PASSWORDS -> passwordDao.deleteAll()
        DeletionStages.USER_TABLE_ITEM -> userApi.deleteUser(uid = supabaseAccountHelper.getId())
        DeletionStages.DELETE_MASTER_PASSWORD -> {
            PassMarkDataStore(
                context = context,
                authId = supabaseAccountHelper.getId()
            ).resetPassword()
        }

        DeletionStages.SUPABASE_LOGOUT -> supabaseAccountHelper.logout()
    }
}

enum class DeletionStages {
    LOCAL_PASSWORDS,
    USER_TABLE_ITEM,
    DELETE_MASTER_PASSWORD,
    SUPABASE_LOGOUT;

    fun getTaskMessage(): String {
        val mainMessage = when (this) {
            LOCAL_PASSWORDS -> "Deleting Locally saved passwords"
            USER_TABLE_ITEM -> "Deleting remote database"
            DELETE_MASTER_PASSWORD -> "Deleting App Account data"
            SUPABASE_LOGOUT -> "Logging out of app"
        }
        return "Currently at stage ${this.ordinal + 1}/${DeletionStages.entries.size},\n$mainMessage"
    }
}