package easter.egg.passmark.ui.auth.login

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import easter.egg.passmark.BuildConfig
import easter.egg.passmark.R
import easter.egg.passmark.di.supabase.SupabaseModule
import easter.egg.passmark.ui.shared_components.CustomLoader
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.accessibility.Describable.Companion.setDescription
import easter.egg.passmark.utils.accessibility.auth.LoginDescribable
import easter.egg.passmark.utils.annotation.MobilePreview
import easter.egg.passmark.utils.annotation.PreviewRestricted
import easter.egg.passmark.utils.values.PassMarkFonts
import easter.egg.passmark.utils.values.setSizeLimitation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object LoginScreen {
    private val TAG = this::class.simpleName

    @Composable
    fun Screen(
        modifier: Modifier,
        viewModel: LoginViewModel,
        toLoaderScreen: () -> Unit
    ) {
        val state = viewModel.screenState.collectAsState().value
        val context = LocalContext.current
        LaunchedEffect(
            key1 = state,
            block = {
                when (state) {
                    is ScreenState.PreCall -> {}
                    is ScreenState.Loading -> {}
                    is ScreenState.Loaded -> toLoaderScreen()
                    is ScreenState.ApiError -> state.manageToastActions(context = context)
                }
            }
        )
        LoginUi(
            modifier = modifier,
            isLoading = state.isLoading,
            onCredentialResponse = viewModel::login
        )
    }

    @Composable
    private fun LoginUi(
        modifier: Modifier,
        isLoading: Boolean,
        onCredentialResponse: (GetCredentialResponse) -> Unit
    ) {
        val verticalColumnPadding = 40.dp
        Column(
            modifier = modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(state = rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterVertically
            ),
            content = {
                Spacer(modifier = Modifier.height(height = verticalColumnPadding))
                Image(
                    modifier = Modifier
                        .widthIn(max = 400.dp)
                        .fillMaxWidth(fraction = 0.4f)
                        .clip(shape = RoundedCornerShape(size = 16.dp))
                        .background(color = MaterialTheme.colorScheme.primaryContainer)
                        .padding(all = 28.dp),
                    painter = painterResource(id = R.drawable.passmark_app_icon),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
                )
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "PassMark",
                    textAlign = TextAlign.Center,
                    fontFamily = PassMarkFonts.font,
                    fontSize = PassMarkFonts.Title.large,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    modifier = Modifier.fillMaxWidth(fraction = 0.8f),
                    text = "Join PassMark and embark on a journey of secure password storage",
                    fontSize = PassMarkFonts.Label.medium,
                    lineHeight = PassMarkFonts.Label.medium,
                    fontFamily = PassMarkFonts.font,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                GoogleSignInButton(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    isLoading = isLoading,
                    onCredentialResponse = onCredentialResponse
                )
                Spacer(modifier = Modifier.height(height = verticalColumnPadding))
            }
        )

    }

    @Composable
    private fun GoogleSignInButton(
        modifier: Modifier = Modifier,
        isLoading: Boolean,
        onCredentialResponse: (GetCredentialResponse) -> Unit
    ) {
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current
        Box(
            modifier = modifier
                .setDescription(describable = LoginDescribable.GOOGLE_LOGIN_BUTTON)
                .setSizeLimitation()
                .clip(shape = RoundedCornerShape(size = 16.dp))
                .background(color = MaterialTheme.colorScheme.surfaceContainer)
                .clickable(
                    enabled = !isLoading,
                    onClick = {
                        val googleIdOption = GetGoogleIdOption.Builder()
                            .setServerClientId(BuildConfig.FIREBASE_WEB_CLIENT_ID)
                            .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts = false)
                            .build()
                        val request = GetCredentialRequest.Builder()
                            .addCredentialOption(googleIdOption)
                            .build()
                        coroutineScope.launch {
                            try {
                                val result = CredentialManager.create(context).getCredential(
                                    request = request,
                                    context = context,
                                )
                                withContext(Dispatchers.Main) {
                                    onCredentialResponse(result)
                                }
                            } catch (e: GetCredentialCancellationException) {
                                Log.d(TAG, "cancelled sign in")
                            } catch (e: Exception) {
                                e.printStackTrace()
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        context,
                                        "Something Went Wrong",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    },
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    shape = RoundedCornerShape(size = 16.dp)
                )
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
            content = {
                Row(
                    modifier = Modifier.alpha(alpha = if (isLoading) 0f else 1f),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 8.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    content = {
                        Image(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(id = R.drawable.google_48),
                            contentScale = ContentScale.Fit,
                            contentDescription = null
                        )
                        Text(
                            text = "Google",
                            fontFamily = PassMarkFonts.font,
                            fontSize = PassMarkFonts.Body.medium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
                if (isLoading) {
                    CustomLoader.ButtonLoader(
                        modifier = Modifier,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )
    }
}

@PreviewRestricted
@Composable
@MobilePreview
private fun LoginScreenPrev() {
    LoginScreen.Screen(
        modifier = Modifier.fillMaxSize(),
        viewModel = LoginViewModel(supabaseClient = SupabaseModule.mockClient),
        toLoaderScreen = {}
    )
}