package easter.egg.passmark.ui.auth.loader

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import easter.egg.passmark.data.supabase.account.SupabaseAccountHelper
import easter.egg.passmark.data.supabase.api.UserApi
import easter.egg.passmark.di.supabase.SupabaseModule
import easter.egg.passmark.ui.shared_components.CustomLoader
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.annotation.MobilePreview
import easter.egg.passmark.utils.values.PassMarkFonts
import easter.egg.passmark.utils.values.setSizeLimitation

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
                    Column(
                        modifier = Modifier
//                            .padding(horizontal = 32.dp)
                            .clip(shape = RoundedCornerShape(size = 16.dp))
                            .background(color = MaterialTheme.colorScheme.surfaceContainer)
                            .width(width = 280.dp)
                            .padding(all = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(
                            space = 4.dp,
                            alignment = Alignment.CenterVertically
                        ),
                        content = {
                            Text(
                                textAlign = TextAlign.Center,
                                text = when (screenState) {
                                    is ScreenState.ApiError.NetworkError -> "Network error"
                                    is ScreenState.ApiError.SomethingWentWrong -> "Something went wrong"
                                },
                                fontFamily = PassMarkFonts.font,
                                fontSize = PassMarkFonts.Title.medium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                textAlign = TextAlign.Center,
                                text = when (screenState) {
                                    is ScreenState.ApiError.NetworkError -> "A network call has failed. Please check your internet connection and try again."
                                    is ScreenState.ApiError.SomethingWentWrong -> "Something went wrong. Please try again."
                                },
                                fontFamily = PassMarkFonts.font,
                                fontSize = PassMarkFonts.Body.medium,
                                lineHeight = PassMarkFonts.Body.medium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Box(
                                modifier = Modifier
                                    .setSizeLimitation()
                                    .clip(shape = RoundedCornerShape(size = 16.dp))
                                    .background(color = MaterialTheme.colorScheme.primaryContainer)
                                    .clickable(onClick = viewModel::forceVerify)
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.Center,
                                content = {
                                    Text(
                                        text = "Try again",
                                        fontFamily = PassMarkFonts.font,
                                        fontSize = PassMarkFonts.Body.medium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            )
                        }
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