package easter.egg.passmark.ui.main.settings

import android.content.Intent
import android.util.Log
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import easter.egg.passmark.data.storage.SettingsDataStore
import easter.egg.passmark.data.storage.database.PasswordDao
import easter.egg.passmark.data.supabase.account.SupabaseAccountHelper
import easter.egg.passmark.data.supabase.api.UserApi
import easter.egg.passmark.di.supabase.SupabaseModule
import easter.egg.passmark.ui.auth.AuthActivity
import easter.egg.passmark.ui.main.password_edit.PasswordEditScreen
import easter.egg.passmark.ui.shared_components.ConfirmationDialog
import easter.egg.passmark.ui.shared_components.StagedLoaderDialog
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.annotation.MobileHorizontalPreview
import easter.egg.passmark.utils.annotation.MobilePreview
import easter.egg.passmark.utils.values.PassMarkDimensions
import easter.egg.passmark.utils.values.PassMarkFonts
import easter.egg.passmark.utils.values.setSizeLimitation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object SettingsScreen {
    private val TAG = this::class.simpleName

    @Composable
    fun Screen(
        modifier: Modifier,
        settingsViewModel: SettingsViewModel,
        navigateUp: () -> Unit
    ) {
        Scaffold(
            modifier = modifier,
            topBar = {
                SettingTopBar(
                    modifier = Modifier.fillMaxWidth(),
                    navigateUp = navigateUp
                )
            },
            content = {
                ScreenContent(
                    modifier = Modifier
                        .padding(paddingValues = it)
                        .fillMaxSize(),
                    settingsViewModel = settingsViewModel
                )
                val screenState = settingsViewModel.deletionScreenState.collectAsState().value
                val currentActiveStage = settingsViewModel.currentStage.collectAsState().value
                if (screenState is ScreenState.Loading || screenState is ScreenState.ApiError) {
                    StagedLoaderDialog(
                        modifier = Modifier.fillMaxWidth(),
                        currentActiveStage = currentActiveStage.ordinal,
                        totalStages = DeletionStages.entries.size,
                        showCurrentStageError = (screenState is ScreenState.ApiError),
                        title = "Deleting everything. Avoid closing the app to prevent data corruption.",
                        subtitle = currentActiveStage.getTaskMessage()
                    )
                } else {
                    Log.d(TAG, "not showing dialog")
                }
                val context = LocalContext.current
                val activity = LocalActivity.current
                LaunchedEffect(
                    key1 = screenState,
                    block = {
                        when (screenState) {
                            is ScreenState.PreCall, is ScreenState.Loading -> {}
                            is ScreenState.Loaded -> {
                                context.startActivity(Intent(context, AuthActivity::class.java))
                                activity?.finish()
                            }

                            is ScreenState.ApiError -> {
                                delay(3_000L)
                                screenState.manageToastActions(context = context)
                                settingsViewModel.deleteEverything(
                                    silent = true
                                )
                            }
                        }
                    }
                )
            }
        )
    }

    @Composable
    private fun SettingTopBar(
        modifier: Modifier,
        navigateUp: () -> Unit
    ) {
        Row(
            modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
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
        settingsViewModel: SettingsViewModel
    ) {
        Column(
            modifier = modifier.verticalScroll(
                state = rememberScrollState()
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 12.dp, alignment = Alignment.Top),
            content = {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height = 8.dp)
                )
                val scope = rememberCoroutineScope()

                val switchModifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)

                val biometricsEnabled = settingsViewModel.settingsDataStore
                    .getBiometricEnabledFlow()
                    .collectAsState(initial = false)
                    .value

                PasswordEditScreen.CustomSwitch(
                    modifier = switchModifier,
                    text = "Enable fingerprint by default",
                    isEnabled = true,
                    isChecked = biometricsEnabled,
                    onCheckedChange = {
                        scope.launch {
                            settingsViewModel.settingsDataStore.changeBiometricsPreference(
                                biometricsEnabledByDefault = !biometricsEnabled
                            )
                        }
                    }
                )

                val offlineEnabled = settingsViewModel.settingsDataStore
                    .getOfflineStorageFlow()
                    .collectAsState(initial = false)
                    .value

                PasswordEditScreen.CustomSwitch(
                    modifier = switchModifier,
                    text = "Enable Offline Storage for Passwords by default",
                    isEnabled = true,
                    isChecked = offlineEnabled,
                    onCheckedChange = {
                        scope.launch {
                            settingsViewModel.settingsDataStore.changeOfflineStoragePreference(
                                offlineStorageEnabledByDefault = !offlineEnabled
                            )
                        }
                    }
                )
                PasswordEditScreen.DefaultCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    content = {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                            fontFamily = PassMarkFonts.font,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = PassMarkFonts.Title.medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            text = "Reset account?"
                        )
                        val resetDescription = "Resetting your account is permanent and would " +
                                "delete all the Vaults and Passwords (even offline ones) " +
                                "along with all your data. This process is unrecoverable."

                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            fontFamily = PassMarkFonts.font,
                            fontWeight = FontWeight.Medium,
                            fontSize = PassMarkFonts.Body.medium,
                            lineHeight = PassMarkFonts.Body.large,
                            color = MaterialTheme.colorScheme.onSurface,
                            text = resetDescription
                        )
                        if (settingsViewModel.resetConfirmationDialogState.collectAsState().value) {
                            ConfirmationDialog(
                                modifier = Modifier.fillMaxWidth(),
                                titleText = "Confirm resetting account?",
                                contentText = resetDescription,
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
                        } else {
                            Log.d(TAG, "reset confirmation dialog invisible")
                        }
                        Box(
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                                .setSizeLimitation()
                                .clip(shape = RoundedCornerShape(size = 12.dp))
                                .background(color = MaterialTheme.colorScheme.surfaceContainerHighest)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(size = 12.dp)
                                )
                                .clickable(
                                    onClick = {
                                        settingsViewModel.setResetConfirmationDialogVisibility(
                                            visible = true
                                        )
                                    }
                                )
                                .align(alignment = Alignment.End),
                            contentAlignment = Alignment.Center,
                            content = {
                                Text(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    text = "Reset Account",
                                    fontFamily = PassMarkFonts.font,
                                    fontSize = PassMarkFonts.Body.medium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                    }
                )
                // TODO: change password UI

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height = PassMarkDimensions.minTouchSize)
                )
            }
        )
    }

}

@Composable
@MobilePreview
@MobileHorizontalPreview
private fun SettingsScreenPreview() {
    val client = SupabaseModule.mockClient
    SettingsScreen.Screen(
        modifier = Modifier.fillMaxSize(),
        settingsViewModel = SettingsViewModel(
            context = LocalContext.current,
            settingsDataStore = SettingsDataStore(context = LocalContext.current),
            supabaseAccountHelper = SupabaseAccountHelper(supabaseClient = client),
            userApi = UserApi(supabaseClient = client),
            passwordDao = PasswordDao.getTestingDao()
        ),
        navigateUp = {}
    )
}