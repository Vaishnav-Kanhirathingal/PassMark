package easter.egg.passmark.ui.sections.user_edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = if (isNewUser)"Create a Master Key" else "Confirm your master key",
                    fontFamily = PassMarkFonts.font,
                    fontSize = PassMarkFonts.Headline.medium,
                )
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "make sure to remember your master key. Without this, account recovery would be impossible",
                    fontFamily = PassMarkFonts.font,
                    fontSize = PassMarkFonts.Body.medium,
                )
                val errorText: String? =
                    viewModel.masterPasswordText.collectAsState().value.length.let {
                        when {
                            it >= 32 -> "password should be less than 32 in length"
                            it <= 8 -> "password should be more than 8 in length"
                            else -> null
                        }
                    }
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.masterPasswordText.collectAsState().value,
                    onValueChange = viewModel::updateMasterPasswordText,
                    label = { Text(text = "Master Password") },
                    placeholder = { Text(text = "Secure Password") },
                    isError = (errorText != null),
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
                        Text("Create")
                    }
                )
            }
        )
    }
}

@Composable
@MobilePreview
@MobileHorizontalPreview
fun UserEditScreenPreview() {
    UserEditScreen.Screen(
        modifier = Modifier.fillMaxSize(),
        viewModel = UserEditViewModel()
    )
}