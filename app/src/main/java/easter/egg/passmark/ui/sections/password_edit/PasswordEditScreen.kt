package easter.egg.passmark.ui.sections.password_edit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import easter.egg.passmark.data.shared.PassMarkDimensions
import easter.egg.passmark.data.shared.PassMarkFonts
import easter.egg.passmark.data.shared.setSizeLimitation
import easter.egg.passmark.utils.annotation.MobileHorizontalPreview
import easter.egg.passmark.utils.annotation.MobilePreview

object PasswordEditScreen {
    @Composable
    fun Screen(
        modifier: Modifier,
        viewModel: PasswordEditViewModel,
        navigateBack: () -> Unit
    ) {
        val barModifier = Modifier
            .fillMaxWidth()
            .heightIn(min = PassMarkDimensions.minTouchSize)
        Scaffold(
            modifier = modifier,
            topBar = {
                EditTopBar(
                    modifier = barModifier,
                    navigateBack = navigateBack
                )
            },
            content = {
                EditContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = it),
                    viewModel = viewModel
                )
            },
            bottomBar = { EditBottomBar(modifier = barModifier) }
        )
    }

    @Composable
    private fun EditTopBar(
        modifier: Modifier,
        navigateBack: () -> Unit
    ) {
        Row(
            modifier = modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterHorizontally
            ),
            content = {
                Box(
                    modifier = Modifier
                        .size(size = PassMarkDimensions.minTouchSize)
                        .clickable(onClick = navigateBack)
                        .clip(shape = CircleShape)
                        .background(color = MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                    content = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                )
                Spacer(
                    modifier = Modifier
                        .weight(weight = 1f)
                        .height(height = PassMarkDimensions.minTouchSize)
                )
                val pillShape = RoundedCornerShape(size = PassMarkDimensions.minTouchSize)
                Row(
                    modifier = Modifier
                        .setSizeLimitation()
                        .clip(shape = pillShape)
                        .background(color = MaterialTheme.colorScheme.primaryContainer)
                        .clickable(
                            onClick = { TODO() }
                        )
                        .padding(
                            start = 12.dp,
                            end = 20.dp
                        ),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 4.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    content = {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                        Text(
                            text = "Folder Name",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = PassMarkFonts.Body.medium,
                            fontWeight = FontWeight.Medium,
                            fontFamily = PassMarkFonts.font,
                        )
                    }
                )

                Box(
                    modifier = Modifier
                        .setSizeLimitation()
                        .clip(shape = pillShape)
                        .background(color = MaterialTheme.colorScheme.primary)
                        .clickable(
                            onClick = { TODO() }
                        )
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.Center,
                    content = {
                        Text(
                            text = "Save",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = PassMarkFonts.Body.medium,
                            fontWeight = FontWeight.Medium,
                            fontFamily = PassMarkFonts.font,
                        )
                    }
                )
            }
        )
    }

    @Composable
    private fun EditContent(
        modifier: Modifier,
        viewModel: PasswordEditViewModel
    ) {
        Column(
            modifier = modifier.padding(horizontal = 16.dp),
            content = {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height = 16.dp)
                )
                val textFieldModifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 72.dp)
                    .padding(horizontal = 8.dp)
                DefaultCard(
                    modifier = Modifier.fillMaxWidth(),
                    content = {
                        CustomTextField(
                            modifier = textFieldModifier,
                            leadingIcon = null,
                            label = "Title",
                            placeHolder = "Untitled",
                            text = viewModel.title.collectAsState().value,
                            onTextChange = { viewModel.updateTitle(newValue = it) },
                            textStyle = LocalTextStyle.current.copy(fontSize = PassMarkFonts.Title.large)
                        )
                    }
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height = 32.dp)
                )
                DefaultCard(
                    modifier = Modifier.fillMaxWidth(),
                    content = {
                        CustomTextField(
                            modifier = textFieldModifier,
                            leadingIcon = Icons.Outlined.Email,
                            label = "Email",
                            placeHolder = "abc@def.xyz",
                            text = viewModel.email.collectAsState().value,
                            onTextChange = { viewModel.updateEmail(newValue = it) }
                        )
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.surfaceContainerHighest
                        )
                        CustomTextField(
                            modifier = textFieldModifier,
                            leadingIcon = Icons.Outlined.AccountCircle,
                            label = "UserName",
                            placeHolder = "John Doe",
                            text = viewModel.userName.collectAsState().value,
                            onTextChange = { viewModel.updateUserName(newValue = it) }
                        )
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.surfaceContainerHighest
                        )
                        CustomTextField(
                            modifier = textFieldModifier,
                            leadingIcon = Icons.Default.Edit,
                            label = "Password",
                            placeHolder = "",
                            text = viewModel.password.collectAsState().value,
                            onTextChange = { viewModel.updatePassword(newValue = it) }
                        )
                    }
                )
                val spacerModifier = Modifier
                    .fillMaxWidth()
                    .height(height = 16.dp)
                Spacer(modifier = spacerModifier)
                DefaultCard(
                    modifier = Modifier.fillMaxWidth(),
                    content = {
                        CustomTextField(
                            modifier = textFieldModifier,
                            leadingIcon = Icons.Default.Info,
                            label = "Website",
                            placeHolder = "Https://",
                            text = viewModel.website.collectAsState().value,
                            onTextChange = { viewModel.updateWebsite(newValue = it) }
                        )
                    }
                )
                Spacer(modifier = spacerModifier)
                DefaultCard(
                    modifier = Modifier.fillMaxWidth(),
                    content = {
                        CustomTextField(
                            modifier = textFieldModifier,
                            leadingIcon = Icons.Default.Info,
                            label = "notes",
                            placeHolder = "Add a note",
                            text = viewModel.notes.collectAsState().value,
                            onTextChange = { viewModel.updateNotes(newValue = it) }
                        )
                    }
                )
                Spacer(modifier = spacerModifier)
                DefaultCard(
                    modifier = Modifier.fillMaxWidth(),
                    content = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 16.dp,
                                    vertical = 8.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            content = {
                                Text(
                                    modifier = Modifier.weight(1f),
                                    fontSize = PassMarkFonts.Body.medium,
                                    fontFamily = PassMarkFonts.font,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    text = "Use fingerprint to access"
                                )
                                Switch(
                                    checked = viewModel.useFingerPrint.collectAsState().value,
                                    onCheckedChange = { viewModel.updateUseFingerPrint(newValue = it) }
                                )
                            }
                        )
                    }
                )
                Spacer(modifier = spacerModifier)
            }
        )
    }

    @Composable
    fun DefaultCard(
        modifier: Modifier,
        content: @Composable ColumnScope.() -> Unit
    ) {
        val shape = RoundedCornerShape(size = 12.dp)
        Column(
            modifier = modifier
                .clip(shape)
                .background(color = MaterialTheme.colorScheme.surfaceContainer)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    shape = shape
                ),
            content = content
        )
    }

    @Composable
    fun CustomTextField(
        modifier: Modifier,
        leadingIcon: ImageVector?,
        label: String,
        placeHolder: String,
        text: String,
        onTextChange: (String) -> Unit,
        textStyle: TextStyle = LocalTextStyle.current
    ) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
            content = {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = label) },
                    placeholder = { Text(text = placeHolder) },
                    leadingIcon = leadingIcon?.let {
                        {
                            Icon(
                                imageVector = it,
                                contentDescription = null
                            )
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent
                    ),
                    value = text,
                    onValueChange = onTextChange,
                    textStyle = textStyle,
                    trailingIcon = text.takeUnless { it.isEmpty() }?.let {
                        {
                            IconButton(
                                onClick = { onTextChange("") },
                                content = {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                )
            }
        )
    }

    @Composable
    private fun EditBottomBar(
        modifier: Modifier
    ) {
        // TODO: pending
    }
}

@Composable
@MobilePreview
@MobileHorizontalPreview
private fun PasswordEditScreenPreview() {
    PasswordEditScreen.Screen(
        modifier = Modifier.fillMaxSize(),
        viewModel = PasswordEditViewModel(),
        navigateBack = {}
    )
}