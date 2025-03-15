package easter.egg.passmark.ui.sections.login

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import easter.egg.passmark.utils.ScreenState
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.asDeferred

class LoginViewModel : ViewModel() {
    private val TAG = this::class.simpleName

    private val _screenState: MutableState<ScreenState<Unit>> =
        mutableStateOf(ScreenState.PreCall())
    val screenState: State<ScreenState<Unit>> get() = _screenState

    fun login(credentialResponse: GetCredentialResponse) {
        this@LoginViewModel._screenState.value = ScreenState.Loading()
        viewModelScope.launch {
            try {
                val googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(data = credentialResponse.credential.data)
                val credential =
                    GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                Firebase.auth.signInWithCredential(credential)
                    .addOnSuccessListener { result ->
                        Log.d(TAG, "email = ${result.user?.email}")
                        this@LoginViewModel._screenState.value = ScreenState.Loaded(result = Unit)
                    }
                    .addOnFailureListener { exception ->
                        exception.printStackTrace()
                        this@LoginViewModel._screenState.value =
                            ScreenState.ApiError.SomethingWentWrong()
                    }
            } catch (e: Exception) {
                e.printStackTrace()
                this@LoginViewModel._screenState.value = ScreenState.ApiError.SomethingWentWrong()
            }
        }
    }
}