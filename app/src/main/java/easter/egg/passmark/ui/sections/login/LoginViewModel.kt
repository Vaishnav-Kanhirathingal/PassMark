package easter.egg.passmark.ui.sections.login

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

class LoginViewModel : ViewModel() {
    private val _screenState: MutableState<ScreenState<Unit>> =
        mutableStateOf(ScreenState.PreCall())

    val screenState: State<ScreenState<Unit>> get() = _screenState

    fun login(credentialResponse: GetCredentialResponse) {
        viewModelScope.launch {
            this@LoginViewModel._screenState.value = ScreenState.Loading()
            try {
                val googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(data = credentialResponse.credential.data)
                val credential =
                    GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                Firebase.auth.signInWithCredential(credential)
                    .addOnSuccessListener { result ->
                        this@LoginViewModel._screenState.value = ScreenState.Loaded(result = Unit)
                    }
                    .addOnFailureListener { exception ->
                        this@LoginViewModel._screenState.value =
                            ScreenState.ApiError.SomethingWentWrong()
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}