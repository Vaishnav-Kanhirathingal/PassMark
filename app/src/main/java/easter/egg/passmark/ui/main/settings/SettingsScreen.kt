package easter.egg.passmark.ui.main.settings

import android.content.Intent
import androidx.activity.compose.LocalActivity
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import easter.egg.passmark.ui.auth.AuthActivity
import easter.egg.passmark.ui.main.password_edit.PasswordEditScreen
import easter.egg.passmark.ui.shared_components.ConfirmationDialog
import easter.egg.passmark.ui.shared_components.CustomLoader
import easter.egg.passmark.ui.shared_components.StagedLoaderDialog
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.annotation.MobileHorizontalPreview
import easter.egg.passmark.utils.annotation.MobilePreview
import easter.egg.passmark.utils.annotation.PreviewRestricted
import easter.egg.passmark.utils.extensions.customTopBarModifier
import easter.egg.passmark.utils.testing.TestTags
import easter.egg.passmark.utils.testing.TestTags.applyTag
import easter.egg.passmark.utils.values.PassMarkDimensions
import easter.egg.passmark.utils.values.PassMarkFonts
import easter.egg.passmark.utils.values.setSizeLimitation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object SettingsScreen {
    private const val RESET_DESCRIPTION = "Resetting your account is permanent and would delete " +
            "all the Vaults and Passwords (even offline ones) along with all your data. This " +
            "process is unrecoverable."
    const val CHANGE_PASSWORD_DESCRIPTION = "Changing the password is a multi-layered " +
            "process which re-encrypts all passwords with a new cryptographic key. Make sure " +
            "you have a stable internet connection to perform this task. Re-login will be " +
            "required at the end for user confirmation and background syncing."
    private const val LOG_OUT_DESCRIPTION = "Logging out won’t delete your offline passwords. " +
            "However, uninstalling the app, resetting it, or resetting your account will. " +
            "After logging out, you'll need to re-enter your password to log back in."

    @Composable
    fun Screen(
        modifier: Modifier,
        settingsViewModel: SettingsViewModel,
        navigateUp: () -> Unit,
        toChangePasswordScreen: () -> Unit
    ) {
        Scaffold(
            modifier = modifier,
            topBar = {
                SettingTopBar(
                    modifier = Modifier.customTopBarModifier(),
                    navigateUp = navigateUp
                )
            },
            content = {
                ScreenContent(
                    modifier = Modifier
                        .padding(paddingValues = it)
                        .fillMaxSize(),
                    settingsViewModel = settingsViewModel,
                    toChangePasswordScreen = toChangePasswordScreen
                )
                DialogContent(settingsViewModel = settingsViewModel)
            }
        )
    }

    @Composable
    private fun SettingTopBar(
        modifier: Modifier,
        navigateUp: () -> Unit
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                space = 16.dp,
                alignment = Alignment.Start
            ),
            content = {
                Box(
                    modifier = Modifier
                        .size(size = PassMarkDimensions.minTouchSize)
                        .clip(shape = CircleShape)
                        .background(color = MaterialTheme.colorScheme.primaryContainer)
                        .clickable(onClick = navigateUp),
                    contentAlignment = Alignment.Center,
                    content = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                )
                Text(
                    modifier = Modifier.weight(weight = 1f),
                    text = "Settings",
                    fontFamily = PassMarkFonts.font,
                    fontSize = PassMarkFonts.Title.medium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        )
    }

    @Composable
    private fun ScreenContent(
        modifier: Modifier,
        settingsViewModel: SettingsViewModel,
        toChangePasswordScreen: () -> Unit,
    ) {
        Column(
            modifier = modifier
                .verticalScroll(state = rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 12.dp, alignment = Alignment.Top),
            content = {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height = 8.dp)
                )
                val scope = rememberCoroutineScope()
                val biometricsEnabled = settingsViewModel.settingsDataStore
                    .getBiometricEnabledFlow()
                    .collectAsState(initial = false)
                    .value
                PasswordEditScreen.CustomSwitch(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Enable fingerprint by default",
                    isEnabled = true,
                    isChecked = biometricsEnabled,
                    onCheckedChange = {
                        scope.launch {
                            settingsViewModel.settingsDataStore.changeBiometricsPreference(
                                biometricsEnabledByDefault = !biometricsEnabled
                            )
                        }
                    },
                    testTag = TestTags.Settings.FINGERPRINT_AUTHENTICATION_SWITCH.name
                )
                val offlineEnabled = settingsViewModel.settingsDataStore
                    .getOfflineStorageFlow()
                    .collectAsState(initial = false)
                    .value
                PasswordEditScreen.CustomSwitch(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Enable Offline Storage for Passwords by default",
                    isEnabled = true,
                    isChecked = offlineEnabled,
                    onCheckedChange = {
                        scope.launch {
                            settingsViewModel.settingsDataStore.changeOfflineStoragePreference(
                                offlineStorageEnabledByDefault = !offlineEnabled
                            )
                        }
                    },
                    testTag = TestTags.Settings.LOCAL_STORAGE_SWITCH.name
                )
                ActionCard(
                    modifier = Modifier.fillMaxWidth(),
                    titleText = "Change password?",
                    contentText = CHANGE_PASSWORD_DESCRIPTION,
                    buttonText = "Change Password",
                    onClick = toChangePasswordScreen,
                    testTag = TestTags.Settings.CHANGE_PASSWORD_BUTTON.name
                )
                val logoutState = settingsViewModel.logoutScreenState.collectAsState()
                val context = LocalContext.current
                val activity = LocalActivity.current
                fun toAuthActivity() {
                    context.startActivity(Intent(context, AuthActivity::class.java))
                    activity?.finish()
                }
                LaunchedEffect(
                    key1 = logoutState.value,
                    block = {
                        when (val state = logoutState.value) {
                            is ScreenState.PreCall, is ScreenState.Loading -> {}
                            is ScreenState.Loaded -> toAuthActivity()
                            is ScreenState.ApiError -> state.manageToastActions(context = context)
                        }
                    }
                )
                ActionCard(
                    modifier = Modifier.fillMaxWidth(),
                    titleText = "Log out?",
                    contentText = LOG_OUT_DESCRIPTION,
                    buttonText = "Log out",
                    onClick = settingsViewModel::logout,
                    isLoading = logoutState.value.isLoading,
                    testTag = TestTags.Settings.LOG_OUT.name
                )
                ActionCard(
                    modifier = Modifier.fillMaxWidth(),
                    titleText = "Reset account?",
                    contentText = RESET_DESCRIPTION,
                    buttonText = "Reset account",
                    onClick = { settingsViewModel.setResetConfirmationDialogVisibility(visible = true) },
                    testTag = TestTags.Settings.RESET_ACCOUNT_BUTTON.name
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height = PassMarkDimensions.minTouchSize)
                )
            }
        )
    }

    @Composable
    fun DialogContent(
        settingsViewModel: SettingsViewModel
    ) {
        val context = LocalContext.current
        val activity = LocalActivity.current
        fun toAuthActivity() {
            context.startActivity(Intent(context, AuthActivity::class.java))
            activity?.finish()
        }
        //--------------------------------------------------------------------------------reset-user
        val resetUserApiState = settingsViewModel.deletionScreenState.collectAsState().value
        val currentActiveStage = settingsViewModel.currentStage.collectAsState().value
        when (resetUserApiState) {
            is ScreenState.PreCall -> {
                ConfirmationDialog(
                    modifier = Modifier.fillMaxWidth(),
                    titleText = "Confirm resetting account?",
                    contentText = RESET_DESCRIPTION,
                    negativeButtonText = "Cancel",
                    onNegativeClicked = {
                        settingsViewModel.setResetConfirmationDialogVisibility(visible = false)
                    },
                    positiveButtonText = "Reset",
                    onPositiveClicked = {
                        settingsViewModel.setResetConfirmationDialogVisibility(visible = false)
                        settingsViewModel.deleteEverything(silent = false)
                    },
                    screenState = ScreenState.PreCall()
                )
            }

            is ScreenState.Loading, is ScreenState.ApiError -> {
                StagedLoaderDialog(
                    modifier = Modifier.fillMaxWidth(),
                    currentActiveStage = currentActiveStage.ordinal,
                    totalStages = DeletionStages.entries.size,
                    showCurrentStageError = (resetUserApiState is ScreenState.ApiError),
                    title = "Deleting everything. Avoid closing the app to prevent data corruption.",
                    subtitle = currentActiveStage.getTaskMessage()
                )
            }

            null, is ScreenState.Loaded -> {}
        }
        LaunchedEffect(
            key1 = resetUserApiState,
            block = {
                when (resetUserApiState) {
                    null, is ScreenState.PreCall, is ScreenState.Loading -> {}
                    is ScreenState.Loaded -> toAuthActivity()
                    is ScreenState.ApiError -> {
                        delay(3_000L)
                        resetUserApiState.manageToastActions(context = context)
                        settingsViewModel.deleteEverything(silent = true)
                    }
                }
            }
        )

    }

    @Composable
    private fun ActionCard(
        modifier: Modifier,
        titleText: String,
        contentText: String,
        buttonText: String,
        onClick: () -> Unit,
        isLoading: Boolean = false,
        testTag: String
    ) {
        PasswordEditScreen.DefaultCard(
            modifier = modifier,
            content = {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                    fontFamily = PassMarkFonts.font,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = PassMarkFonts.Title.medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    text = titleText
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    fontFamily = PassMarkFonts.font,
                    fontWeight = FontWeight.Medium,
                    fontSize = PassMarkFonts.Body.medium,
                    lineHeight = PassMarkFonts.Body.large,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    text = contentText
                )
                Box(
                    modifier = Modifier
                        .applyTag(testTag = testTag)
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .setSizeLimitation()
                        .clip(shape = RoundedCornerShape(size = 12.dp))
                        .background(color = MaterialTheme.colorScheme.surfaceContainerHighest)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant,
                            shape = RoundedCornerShape(size = 12.dp)
                        )
                        .clickable(onClick = onClick)
                        .align(alignment = Alignment.End),
                    contentAlignment = Alignment.Center,
                    content = {
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .alpha(alpha = if (isLoading) 0f else 1f),
                            text = buttonText,
                            fontFamily = PassMarkFonts.font,
                            fontSize = PassMarkFonts.Body.medium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
        )
    }
}

@PreviewRestricted
@Composable
@MobilePreview
@MobileHorizontalPreview
private fun SettingsScreenPreview() {
    SettingsScreen.Screen(
        modifier = Modifier.fillMaxSize(),
        settingsViewModel = SettingsViewModel.getTestViewModel(),
        navigateUp = {},
        toChangePasswordScreen = {}
    )
}