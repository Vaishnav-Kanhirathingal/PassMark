package easter.egg.passmark.ui.main.change_password

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import easter.egg.passmark.R
import easter.egg.passmark.data.supabase.api.VaultApi
import easter.egg.passmark.di.supabase.SupabaseModule
import easter.egg.passmark.ui.auth.master_key.PasswordTextState
import easter.egg.passmark.ui.main.settings.SettingsScreen
import easter.egg.passmark.ui.shared_components.StagedLoaderDialog
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.annotation.MobilePreview
import easter.egg.passmark.utils.values.PassMarkFonts
import easter.egg.passmark.utils.values.setSizeLimitation
import kotlinx.coroutines.delay

object ChangePasswordScreen {
    @Composable
    fun Screen(
        modifier: Modifier,
        changePasswordViewModel: ChangePasswordViewModel,
        navigateUp: () -> Unit
    ) {
        // TODO: make back button a shared composable
        val screenState =
            changePasswordViewModel.screenState.collectAsState().value

        when (screenState) {
            is ScreenState.Loading, is ScreenState.ApiError -> {
                val currentActiveStage =
                    changePasswordViewModel.currentReEncryptionStates.collectAsState().value
                StagedLoaderDialog(
                    modifier = Modifier.fillMaxWidth(),
                    currentActiveStage = currentActiveStage.ordinal,
                    totalStages = ReEncryptionStates.entries.size,
                    showCurrentStageError = (screenState is ScreenState.ApiError<Unit>),
                    title = "Re-encrypting files",
                    subtitle = currentActiveStage.getSubtitle()
                )
            }

            is ScreenState.PreCall, is ScreenState.Loaded -> {}
        }

        val context = LocalContext.current
        LaunchedEffect(
            key1 = screenState,
            block = {
                when (screenState) {
                    is ScreenState.PreCall, is ScreenState.Loading -> {}
                    is ScreenState.Loaded -> {
                        TODO("sign out -> to Auth loader screen")
                    }

                    is ScreenState.ApiError -> {
                        screenState.manageToastActions(context = context)
                        delay(timeMillis = 1_000L)
                        changePasswordViewModel.changePassword(isSilent = true)
                    }
                }
            }
        )

        val spacing = 12.dp
        val corners = 12.dp

        Column(
            modifier = modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(state = rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            content = {
                @Composable
                fun CustomSpacer() {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(height = spacing)

                    )
                }
                Box(
                    modifier = Modifier
                        .size(size = 100.dp)
                        .clip(shape = RoundedCornerShape(size = 16.dp))
                        .background(color = MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                    content = {
                        Image(
                            modifier = Modifier.size(size = 60.dp),
                            painter = painterResource(R.drawable.ic_launcher_uncropped),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                )
                CustomSpacer()
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Change Password?",
                    fontFamily = PassMarkFonts.font,
                    fontSize = PassMarkFonts.Headline.medium,
                    lineHeight = PassMarkFonts.Headline.medium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                CustomSpacer()
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = SettingsScreen.CHANGE_PASSWORD_DESCRIPTION,
                    fontFamily = PassMarkFonts.font,
                    fontSize = PassMarkFonts.Label.medium,
                    lineHeight = PassMarkFonts.Label.medium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                CustomSpacer()
                val cardShape = RoundedCornerShape(size = corners)
                val cardModifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = cardShape)
                    .background(color = MaterialTheme.colorScheme.surfaceContainer)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                        shape = cardShape
                    )

                val oldPass = changePasswordViewModel.oldPassword.collectAsState()
                val newPass = changePasswordViewModel.newPassword.collectAsState()
                val newPassRepeat = changePasswordViewModel.newPasswordRepeated.collectAsState()

                Box(
                    modifier = cardModifier,
                    contentAlignment = Alignment.Center,
                    content = {
                        PasswordTextField(
                            label = "Enter current password",
                            text = oldPass.value,
                            onTextChanged = {
                                changePasswordViewModel.oldPassword.value = it
                            },
                            isEnabled = !screenState.isLoading
                        )
                    }
                )

                @Composable
                fun ErrorText(
                    text: String,
                    visible: Boolean
                ) {
                    AnimatedVisibility(
                        modifier = Modifier.fillMaxWidth(),
                        visible = visible && changePasswordViewModel.showError.collectAsState().value,
                        content = {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                text = text,
                                fontFamily = PassMarkFonts.font,
                                fontSize = PassMarkFonts.Body.medium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    )
                }

                val lengthCheckState = remember {
                    derivedStateOf { PasswordTextState.getEState(password = newPass.value) }
                }
                val passwordsMatchState = remember {
                    derivedStateOf { newPass.value == newPassRepeat.value }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = (spacing / 2)),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center,
                    content = {
                        ErrorText(
                            text = lengthCheckState.value.getMessage(),
                            visible = lengthCheckState.value != PasswordTextState.OK_LENGTH
                        )
                        ErrorText(
                            text = "New and repeated passwords do not match",
                            visible = !passwordsMatchState.value
                        )
                    }
                )
                Column(
                    modifier = cardModifier,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    content = {
                        PasswordTextField(
                            label = "Enter new password",
                            text = newPass.value,
                            onTextChanged = {
                                changePasswordViewModel.newPassword.value = it
                            },
                            isEnabled = !screenState.isLoading
                        )
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.surfaceContainerHighest
                        )
                        PasswordTextField(
                            label = "Repeat new password",
                            text = newPassRepeat.value,
                            onTextChanged = {
                                changePasswordViewModel.newPasswordRepeated.value = it
                            },
                            isEnabled = !screenState.isLoading
                        )
                    }
                )
                CustomSpacer()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = spacing,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    content = {
                        @Composable
                        fun RowScope.CustomButton(
                            isPrimary: Boolean,
                            text: String,
                            onClick: () -> Unit
                        ) {
                            Box(
                                modifier = Modifier
                                    .setSizeLimitation()
                                    .weight(weight = 1f)
                                    .clip(shape = RoundedCornerShape(size = corners))
                                    .background(
                                        color =
                                            if (isPrimary) MaterialTheme.colorScheme.primaryContainer
                                            else MaterialTheme.colorScheme.surfaceContainer
                                    )
                                    .clickable(
                                        enabled = !screenState.isLoading,
                                        onClick = onClick
                                    ),
                                contentAlignment = Alignment.Center,
                                content = {
                                    Text(
                                        modifier = Modifier.padding(
                                            horizontal = 16.dp,
                                            vertical = 4.dp
                                        ),
                                        text = text,
                                        fontFamily = PassMarkFonts.font,
                                        fontSize = PassMarkFonts.Title.medium,
                                        fontWeight = FontWeight.SemiBold,
                                        color =
                                            if (isPrimary) MaterialTheme.colorScheme.onPrimaryContainer
                                            else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            )
                        }

                        CustomButton(
                            isPrimary = false,
                            text = "Cancel",
                            onClick = navigateUp
                        )
                        CustomButton(
                            isPrimary = true,
                            text = "Confirm",
                            onClick = {
                                if ((lengthCheckState.value == PasswordTextState.OK_LENGTH) && passwordsMatchState.value) {
                                    changePasswordViewModel.changePassword(isSilent = false)
                                } else {
                                    changePasswordViewModel.triggerErrorFlag()
                                }
                            }
                        )
                    }
                )
            }
        )
    }

    @Composable
    private fun PasswordTextField(
        label: String,
        text: String,
        onTextChanged: (String) -> Unit,
        isEnabled: Boolean
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 60.dp),
            contentAlignment = Alignment.Center,
            content = {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Password
                    ),
                    label = { Text(text = label) },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent
                    ),
                    trailingIcon = if (text.isEmpty()) {
                        null
                    } else {
                        {
                            IconButton(
                                onClick = { onTextChanged("") },
                                content = {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    },
                    singleLine = true,
                    value = text,
                    onValueChange = onTextChanged,
                    enabled = isEnabled
                )
            }
        )
    }
}

@Composable
@MobilePreview
fun ChangePasswordScreenPreview() {
    ChangePasswordScreen.Screen(
        modifier = Modifier.fillMaxSize(),
        changePasswordViewModel = ChangePasswordViewModel(
            vaultApi = VaultApi(supabaseClient = SupabaseModule.mockClient)
        ),
        navigateUp = {}
    )
}