package easter.egg.passmark.ui.sections.user_edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import easter.egg.passmark.data.shared.PassMarkFonts
import easter.egg.passmark.data.shared.setSizeLimitation
import easter.egg.passmark.utils.annotation.MobileHorizontalPreview
import easter.egg.passmark.utils.annotation.MobilePreview

object UserEditScreen {
    @Composable
    fun Screen(
        modifier: Modifier,
        viewModel: UserEditViewModel,
        isNewUser: Boolean
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(state = scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 12.dp,
                alignment = Alignment.CenterVertically
            ),
            content = {
                val spacerModifier = Modifier
                    .fillMaxWidth()
                    .height(height = 8.dp)
                Spacer(modifier = spacerModifier)
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text =
                        if (isNewUser) "Create a Master Key"
                        else "Confirm your master key",
                    fontFamily = PassMarkFonts.font,
                    fontSize = PassMarkFonts.Headline.medium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text =
                        if (isNewUser) "Make sure to remember your master key. Without this, account recovery would be impossible."
                        else "Enter the master key you used to create your account.",
                    fontFamily = PassMarkFonts.font,
                    fontSize = PassMarkFonts.Body.medium,
                    fontWeight = FontWeight.Medium
                )
                val errorText: String? =
                    viewModel.masterPasswordText.collectAsState().value.length.let {
                        when {
                            it >= 32 -> "Password should be less than 32 in length"
                            it <= 8 -> "Password should be more than 8 in length"
                            else -> null
                        }
                    }
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.masterPasswordText.collectAsState().value,
                    onValueChange = viewModel::updateMasterPasswordText,
                    label = { Text(text = "Master Password") },
                    placeholder = { Text(text = "Secure Password") },
                    isError = (viewModel.showError.collectAsState().value && (errorText != null)),
                    supportingText = { Text(text = errorText ?: "Password is of correct length") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Password,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = viewModel::updateVisibility,
                            content = {
                                Icon(
                                    imageVector = viewModel.visible.collectAsState().value.let {
                                        if (it) Icons.Default.Visibility else Icons.Default.VisibilityOff
                                    },
                                    contentDescription = null
                                )
                            }
                        )
                    }
                )
                Button(
                    modifier = Modifier
                        .setSizeLimitation()
                        .align(Alignment.End),
                    onClick = { TODO() },
                    content = {
                        Box(
                            modifier = Modifier,
                            contentAlignment = Alignment.Center,
                            content = {
                                val isLoading =
                                    viewModel.screenState.collectAsState().value.isLoading
                                Text(
                                    modifier = Modifier.alpha(alpha = if (isLoading) 0f else 1f),
                                    text = "Create"
                                )
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(size = 24.dp),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        )
                    }
                )
                Spacer(modifier = spacerModifier)
            }
        )
    }
}

@Composable
@MobilePreview
@MobileHorizontalPreview
private fun UserEditScreenPreview() {
    Column {
        UserEditScreen.Screen(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            viewModel = UserEditViewModel(),
            isNewUser = false
        )
        UserEditScreen.Screen(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            viewModel = UserEditViewModel(),
            isNewUser = true
        )
    }
}