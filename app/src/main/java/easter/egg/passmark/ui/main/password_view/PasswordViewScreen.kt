package easter.egg.passmark.ui.main.password_view

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.Visibility
import androidx.fragment.app.FragmentActivity
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import easter.egg.passmark.data.models.content.Password
import easter.egg.passmark.data.models.content.PasswordData
import easter.egg.passmark.data.models.content.Vault
import easter.egg.passmark.data.models.content.Vault.Companion.getIcon
import easter.egg.passmark.utils.annotation.MobileHorizontalPreview
import easter.egg.passmark.utils.annotation.MobilePreview
import easter.egg.passmark.utils.security.biometrics.BiometricsHandler
import easter.egg.passmark.utils.values.PassMarkDimensions
import easter.egg.passmark.utils.values.PassMarkFonts
import easter.egg.passmark.utils.values.setSizeLimitation
import java.time.ZoneId

object PasswordViewScreen {
    private val TAG = this::class.simpleName

    @Composable
    fun Screen(
        modifier: Modifier,
        password: Password,
        navigateUp: () -> Unit,
        toEditScreen: () -> Unit,
        associatedVault: Vault?
    ) {
        Scaffold(
            modifier = modifier,
            topBar = {
                PasswordViewTopBar(
                    modifier = Modifier.fillMaxWidth(),
                    navigateUp = navigateUp,
                    toEditScreen = toEditScreen,
                )
            },
            content = {
                PasswordViewContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = it),
                    password = password,
                    associatedVault = associatedVault
                )
            }
        )
    }

    @Composable
    private fun PasswordViewTopBar(
        modifier: Modifier,
        navigateUp: () -> Unit,
        toEditScreen: () -> Unit
    ) {
        val barSize = PassMarkDimensions.minTouchSize
        Row(
            modifier = modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.Start
            ),
            verticalAlignment = Alignment.CenterVertically,
            content = {
                Box(
                    modifier = Modifier
                        .size(size = barSize)
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
                Spacer(modifier = Modifier.weight(weight = 1f))
                Row(
                    modifier = Modifier
                        .setSizeLimitation()
                        .height(height = barSize)
                        .widthIn(min = barSize)
                        .clip(shape = CircleShape)
                        .background(color = MaterialTheme.colorScheme.primaryContainer)
                        .clickable(onClick = toEditScreen)
                        .padding(
                            start = 20.dp,
                            end = 24.dp
                        ),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 8.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    content = {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Edit",
                            fontFamily = PassMarkFonts.font,
                            fontSize = PassMarkFonts.Title.medium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                )
            }
        )
    }

    @Composable
    private fun PasswordViewContent(
        modifier: Modifier,
        password: Password,
        associatedVault: Vault?
    ) {
        Column(
            modifier = modifier.verticalScroll(state = rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 16.dp,
                alignment = Alignment.Top
            ),
            content = {
                val biometricAuthenticated = remember { mutableStateOf(false) }
                val context = LocalContext.current
                fun showBiometricPrompt() {
                    (context as? FragmentActivity)?.let {
                        BiometricsHandler.performBiometricAuthentication(
                            activity = it,
                            onComplete = {},
                            onSuccess = { biometricAuthenticated.value = true },
                            showToast = { s ->
                                Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
                Heading(
                    modifier = Modifier.fillMaxWidth(),
                    password = password,
                    associatedVault = associatedVault
                )
                val itemModifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                val displayFieldContentModifier = Modifier.fillMaxWidth()
                val clipboardManager = LocalClipboardManager.current
                fun copy(str: String) {
                    copyToClipBoard(
                        clipboardManager = clipboardManager,
                        context = context,
                        str = str
                    )
                }
                PropertyListCard(
                    modifier = itemModifier,
                    itemList = mutableListOf<@Composable () -> Unit>().apply {
                        password.data.email?.let { email ->
                            this.add(
                                element = {
                                    DisplayFieldContent(
                                        modifier = displayFieldContentModifier,
                                        startIcon = Icons.Default.Email,
                                        titleText = "Email",
                                        fieldText = email,
                                        endIcon = Icons.Default.ContentCopy,
                                        endIconOnClick = { copy(str = email) }
                                    )
                                }
                            )
                        }
                        password.data.userName?.let { username ->
                            this.add(
                                element = {
                                    DisplayFieldContent(
                                        modifier = displayFieldContentModifier,
                                        startIcon = Icons.Default.Person,
                                        titleText = "Username",
                                        fieldText = username,
                                        endIcon = Icons.Default.ContentCopy,
                                        endIconOnClick = { copy(str = username) }
                                    )
                                }
                            )
                        }
                        this.add(
                            element = {
                                DisplayFieldContent(
                                    modifier = displayFieldContentModifier,
                                    startIcon = Icons.Default.Password,
                                    titleText = "Password",
                                    fieldText =
                                        if (biometricAuthenticated.value) password.data.password
                                        else "************",
                                    endIcon =
                                        if (biometricAuthenticated.value) Icons.Default.ContentCopy
                                        else Icons.Default.Fingerprint,
                                    endIconOnClick = {
                                        if (biometricAuthenticated.value) copy(str = password.data.password)
                                        else showBiometricPrompt()
                                    }
                                )
                            }
                        )
                    },
                )

                password.data.website?.let { website ->
                    DefaultCard(
                        modifier = itemModifier,
                        content = {
                            DisplayFieldContent(
                                modifier = Modifier.fillMaxWidth(),
                                startIcon = Icons.Default.Web,
                                titleText = "Website",
                                fieldText = website,
                                endIcon = Icons.Default.ContentCopy,
                                endIconOnClick = { copy(str = website) }
                            )
                        }
                    )
                }
                password.data.notes?.let { notes ->
                    DefaultCard(
                        modifier = itemModifier,
                        content = {
                            DisplayFieldContent(
                                modifier = Modifier.fillMaxWidth(),
                                startIcon = Icons.Default.EditNote,
                                titleText = "Notes",
                                fieldText = notes
                            )
                        }
                    )
                }
                PropertyListCard(
                    modifier = itemModifier,
                    itemList = listOf(
                        {
                            DisplayFieldContent(
                                modifier = Modifier.fillMaxWidth(),
                                startIcon = Icons.Default.Cloud,
                                titleText = "Storage Type",
                                fieldText = if (password.data.saveToLocalOnly) "Saved to device only" else "Saved on the cloud"
                            )
                        },
                        {
                            DisplayFieldContent(
                                modifier = Modifier.fillMaxWidth(),
                                startIcon = Icons.Default.Fingerprint,
                                titleText = "Fingerprint Authentication",
                                fieldText = if (password.data.useFingerPrint) "Enabled" else "Disabled"
                            )
                        }
                    ),
                )
                val deleteShape = RoundedCornerShape(size = 16.dp)
                fun Long.formatToTime(): String {
                    return try {
                        val instant = java.time.Instant.ofEpochMilli(this)
                        val dateTime =
                            java.time.LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                        val formatted =
                            dateTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss | dd/MM/yyyy"))
                        formatted!!
                    } catch (e: Exception) {
                        e.printStackTrace()
                        "Time Error"
                    }
                }
                PropertyListCard(
                    modifier = itemModifier,
                    itemList = mutableListOf<@Composable () -> Unit>().apply {
                        this.add(
                            element = {
                                DisplayFieldContent(
                                    modifier = displayFieldContentModifier,
                                    startIcon = Icons.Default.CalendarToday,
                                    titleText = "Created",
                                    fieldText = password.created.formatToTime()
                                )
                            }
                        )
                        this.add(
                            element = {
                                DisplayFieldContent(
                                    modifier = displayFieldContentModifier,
                                    startIcon = Icons.Default.EditCalendar,
                                    titleText = "Updated",
                                    fieldText = password.lastModified.formatToTime()
                                )
                            }
                        )
                        this.add(
                            element = {
                                DisplayFieldContent(
                                    modifier = displayFieldContentModifier,
                                    startIcon = Icons.Default.EventRepeat,
                                    titleText = "Last Used",
                                    fieldText = password.lastUsed.formatToTime()
                                )
                            }
                        )
                    },
                )
                Box(
                    modifier = Modifier
                        .align(alignment = Alignment.End)
                        .setSizeLimitation()
                        .padding(horizontal = 16.dp)
                        .clip(shape = deleteShape)
                        .background(color = MaterialTheme.colorScheme.error)
                        .clickable(onClick = { TODO() }),
                    contentAlignment = Alignment.Center,
                    content = {
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            fontFamily = PassMarkFonts.font,
                            fontSize = PassMarkFonts.Body.medium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onError,
                            text = "Delete"
                        )
                    }
                )

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height = PassMarkDimensions.minTouchSize * 3)
                )
            }
        )
    }

    @Composable
    private fun Heading(
        password: Password,
        modifier: Modifier,
        associatedVault: Vault?
    ) {
        ConstraintLayout(
            modifier = modifier,
            content = {
                val (passwordIcon, title, subtitle) = createRefs()
                Box(
                    modifier = Modifier
                        .size(size = 100.dp)
                        .clip(shape = RoundedCornerShape(size = 24.dp))
                        .background(color = MaterialTheme.colorScheme.surfaceContainer)
                        .constrainAs(
                            ref = passwordIcon,
                            constrainBlock = {
                                this.top.linkTo(anchor = parent.top, margin = 16.dp)
                                this.bottom.linkTo(anchor = parent.bottom, margin = 16.dp)
                                this.start.linkTo(anchor = parent.start, margin = 16.dp)
                            }
                        ),
                    contentAlignment = Alignment.Center,
                    content = {
                        val showText: MutableState<Boolean> =
                            remember { mutableStateOf(true) }
                        val model = ImageRequest.Builder(LocalContext.current)
                            .data(password.data.getFavicon())
                            .crossfade(true)
                            .listener(onSuccess = { _, _ -> showText.value = false })
                            .build()
                        if (showText.value) {
                            Text(
                                textAlign = TextAlign.Center,
                                fontFamily = PassMarkFonts.font,
                                fontSize = PassMarkFonts.Display.medium,
                                fontWeight = FontWeight.Bold,
                                text = password.data.getShortName(),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        AsyncImage(
                            model = model,
                            modifier = Modifier.size(size = 72.dp),
                            contentScale = ContentScale.Fit,
                            contentDescription = null,
                            imageLoader = ImageLoader(context = LocalContext.current),
                        )
                    }
                )
                Text(
                    modifier = Modifier.constrainAs(
                        ref = title,
                        constrainBlock = {
                            this.top.linkTo(anchor = parent.top, margin = 16.dp)
                            this.bottom.linkTo(subtitle.top)
                            this.start.linkTo(anchor = passwordIcon.end, margin = 16.dp)
                            this.end.linkTo(anchor = parent.end, margin = 16.dp)
                            width = Dimension.fillToConstraints
                        }
                    ),
                    text = password.data.title,
                    fontFamily = PassMarkFonts.font,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = PassMarkFonts.Display.medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(size = 24.dp))
                        .background(color = MaterialTheme.colorScheme.surfaceContainer)
                        .padding(
                            vertical = 2.dp,
                            horizontal = 4.dp
                        )
                        .constrainAs(
                            ref = subtitle,
                            constrainBlock = {
                                this.start.linkTo(anchor = passwordIcon.end, margin = 16.dp)
                                this.top.linkTo(anchor = title.bottom)
                                width = Dimension.wrapContent
                            }
                        ),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 4.dp,
                        alignment = Alignment.Start
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    content = {
                        Icon(
                            modifier = Modifier.size(size = 16.dp),
                            imageVector = associatedVault.getIcon(),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .widthIn(max = 120.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = associatedVault?.name ?: Vault.VAULT_NAME_FOR_ALL_ITEMS,
                            fontFamily = PassMarkFonts.font,
                            fontSize = PassMarkFonts.Label.small,
                            lineHeight = PassMarkFonts.Label.small,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
            }
        )
    }

    @Composable
    private fun PropertyListCard(
        itemList: List<@Composable () -> Unit>,
        modifier: Modifier,
    ) {
        DefaultCard(
            modifier = modifier,
            content = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    content = {
                        itemList.forEachIndexed { index, composable ->
                            composable()
                            if (index != itemList.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.surfaceContainerHighest
                                )
                            }
                        }
                    }
                )
            }
        )
    }

    @Composable
    private fun DefaultCard(
        modifier: Modifier,
        content: @Composable BoxScope.() -> Unit
    ) {
        val shape = RoundedCornerShape(size = 16.dp)
        Box(
            modifier = modifier
                .clip(shape = shape)
                .background(color = MaterialTheme.colorScheme.surfaceContainer)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    shape = shape
                ),
            contentAlignment = Alignment.Center,
            content = content
        )
    }

    /** make sure to have both [endIcon] and [endIconOnClick] as either null or non null
     */
    @Composable
    private fun DisplayFieldContent(
        modifier: Modifier,
        startIcon: ImageVector,
        titleText: String,
        fieldText: String,
        endIcon: ImageVector? = null,
        endIconOnClick: (() -> Unit)? = null
    ) {
        ConstraintLayout(
            modifier = modifier
                .setSizeLimitation()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            content = {
                val (startIconRef, titleRef, contentRef, endIconRef) = createRefs()
                Icon(
                    modifier = Modifier.constrainAs(
                        ref = startIconRef,
                        constrainBlock = {
                            this.top.linkTo(parent.top)
                            this.bottom.linkTo(parent.bottom)
                            this.start.linkTo(parent.start)
                        }
                    ),
                    imageVector = startIcon,
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = null
                )
                Text(
                    modifier = Modifier.constrainAs(
                        ref = titleRef,
                        constrainBlock = {
                            this.top.linkTo(parent.top)
                            this.bottom.linkTo(contentRef.top)
                            this.start.linkTo(anchor = startIconRef.end, margin = 24.dp)
                            this.end.linkTo(endIconRef.start)
                            width = Dimension.fillToConstraints
                        }
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = PassMarkFonts.font,
                    fontSize = PassMarkFonts.Label.medium,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = titleText,
                )
                Text(
                    modifier = Modifier.constrainAs(
                        ref = contentRef,
                        constrainBlock = {
                            this.top.linkTo(titleRef.bottom)
                            this.bottom.linkTo(parent.bottom)
                            this.start.linkTo(anchor = startIconRef.end, margin = 24.dp)
                            this.end.linkTo(endIconRef.start)
                            width = Dimension.fillToConstraints
                        }
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = PassMarkFonts.font,
                    fontSize = PassMarkFonts.Title.medium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = fieldText,
                )
                IconButton(
                    modifier = Modifier
                        .constrainAs(
                            ref = endIconRef,
                            constrainBlock = {
                                this.top.linkTo(parent.top)
                                this.end.linkTo(parent.end)
                                this.bottom.linkTo(parent.bottom)
                                visibility =
                                    if (endIcon == null) Visibility.Gone
                                    else Visibility.Visible
                            }
                        ),
                    onClick = { endIconOnClick?.invoke() },
                    content = {
                        endIcon?.let {
                            Icon(
                                imageVector = it,
                                contentDescription = null
                            )
                        }
                    }
                )
                createVerticalChain(titleRef, contentRef)
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DeleteDialog(
        modifier: Modifier
    ) {
        BasicAlertDialog(
            modifier = modifier,
            onDismissRequest = { TODO() },
            content = {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(size = 16.dp)
                        ),
                    content = {
                        val (title, subtitle, cancelButton, deleteButton) = createRefs()
                        Text(
                            modifier = Modifier.constrainAs(
                                ref = title,
                                constrainBlock = {
                                    this.top.linkTo(anchor = parent.top, margin = 16.dp)
                                    this.start.linkTo(anchor = parent.start, margin = 16.dp)
                                    this.end.linkTo(anchor = parent.end, margin = 16.dp)
                                    width = Dimension.fillToConstraints
                                }
                            ),
                            fontFamily = PassMarkFonts.font,
                            fontSize = PassMarkFonts.Headline.medium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            text = "Delete?"
                        )
                        Text(
                            modifier = Modifier.constrainAs(
                                ref = subtitle,
                                constrainBlock = {
                                    this.top.linkTo(anchor = title.bottom, margin = 8.dp)
                                    this.start.linkTo(title.start)
                                    this.end.linkTo(title.end)
                                    width = Dimension.fillToConstraints
                                }
                            ),
                            text = "Note : Deleting this password is unrecoverable/irreversible. " +
                                    "You can instead transfer this password to another vault if " +
                                    "you wish so.",
                            fontFamily = PassMarkFonts.font,
                            fontSize = PassMarkFonts.Body.medium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        @Composable
                        fun CustomButton(
                            modifier: Modifier,
                            text: String,
                            isLoading: Boolean,
                            color: Color
                        ) {
                            Box(
                                modifier = modifier.setSizeLimitation(),
                                contentAlignment = Alignment.Center,
                                content = {
                                    if (isLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(size = 24.dp),
                                            color = color,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text(
                                            text = text,
                                            fontFamily = PassMarkFonts.font,
                                            fontSize = PassMarkFonts.Title.medium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = color
                                        )
                                    }
                                }
                            )
                        }
//                        createHorizontalChain(cancelButton,deleteButton)
                        CustomButton(
                            modifier = Modifier.constrainAs(
                                ref = cancelButton,
                                constrainBlock = {
                                    this.start.linkTo(parent.start)
                                    this.end.linkTo(deleteButton.start)
                                    this.top.linkTo(subtitle.bottom)
                                    this.bottom.linkTo(parent.bottom)
                                    width = Dimension.fillToConstraints
                                }
                            ),
                            text = "Cancel",
                            isLoading = false,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        CustomButton(
                            modifier = Modifier.constrainAs(
                                ref = deleteButton,
                                constrainBlock = {
                                    this.start.linkTo(cancelButton.end)
                                    this.end.linkTo(parent.end)
                                    this.top.linkTo(subtitle.bottom)
                                    this.bottom.linkTo(parent.bottom)
                                    width = Dimension.fillToConstraints
                                }
                            ),
                            text = "Delete",
                            isLoading = false,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                )
            },
        )
    }

    private fun copyToClipBoard(
        clipboardManager: ClipboardManager,
        context: Context,
        str: String
    ) {
        clipboardManager.setText(AnnotatedString(text = str))
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            Toast
                .makeText(
                    context,
                    "Text copied to clipboard",
                    Toast.LENGTH_LONG
                )
                .show()
        } else {
            Log.d(TAG, "system has it's own toast")
        }
    }
}

@Composable
@Preview(
    widthDp = 360,
    heightDp = 1000,
    showBackground = true
)
@MobilePreview
@MobileHorizontalPreview
private fun PasswordViewScreenPreview() {
    PasswordViewScreen.Screen(
        modifier = Modifier.fillMaxSize(),
        password = Password(
            data = PasswordData(
                title = "Title",
                email = "someEmail@gmail.com",
                userName = "SomeUserName",
                password = "SomePassword",
                website = "www.google.com",
                notes = "Some note",
                useFingerPrint = false,
                saveToLocalOnly = false,
            ),
            created = 0L,
            lastUsed = 0L,
            lastModified = 0L,
            usedCount = 0
        ),
        navigateUp = {},
        toEditScreen = {},
        associatedVault = null
    )
}

@Composable
@MobilePreview
private fun DeleteDialogPreview() {
    PasswordViewScreen.DeleteDialog(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp)
    )
}