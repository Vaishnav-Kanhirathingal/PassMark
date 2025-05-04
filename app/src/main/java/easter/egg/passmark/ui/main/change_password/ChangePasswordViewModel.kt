package easter.egg.passmark.ui.main.change_password

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import easter.egg.passmark.data.supabase.api.VaultApi
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    val vaultApi: VaultApi
) : ViewModel() {

}