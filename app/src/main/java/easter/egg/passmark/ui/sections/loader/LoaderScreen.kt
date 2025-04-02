package easter.egg.passmark.ui.sections.loader

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import easter.egg.passmark.utils.values.PassMarkDimensions
import easter.egg.passmark.data.supabase.account.SupabaseAccountHelper
import easter.egg.passmark.data.supabase.api.UserApi
import easter.egg.passmark.di.supabase.SupabaseModule
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.annotation.MobileHorizontalPreview
import easter.egg.passmark.utils.annotation.MobilePreview

object LoaderScreen {
    private val TAG = this::class.simpleName

    @Composable
    fun Screen(
        modifier: Modifier,
        viewModel: LoaderViewModel,
        toHomeScreen: () -> Unit,
        toLoginScreen: () -> Unit,
        toMasterKeyScreen: (isNewUser: Boolean) -> Unit
    ) {
        val context = LocalContext.current
        LaunchedEffect(
            key1 = viewModel.screenState.collectAsState().value,
            block = {
                when (val state = viewModel.screenState.value) {
                    is ScreenState.Loaded -> {
                        when (state.result) {
                            UserState.NOT_LOGGED_IN -> toLoginScreen()
                            UserState.NEW_USER -> toMasterKeyScreen(true)
                            UserState.EXISTS_WITHOUT_KEY_IN_STORAGE -> toMasterKeyScreen(false)
                            UserState.EXISTS_WITH_KEY_IN_STORAGE -> toHomeScreen()
                        }
                    }

                    is ScreenState.PreCall, is ScreenState.Loading -> {}
                    is ScreenState.ApiError -> {
                        Toast.makeText(
                            context,
                            state.generalToastMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                        state.setErrorHasBeenDisplayed()
                    }
                }
            }
        )
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
            content = {
                if (viewModel.screenState.collectAsState().value is ScreenState.ApiError) {
                    Button(
                        onClick = { TODO() },
                        content = { Text(text = "Retry") }
                    )
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.size(size = PassMarkDimensions.minTouchSize)
                    )
                }
            }
        )
    }
}

@Composable
@MobilePreview
@MobileHorizontalPreview
fun LoaderScreenPreview() {
    val context = LocalContext.current.applicationContext
    LoaderScreen.Screen(
        modifier = Modifier.fillMaxSize(),
        viewModel = LoaderViewModel(
            userApi = UserApi(SupabaseModule.mockClient),
            supabaseAccountHelper = SupabaseAccountHelper(SupabaseModule.mockClient),
            applicationContext = context
        ),
        toHomeScreen = {},
        toLoginScreen = {},
        toMasterKeyScreen = {}
    )
}