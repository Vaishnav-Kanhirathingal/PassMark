package easter.egg.passmark.ui.shared_components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import easter.egg.passmark.utils.annotation.MobilePreview
import easter.egg.passmark.utils.annotation.PreviewRestricted
import easter.egg.passmark.utils.values.PassMarkDimensions
import easter.egg.passmark.utils.values.PassMarkFonts
import easter.egg.passmark.utils.values.setSizeLimitation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordVerificationDialog(
    modifier: Modifier,
    text: String,
    onTextChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isLoading: Boolean
) {
    BasicAlertDialog(
        modifier = modifier,
        onDismissRequest = {
            if (!isLoading) {
                onDismiss()
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(size = PassMarkDimensions.dialogRadius)
                    )
                    .padding(all = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    space = 12.dp,
                    alignment = Alignment.CenterVertically
                ),
                content = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Enter password to verify identity",
                        fontFamily = PassMarkFonts.font,
                        fontSize = PassMarkFonts.Title.medium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    val visible = remember { mutableStateOf(false) }
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = text,
                        onValueChange = onTextChange,
                        enabled = !isLoading,
                        placeholder = { Text(text = "****") },
                        label = { Text(text = "Master Key") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Password,
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                modifier = Modifier.setSizeLimitation(),
                                onClick = { TODO() },
                                content = {
                                    Icon(
                                        imageVector =
                                            if (visible.value) Icons.Default.Visibility
                                            else Icons.Default.VisibilityOff,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(
                            space = 8.dp,
                            alignment = Alignment.CenterHorizontally
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        content = {
                            Box(
                                modifier = Modifier
                                    .weight(weight = 1f)
                                    .setSizeLimitation()
                                    .clip(shape = RoundedCornerShape(size = 8.dp))
                                    .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                        shape = RoundedCornerShape(size = 8.dp)
                                    )
                                    .clickable(
                                        enabled = !isLoading,
                                        onClick = onDismiss
                                    ),
                                contentAlignment = Alignment.Center,
                                content = {
                                    Text(
                                        modifier = Modifier,
                                        text = "Cancel",
                                        fontFamily = PassMarkFonts.font,
                                        fontSize = PassMarkFonts.Body.medium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            )
                            Box(
                                modifier = Modifier
                                    .weight(weight = 1f)
                                    .setSizeLimitation()
                                    .clip(RoundedCornerShape(size = 8.dp))
                                    .background(color = MaterialTheme.colorScheme.primaryContainer)
                                    .clickable(
                                        enabled = !isLoading,
                                        onClick = onConfirm
                                    ),
                                contentAlignment = Alignment.Center,
                                content = {
                                    if (isLoading) {
                                        CustomLoader.ButtonLoader(
                                            modifier = Modifier,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    } else {
                                        Text(
                                            modifier = Modifier,
                                            text = "Confirm",
                                            fontFamily = PassMarkFonts.font,
                                            fontSize = PassMarkFonts.Body.medium,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            )
                        }
                    )
                }
            )
        }
    )
}

@PreviewRestricted
@MobilePreview
//@MobileHorizontalPreview
@Composable
private fun PasswordVerificationDialogPrev() {
    Box(
        modifier = Modifier.fillMaxSize(),
        content = {
            PasswordVerificationDialog(
                modifier = Modifier.padding(horizontal = 30.dp),
                text = "123",
                onTextChange = {},
                onConfirm = {},
                onDismiss = {},
                isLoading = false,
            )
        }
    )
}