package easter.egg.passmark.ui.sections.loader

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoaderViewModel @Inject constructor(
    val supabaseClient: SupabaseClient
) : ViewModel() {
    private val TAG = this::class.simpleName

    private val _hasUser: MutableState<Boolean?> = mutableStateOf(null)
    val hasUser: State<Boolean?> get() = _hasUser

    init {
        viewModelScope.launch {
            supabaseClient.auth.sessionStatus.collect {
                when (it) {
                    is SessionStatus.Authenticated -> true
                    SessionStatus.Initializing -> null
                    is SessionStatus.RefreshFailure, is SessionStatus.NotAuthenticated -> false
                }.let { newValue: Boolean? ->
                    this@LoaderViewModel._hasUser.value = newValue
                }
            }
        }
    }
}