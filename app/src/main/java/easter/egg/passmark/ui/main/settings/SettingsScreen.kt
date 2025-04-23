package easter.egg.passmark.ui.main.settings

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
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import easter.egg.passmark.data.storage.SettingsDataStore
import easter.egg.passmark.ui.main.password_edit.PasswordEditScreen
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.annotation.MobileHorizontalPreview
import easter.egg.passmark.utils.annotation.MobilePreview
import easter.egg.passmark.utils.values.PassMarkDimensions
import easter.egg.passmark.utils.values.PassMarkFonts
import easter.egg.passmark.utils.values.setSizeLimitation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object SettingsScreen {
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
                val screenState = settingsViewModel.screenState.collectAsState().value
                if (
                    screenState is ScreenState.Loading || screenState is ScreenState.ApiError
                ) {
                    DeleteProgressDialog(
                        modifier = Modifier.fillMaxWidth(),
                        currentActiveStage = settingsViewModel.currentStage.collectAsState().value.ordinal,
                        totalStages = DeletionStages.entries.size,
                        showCurrentStageError = (screenState is ScreenState.ApiError)
                    )
                }
                val context = LocalContext.current
                LaunchedEffect(
                    key1 = screenState,
                    block = {
                        when (screenState) {
                            is ScreenState.PreCall, is ScreenState.Loading -> {}
                            is ScreenState.Loaded -> TODO("close app")
                            is ScreenState.ApiError -> {
                                delay(3_000L)
                                screenState.manageToastActions(context = context)
                                settingsViewModel.deleteEverything(silent = true)
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
                    text = "Enable fingerprint by default",
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(shape = RoundedCornerShape(size = 12.dp))
                        .background(color = MaterialTheme.colorScheme.surfaceContainer)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            shape = RoundedCornerShape(size = 12.dp)
                        ),
                    content = {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                            fontFamily = PassMarkFonts.font,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = PassMarkFonts.Title.medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            text = "Delete account?"
                        )
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            fontFamily = PassMarkFonts.font,
                            fontWeight = FontWeight.Medium,
                            fontSize = PassMarkFonts.Body.medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            text = "Deleting your account is permanent and would delete all the " +
                                    "Vaults and Passwords along with it (even offline ones). This " +
                                    "process is unrecoverable."
                        )
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
                                .clickable(onClick = settingsViewModel::deleteEverything)
                                .align(alignment = Alignment.End),
                            contentAlignment = Alignment.Center,
                            content = {
                                Text(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    text = "Delete Account",
                                    fontFamily = PassMarkFonts.font,
                                    fontSize = PassMarkFonts.Body.medium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                    }
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height = PassMarkDimensions.minTouchSize)
                )
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DeleteProgressDialog(
        modifier: Modifier,
        currentActiveStage: Int,
        totalStages: Int,
        showCurrentStageError: Boolean
    ) {
        BasicAlertDialog(
            modifier = modifier
                .clip(shape = RoundedCornerShape(size = PassMarkDimensions.dialogRadius))
                .background(color = MaterialTheme.colorScheme.surfaceContainer),
            onDismissRequest = {},
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(
                        space = 8.dp,
                        alignment = Alignment.CenterVertically
                    ),
                    content = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            fontFamily = PassMarkFonts.font,
                            fontSize = PassMarkFonts.Title.medium,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            text = "Deleting everything. Avoid closing the app to prevent data corruption."
                        )
                        CustomStagedLoader(
                            currentActiveStage = currentActiveStage,
                            totalStages = totalStages,
                            showCurrentStageError = showCurrentStageError
                        )
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            fontFamily = PassMarkFonts.font,
                            fontSize = PassMarkFonts.Body.medium,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            text = "Currently at stage $currentActiveStage/${DeletionStages.entries.size}."
                        )
                    }
                )
            }
        )
    }

    @Composable
    fun CustomStagedLoader(
        currentActiveStage: Int,
        totalStages: Int,
        startSize: Dp = PassMarkDimensions.minTouchSize,
        layerWidth: Dp = 4.dp,
        layerGap: Dp = 2.dp,
        showCurrentStageError: Boolean
    ) {
        val loaderColor = MaterialTheme.colorScheme.primary
        val loaderTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest

        Box(
            modifier = Modifier,
            contentAlignment = Alignment.Center,
            content = {
                repeat(
                    times = totalStages,
                    action = { stage ->
                        val loaderModifier = Modifier.size(
                            size = startSize + ((layerWidth + layerGap) * 2 * stage)
                        )
                        if (stage == currentActiveStage) {
                            CircularProgressIndicator(
                                modifier = loaderModifier,
                                color =
                                    if (showCurrentStageError) MaterialTheme.colorScheme.error
                                    else loaderColor,
                                strokeWidth = layerWidth,
                                trackColor = loaderTrackColor
                            )
                        } else {
                            CircularProgressIndicator(
                                modifier = loaderModifier,
                                color = loaderColor,
                                strokeWidth = layerWidth,
                                trackColor = loaderTrackColor,
                                progress = { if (currentActiveStage > stage) 1f else 0f },
                            )
                        }

                    }
                )
            }
        )
    }
}

@Composable
@MobilePreview
@MobileHorizontalPreview
private fun SettingsScreenPreview() {
    SettingsScreen.Screen(
        modifier = Modifier.fillMaxSize(),
        settingsViewModel = SettingsViewModel(
            settingsDataStore = SettingsDataStore(context = LocalContext.current)
        ),
        navigateUp = {}
    )
}

@Composable
@MobilePreview
private fun DeleteProgressDialogPreview() {
    SettingsScreen.DeleteProgressDialog(
        modifier = Modifier.padding(horizontal = 40.dp),
        currentActiveStage = 3,
        totalStages = 6,
        showCurrentStageError = true
    )
}