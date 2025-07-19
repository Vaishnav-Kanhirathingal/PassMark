package easter.egg.passmark.ui.main

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import easter.egg.passmark.data.models.password.Password
import easter.egg.passmark.ui.main.change_password.ChangeMasterPasswordScreen
import easter.egg.passmark.ui.main.home.HomeViewModel
import easter.egg.passmark.ui.main.home.screens.HomeScreen
import easter.egg.passmark.ui.main.password_edit.PasswordEditScreen
import easter.egg.passmark.ui.main.password_view.PasswordViewScreen
import easter.egg.passmark.ui.main.settings.SettingsScreen
import easter.egg.passmark.ui.shared_components.CustomLoader
import easter.egg.passmark.ui.theme.PassMarkTheme
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.annotation.MobileHorizontalPreview
import easter.egg.passmark.utils.annotation.MobilePreview
import easter.egg.passmark.utils.extensions.findPassword
import easter.egg.passmark.utils.security.biometrics.BiometricsHandler
import easter.egg.passmark.utils.testing.PassMarkConfig
import easter.egg.passmark.utils.testing.TestTags
import easter.egg.passmark.utils.testing.TestTags.applyTag
import easter.egg.passmark.utils.values.PassMarkDimensions
import easter.egg.passmark.utils.values.PassMarkFonts
import easter.egg.passmark.utils.values.setSizeLimitation
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    companion object {
        const val PASSWORD_ENTERED_RECENTLY_KEY = "PASSWORD_ENTERED_RECENTLY_KEY"
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.cancelAppLockLambda()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (this.intent.getBooleanExtra(PASSWORD_ENTERED_RECENTLY_KEY, false)) {
            this.intent.removeExtra(PASSWORD_ENTERED_RECENTLY_KEY)
            mainViewModel.forceVerify()
        }
        setContent(
            content = {
                PassMarkTheme {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        content = { innerPadding ->
                            val navController = rememberNavController()
                            val verificationState = mainViewModel
                                .passwordVerificationState
                                .collectAsState()
                                .value

                            if (((verificationState as? ScreenState.Loaded)?.result == true) || !PassMarkConfig.AutoLockConfig.IS_ENABLED) {
                                MainActivityNavHost(
                                    modifier = Modifier.padding(paddingValues = innerPadding),
                                    navController = navController
                                )
                            } else {
                                SecurityScreen(
                                    modifier = Modifier
                                        .padding(paddingValues = innerPadding)
                                        .fillMaxSize(),
                                    text = mainViewModel.passwordEntered.collectAsState().value,
                                    onTextChanged = { mainViewModel.passwordEntered.value = it },
                                    onFingerPrintVerification = mainViewModel::forceVerify,
                                    onVerifyClicked = mainViewModel::verifyPassword,
                                    screenState = verificationState
                                )
                            }
                        }
                    )
                }
            }
        )
        if (PassMarkConfig.USE_SECURE_ACTIVITY) {
            window?.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
    }

    @Composable
    fun SecurityScreen(
        modifier: Modifier,
        text: String,
        onTextChanged: (String) -> Unit,
        onFingerPrintVerification: () -> Unit,
        onVerifyClicked: () -> Unit,
        screenState: ScreenState<Boolean>
    ) {
        val passwordVisible = remember { mutableStateOf(false) }
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 4.dp,
                alignment = Alignment.CenterVertically
            ),
            content = {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    textAlign = TextAlign.Start,
                    text = "Enter password",
                    fontFamily = PassMarkFonts.font,
                    fontSize = PassMarkFonts.Headline.medium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    textAlign = TextAlign.Start,
                    text = "Verification of password / biometrics is necessary to avoid unauthorized access.",
                    fontFamily = PassMarkFonts.font,
                    fontWeight = FontWeight.Medium,
                    fontSize = PassMarkFonts.Body.medium,
                    lineHeight = PassMarkFonts.Body.medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                OutlinedTextField(
                    modifier = Modifier
                        .applyTag(testTag = TestTags.AutoLock.PASSWORD_TEXT_FIELD.name)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    value = text,
                    onValueChange = onTextChanged,
                    label = { Text(text = "Enter your Password") },
                    placeholder = { Text(text = "Password123") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Password,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            modifier = Modifier.applyTag(testTag = TestTags.AutoLock.VISIBILITY_BUTTON.name),
                            onClick = { passwordVisible.value = !passwordVisible.value },
                            content = {
                                Icon(
                                    imageVector =
                                        if (passwordVisible.value) Icons.Default.Visibility
                                        else Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        )
                    },
                    visualTransformation =
                        if (passwordVisible.value) VisualTransformation.None
                        else PasswordVisualTransformation(),
                    supportingText = {
                        Text(
                            text =
                                if ((screenState as? ScreenState.Loaded)?.result == false) "Password is incorrect"
                                else "Enter your password"
                        )
                    },
                    isError = ((screenState as? ScreenState.Loaded)?.result == false),
                    enabled = !screenState.isLoading,
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = false,
                        keyboardType = PassMarkConfig.getKeyboardTypeForPasswords()
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 8.dp,
                        alignment = Alignment.End
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    content = {
                        val context = LocalContext.current
                        Box(
                            modifier = Modifier
                                .applyTag(testTag = TestTags.AutoLock.FINGERPRINT_BUTTON.name)
                                .size(size = PassMarkDimensions.minTouchSize)
                                .clip(shape = RoundedCornerShape(16.dp))
                                .background(color = MaterialTheme.colorScheme.surfaceContainer)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                    shape = RoundedCornerShape(size = 16.dp)
                                )
                                .clickable(
                                    enabled = !screenState.isLoading,
                                    onClick = {
                                        (context as? FragmentActivity)?.let { act ->
                                            BiometricsHandler.performBiometricAuthentication(
                                                context = context,
                                                activity = act,
                                                onComplete = { biometricHandlerOutput ->
                                                    if (biometricHandlerOutput == BiometricsHandler.BiometricHandlerOutput.AUTHENTICATED) {
                                                        onFingerPrintVerification()
                                                    } else {
                                                        biometricHandlerOutput.handleToast(
                                                            context = context,
                                                            successMessage = null

                                                        )
                                                    }
                                                }
                                            )
                                        }
                                    }
                                ),
                            contentAlignment = Alignment.Center,
                            content = {
                                Icon(
                                    imageVector = Icons.Default.Fingerprint,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                        Box(
                            modifier = Modifier
                                .applyTag(testTag = TestTags.AutoLock.CONFIRM_BUTTON.name)
                                .setSizeLimitation()
                                .clip(shape = RoundedCornerShape(size = 16.dp))
                                .background(
                                    color = ButtonDefaults.buttonColors().let {
                                        if (text.isNotEmpty()) it.containerColor else it.disabledContainerColor
                                    }
                                )
                                .clickable(
                                    enabled = (!screenState.isLoading) && text.isNotEmpty(),
                                    onClick = onVerifyClicked
                                )
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center,
                            content = {
                                val contentColor = ButtonDefaults.buttonColors().let {
                                    if (text.isNotEmpty()) it.contentColor else it.disabledContentColor
                                }
                                if (screenState.isLoading) {
                                    CustomLoader.ButtonLoader(
                                        modifier = Modifier,
                                        color = contentColor
                                    )
                                }
                                Text(
                                    modifier = Modifier.alpha(alpha = if (screenState.isLoading) 0f else 1f),
                                    text = "Verify",
                                    fontFamily = PassMarkFonts.font,
                                    fontSize = PassMarkFonts.Body.medium,
                                    fontWeight = FontWeight.Medium,
                                    color = contentColor,
                                )
                            }
                        )
                    }
                )
            }
        )
    }

    @Composable
    private fun MainActivityNavHost(
        modifier: Modifier,
        navController: NavHostController
    ) {
        val result =
            (mainViewModel.screenState.collectAsState().value as? ScreenState.Loaded)
                ?.result
        val passwordList = result?.passwordListState?.collectAsState()?.value
        val vaultList = result?.vaultListState?.collectAsState()?.value
        NavHost(
            modifier = modifier.fillMaxSize(),
            navController = navController,
            startDestination = MainScreens.Home,
            builder = {
                val composableModifier = Modifier.fillMaxSize()
                composable<MainScreens.Home>(
                    content = {
                        val homeViewModel: HomeViewModel = hiltViewModel(viewModelStoreOwner = it)
                        HomeScreen.Screen(
                            modifier = composableModifier,
                            toPasswordEditScreen = { password: Password? ->
                                navController.navigate(
                                    route = MainScreens.PasswordEdit(
                                        localId = password?.localId,
                                        cloudId = password?.cloudId,
                                        defaultVaultId = homeViewModel.vaultIdSelected.value
                                    )
                                )
                            },
                            mainViewModel = mainViewModel,
                            toViewPasswordScreen = { password: Password ->
                                navController.navigate(
                                    route = MainScreens.PasswordView(
                                        passwordJson = Gson().toJson(password)
                                    )
                                )
                            },
                            homeViewModel = homeViewModel,
                            toSettingsScreen = { navController.navigate(route = MainScreens.Settings) }
                        )
                    }
                )
                composable<MainScreens.PasswordEdit>(
                    content = {
                        PasswordEditScreen.Screen(
                            modifier = composableModifier,
                            viewModel = hiltViewModel(viewModelStoreOwner = it),
                            mainViewModel = mainViewModel,
                            navigateBack = navController::navigateUp,
                            passwordToEdit = it.arguments?.let { args ->
                                passwordList?.findPassword(
                                    cloudId = args
                                        .getInt(MainScreens.PasswordEdit::cloudId.name, -1)
                                        .takeUnless { id -> id == -1 },
                                    localId = args
                                        .getInt(MainScreens.PasswordEdit::localId.name, -1)
                                        .takeUnless { id -> id == -1 }
                                )
                            },
                            defaultVaultId = it.arguments
                                ?.getInt(MainScreens.PasswordEdit::defaultVaultId.name, -1)
                                ?.takeUnless { id -> id == -1 }
                        )
                    }
                )
                composable<MainScreens.PasswordView>(
                    content = { navBackStackEntry ->
                        val defaultPassword = Gson().fromJson(
                            navBackStackEntry.arguments!!.getString(MainScreens.PasswordView::passwordJson.name)!!,
                            Password::class.java
                        )
                        val password = passwordList?.findPassword(
                            localId = defaultPassword.localId,
                            cloudId = defaultPassword.cloudId
                        ) ?: defaultPassword
                        PasswordViewScreen.Screen(
                            modifier = composableModifier,
                            password = password,
                            navigateUp = navController::navigateUp,
                            toEditScreen = {
                                navController.navigate(
                                    route = MainScreens.PasswordEdit(
                                        localId = password.localId,
                                        cloudId = password.cloudId,
                                        defaultVaultId = password.vaultId
                                    )
                                )
                            },
                            associatedVault = password.vaultId?.let { vid -> vaultList?.find { v -> v.id == vid } },
                            passwordViewViewModel = hiltViewModel(viewModelStoreOwner = navBackStackEntry),
                            mainViewModel = mainViewModel
                        )
                    }
                )

                composable<MainScreens.Settings>(
                    content = {
                        SettingsScreen.Screen(
                            modifier = composableModifier,
                            settingsViewModel = hiltViewModel(viewModelStoreOwner = it),
                            navigateUp = navController::navigateUp,
                            toChangePasswordScreen = {
                                navController.navigate(
                                    route = MainScreens.ChangeMasterPassword
                                )
                            }
                        )
                    }
                )

                composable<MainScreens.ChangeMasterPassword>(
                    content = {
                        ChangeMasterPasswordScreen.Screen(
                            modifier = composableModifier,
                            changeMasterPasswordViewModel = hiltViewModel(viewModelStoreOwner = it),
                            navigateUp = navController::navigateUp
                        )
                    }
                )
            }
        )
    }

    override fun onPause() {
        super.onPause()
        mainViewModel.startAppLockLambda()
    }
}

private sealed class MainScreens {
    @Serializable
    data object Home : MainScreens()

    @Serializable
    data class PasswordEdit(
        val localId: Int?,
        val cloudId: Int?,
        val defaultVaultId: Int?
    ) : MainScreens()

    @Serializable
    data class PasswordView(
        val passwordJson: String
    ) : MainScreens()

    @Serializable
    data object Settings : MainScreens()

    @Serializable
    data object ChangeMasterPassword : MainScreens()
}

@MobilePreview
@MobileHorizontalPreview
@Composable
private fun SecurityScreenPreview() {
    MainActivity().SecurityScreen(
        modifier = Modifier.fillMaxSize(),
        text = "",
        onTextChanged = {},
        onFingerPrintVerification = {},
        onVerifyClicked = {},
        screenState = ScreenState.Loaded(result = false)
    )
}