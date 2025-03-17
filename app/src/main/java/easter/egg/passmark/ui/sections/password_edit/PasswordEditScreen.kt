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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import easter.egg.passmark.data.shared.PassMarkDimensions
import easter.egg.passmark.data.shared.PassMarkFonts
import easter.egg.passmark.data.shared.setSizeLimitation
import easter.egg.passmark.utils.annotation.MobileHorizontalPreview
import easter.egg.passmark.utils.annotation.MobilePreview

object PasswordEditScreen {
    @Composable
    fun Screen(modifier: Modifier) {
        // TODO: take [title], [email, username, password], [website], [note]
        val barModifier = Modifier
            .fillMaxWidth()
            .heightIn(min = PassMarkDimensions.minTouchSize)
        Scaffold(
            modifier = modifier,
            topBar = { EditTopBar(modifier = barModifier) },
            content = {
                EditContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = it)
                )
            },
            bottomBar = { EditBottomBar(modifier = barModifier) }
        )
    }

    @Composable
    private fun EditTopBar(
        modifier: Modifier
    ) {
        Row(
            modifier = modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            content = {
                Box(
                    modifier = Modifier
                        .size(size = PassMarkDimensions.minTouchSize)
                        .clickable(
                            onClick = {
                                TODO()
                            }
                        )
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

                Button(
                    modifier = Modifier.setSizeLimitation(),
                    onClick = { TODO() },
                    content = { Text(text = "Save") }
                )
            }
        )
        // TODO: pending
    }

    @Composable
    private fun EditContent(
        modifier: Modifier
    ) {
        Column(
            modifier = modifier.padding(horizontal = 16.dp),
            content = {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height = 16.dp)
                )
                DefaultCard(
                    modifier = Modifier.fillMaxWidth(),
                    content = {
                        val title = remember { mutableStateOf("") }
                        CustomTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 8.dp),
                            leadingIcon = null,
                            label = "Title",
                            placeHolder = "Untitled",
                            text = title.value,
                            onTextChange = { title.value = it },
                            textStyle = LocalTextStyle.current.copy(fontSize = PassMarkFonts.Title.large)
                        )
                    }
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height = 32.dp)
                )
                val textFieldModifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp)

                val email = remember { mutableStateOf("") }
                val userName = remember { mutableStateOf("") }
                val password = remember { mutableStateOf("") }
                DefaultCard(
                    modifier = Modifier.fillMaxWidth(),
                    content = {
                        CustomTextField(
                            modifier = textFieldModifier,
                            leadingIcon = Icons.Outlined.Email,
                            label = "Email",
                            placeHolder = "abc@def.xyz",
                            text = email.value,
                            onTextChange = { email.value = it }
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
                            text = userName.value,
                            onTextChange = { userName.value = it }
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
                            text = password.value,
                            onTextChange = { password.value = it }
                        )
                    }
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height = 16.dp)
                )

                val website = remember { mutableStateOf("") }
                DefaultCard(
                    modifier = Modifier.fillMaxWidth(),
                    content = {
                        CustomTextField(
                            modifier = textFieldModifier,
                            leadingIcon = Icons.Default.Info,
                            label = "Website",
                            placeHolder = "Https://",
                            text = website.value,
                            onTextChange = { website.value = it }
                        )
                    }
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height = 16.dp)
                )
                val notes = remember { mutableStateOf("") }
                DefaultCard(
                    modifier = Modifier.fillMaxWidth(),
                    content = {
                        CustomTextField(
                            modifier = textFieldModifier,
                            leadingIcon = Icons.Default.Info,
                            label = "notes",
                            placeHolder = "Add a note",
                            text = notes.value,
                            onTextChange = { notes.value = it }
                        )
                    }
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height = 16.dp)
                )
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
        TextField(
            modifier = modifier,
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
            textStyle = textStyle
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
    PasswordEditScreen.Screen(modifier = Modifier.fillMaxSize())
}

@Composable
@Preview(
    widthDp = 360,
    heightDp = 180,
    showBackground = true
)
fun CustomTextFieldPreview() {
    PasswordEditScreen.CustomTextField(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
//            .requiredHeight(height = 96.dp)
            .padding(all = 16.dp)
            .clip(shape = RoundedCornerShape(size = 8.dp))
            .background(color = MaterialTheme.colorScheme.primaryContainer),
        leadingIcon = Icons.Default.Search,
        label = "Some Heading",
        placeHolder = "Some Hint",
        text = "hi",
        onTextChange = {}
    )
}