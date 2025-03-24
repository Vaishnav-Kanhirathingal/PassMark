package easter.egg.passmark.ui.sections.login

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import easter.egg.passmark.utils.ScreenState
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.exceptions.HttpRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val supabaseClient: SupabaseClient
) : ViewModel() {
    private val TAG = this::class.simpleName

    private val _screenState: MutableState<ScreenState<Unit>> =
        mutableStateOf(ScreenState.PreCall())
    val screenState: State<ScreenState<Unit>> get() = _screenState

    fun login(credentialResponse: GetCredentialResponse) {
        this@LoginViewModel._screenState.value = ScreenState.Loading()
        viewModelScope.launch {
            val newState: ScreenState<Unit> = try {
                Log.d(TAG, "delay started")
                val googleIdToken =
                    GoogleIdTokenCredential.createFrom(data = credentialResponse.credential.data).idToken
                supabaseClient.auth.signInWith(
                    provider = IDToken,
                    config = {
                        this.idToken = googleIdToken
                        this.provider = Google
                    }
                )
                ScreenState.Loaded(result = Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                when (e) {
                    is HttpRequestTimeoutException, is HttpRequestException -> ScreenState.ApiError.NetworkError()
                    else -> ScreenState.ApiError.SomethingWentWrong()
                }
            }
            this@LoginViewModel._screenState.value = newState
        }
    }
}