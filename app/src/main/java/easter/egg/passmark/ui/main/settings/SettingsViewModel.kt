package easter.egg.passmark.ui.main.settings

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import easter.egg.passmark.data.storage.PassMarkDataStore
import easter.egg.passmark.data.storage.SettingsDataStore
import easter.egg.passmark.data.storage.database.PasswordDao
import easter.egg.passmark.data.supabase.account.SupabaseAccountHelper
import easter.egg.passmark.data.supabase.api.UserApi
import easter.egg.passmark.di.supabase.SupabaseModule
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.testing.TestTags
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

    companion object {
        const val LOOP_DELAY = 1_000L

        @Composable
        fun getTestViewModel(): SettingsViewModel {
            val client = SupabaseModule.mockClient
            return SettingsViewModel(
                context = LocalContext.current,
                settingsDataStore = SettingsDataStore(context = LocalContext.current),
                supabaseAccountHelper = SupabaseAccountHelper(supabaseClient = client),
                userApi = UserApi(supabaseClient = client),
                passwordDao = PasswordDao.getTestingDao()
            )
        }
    }

    //----------------------------------------------------------------------------------------------deletion-state
    //----------------------------------------------------------------------------------screen-state
    private val _deletionScreenState: MutableStateFlow<ScreenState<Unit>?> =
        MutableStateFlow(null)
    val deletionScreenState: StateFlow<ScreenState<Unit>?> get() = _deletionScreenState

    fun setResetConfirmationDialogVisibility(visible: Boolean) {
        _deletionScreenState.value = ScreenState.PreCall<Unit>().takeIf { visible }
    }

    //-----------------------------------------------------------------------------------------stage
    private val _currentStage: MutableStateFlow<DeletionStages> =
        MutableStateFlow(DeletionStages.entries.first())
    val currentStage: StateFlow<DeletionStages> get() = _currentStage

    //------------------------------------------------------------------------------------------call
    fun deleteEverything(
        silent: Boolean
    ) {
        if (!silent) {
            _deletionScreenState.value = ScreenState.Loading()
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
                        delay(timeMillis = LOOP_DELAY)
                        performDeletionTask(deletionStages = it)
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
                this@SettingsViewModel._deletionScreenState.value = newState
            }
        }
    }

    private suspend fun performDeletionTask(
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

    //---------------------------------------------------------------------------------------log-out
    private val _logoutScreenState: MutableStateFlow<ScreenState<Unit>> =
        MutableStateFlow(ScreenState.PreCall())
    val logoutScreenState: StateFlow<ScreenState<Unit>> get() = _logoutScreenState

    fun logout() {
        this._logoutScreenState.value = ScreenState.Loading()
        viewModelScope.launch {
            this@SettingsViewModel._logoutScreenState.value = try {
                TestTags.holdForDelay(
                    task = {
                        val dataStore =
                            PassMarkDataStore(
                                context = context,
                                authId = supabaseAccountHelper.getId()
                            )
                        supabaseAccountHelper.logout()
                        dataStore.resetPassword()
                        ScreenState.Loaded(result = Unit)
                    }
                )
            } catch (e: Exception) {
                e.printStackTrace()
                ScreenState.ApiError.fromException(e = e)
            }
        }
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