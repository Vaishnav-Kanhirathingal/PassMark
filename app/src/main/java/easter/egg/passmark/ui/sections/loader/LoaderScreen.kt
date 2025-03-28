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
import easter.egg.passmark.data.api.UserApi
import easter.egg.passmark.data.shared.PassMarkDimensions
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
        toEditUserScreen: (isNewUser:Boolean) -> Unit
    ) {
        val context = LocalContext.current
        LaunchedEffect(
            key1 = viewModel.screenState.collectAsState().value,
            block = {
                when (val state = viewModel.screenState.value) {
                    is ScreenState.Loaded -> {
                        when (state.result) {
                            UserState.DOES_NOT_EXIST -> toLoginScreen()
                            UserState.NEW_USER-> toEditUserScreen(true)
                            UserState.EXISTS_WITHOUT_KEY_IN_STORAGE -> toEditUserScreen(false)
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
    LoaderScreen.Screen(
        modifier = Modifier.fillMaxSize(),
        viewModel = LoaderViewModel(
            supabaseClient = SupabaseModule.mockClient,
            userApi = UserApi(SupabaseModule.mockClient)
        ),
        toHomeScreen = {},
        toLoginScreen = {},
        toEditUserScreen = {}
    )
}