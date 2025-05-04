package easter.egg.passmark.ui.main.change_password

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import easter.egg.passmark.data.supabase.api.VaultApi
import easter.egg.passmark.di.supabase.SupabaseModule
import easter.egg.passmark.utils.annotation.MobilePreview

object ChangePasswordScreen {
    @Composable
    fun Screen(
        modifier: Modifier,
        changePasswordViewModel: ChangePasswordViewModel
    ) {
        TODO()
    }
}

@Composable
@MobilePreview
fun ChangePasswordScreenPreview() {
    ChangePasswordScreen.Screen(
        modifier = Modifier.fillMaxSize(),
        changePasswordViewModel = ChangePasswordViewModel(
            vaultApi = VaultApi(supabaseClient = SupabaseModule.mockClient)
        )
    )
}