package easter.egg.passmark.ui.auth.loader

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import easter.egg.passmark.data.supabase.account.SupabaseAccountHelper
import easter.egg.passmark.data.supabase.api.UserApi
import easter.egg.passmark.di.supabase.SupabaseModule
import easter.egg.passmark.ui.shared_components.CustomLoader
import easter.egg.passmark.ui.shared_components.ErrorScreen
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.annotation.MobilePreview

object LoaderScreen {
    private val TAG = this::class.simpleName

    @Composable
    fun Screen(
        modifier: Modifier,
        viewModel: LoaderViewModel,
        toMainActivity: () -> Unit,
        toLoginScreen: () -> Unit,
        toMasterKeyScreen: (isNewUser: Boolean) -> Unit
    ) {
        val screenState = viewModel.screenState.collectAsState().value

        LaunchedEffect(
            key1 = screenState,
            block = {
                when (screenState) {
                    is ScreenState.PreCall, is ScreenState.Loading, is ScreenState.ApiError -> {}
                    is ScreenState.Loaded -> {
                        when (screenState.result) {
                            UserState.NOT_LOGGED_IN -> toLoginScreen()
                            UserState.NEW_USER -> toMasterKeyScreen(true)
                            UserState.EXISTS_WITHOUT_KEY_IN_STORAGE -> toMasterKeyScreen(false)
                            UserState.EXISTS_WITH_KEY_IN_STORAGE -> toMainActivity()
                        }
                    }
                }
            }
        )
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
            content = {
                if (screenState is ScreenState.ApiError) {
                    ErrorScreen.ErrorCard(
                        modifier = ErrorScreen.errorCardFullScreenModifier,
                        screenState = screenState,
                        onRetry = viewModel::forceVerify,
                        attemptedAction = "trying to load user data"
                    )
                } else {
                    CustomLoader.FullScreenLoader(modifier = Modifier)
                }
            }
        )
    }

}

@Composable
@MobilePreview
fun LoaderScreenPreview() {
    val context = LocalContext.current.applicationContext
    LoaderScreen.Screen(
        modifier = Modifier.fillMaxSize(),
        viewModel = LoaderViewModel(
            userApi = UserApi(SupabaseModule.mockClient),
            supabaseAccountHelper = SupabaseAccountHelper(SupabaseModule.mockClient),
            applicationContext = context
        ),
        toMainActivity = {},
        toLoginScreen = {},
        toMasterKeyScreen = {}
    )
}