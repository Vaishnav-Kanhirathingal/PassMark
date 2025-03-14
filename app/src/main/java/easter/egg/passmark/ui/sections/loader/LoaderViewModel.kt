package easter.egg.passmark.ui.sections.loader

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoaderViewModel : ViewModel() {
    private val TAG = this::class.simpleName

    private val _hasUser: MutableState<Boolean?> = mutableStateOf(null)
    val hasUser: State<Boolean?> get() = _hasUser

    init {
        Firebase.auth.addAuthStateListener {
            Log.d(TAG, "current user = ${if (it.currentUser == null) "null" else "active"}")
//            this._hasUser.value = (it.currentUser != null)
        }
    }
}