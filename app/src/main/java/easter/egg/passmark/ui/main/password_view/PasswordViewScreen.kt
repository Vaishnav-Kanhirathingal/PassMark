package easter.egg.passmark.ui.main.password_view

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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Web
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
                        .height(height = barSize)
                        .widthIn(min = barSize)
                        .clip(shape = CircleShape)
                        .background(color = MaterialTheme.colorScheme.primaryContainer)
                        .padding(
                            start = 24.dp,
                            end = 24.dp,
                            top = 8.dp,
                            bottom = 8.dp
                        )
                        .clickable(onClick = toEditScreen),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 4.dp,
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
                            fontSize = PassMarkFonts.Body.medium,
                            fontWeight = FontWeight.Medium,
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
                PropertyListCard(
                    modifier = itemModifier,
                    passwordPropertyList = mutableListOf<PasswordProperty>().apply {
                        // TODO: email, username, password
                        password.data.email?.let { email ->
                            this.add(
                                PasswordProperty(
                                    imageVector = Icons.Default.Email,
                                    title = "Email",
                                    field = email
                                )
                            )
                        }
                        password.data.userName?.let { username ->
                            this.add(
                                PasswordProperty(
                                    imageVector = Icons.Default.Person,
                                    title = "Username",
                                    field = username
                                )
                            )
                        }
                        this.add(
                            PasswordProperty(
                                imageVector = Icons.Default.Password,
                                title = "Password",
                                field =
                                    if (biometricAuthenticated.value) password.data.password
                                    else "************",
                                isPassword = true
                            )
                        )
                    },
                    showBiometricPrompt = ::showBiometricPrompt
                )

                password.data.website?.let { website ->
                    DefaultCard(
                        modifier = itemModifier,
                        content = {
                            DisplayFieldContent(
                                modifier = Modifier.fillMaxWidth(),
                                passwordProperty = PasswordProperty(
                                    imageVector = Icons.Default.Web,
                                    title = "Website",
                                    field = website
                                ),
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
                                passwordProperty = PasswordProperty(
                                    imageVector = Icons.Default.EditNote,
                                    title = "Notes",
                                    field = notes
                                )
                            )
                        }
                    )
                }
                PropertyListCard(
                    modifier = itemModifier,
                    passwordPropertyList = listOf(
                        PasswordProperty(
                            imageVector = Icons.Default.Cloud,
                            title = "Storage Type",
                            field = if (password.data.saveToLocalOnly) "Saved to device only" else "Saved on the cloud"
                        ),
                        PasswordProperty(
                            imageVector = Icons.Default.Fingerprint,
                            title = "Fingerprint Authentication",
                            field = if (password.data.useFingerPrint) "Enabled" else "Disabled"
                        )
                    ),
                    showBiometricPrompt = ::showBiometricPrompt
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
                    passwordPropertyList = listOf(
                        PasswordProperty(
                            imageVector = Icons.Default.CalendarToday,
                            title = "Created",
                            field = password.created.formatToTime()
                        ),
                        PasswordProperty(
                            imageVector = Icons.Default.EditCalendar,
                            title = "Updated",
                            field = password.lastModified.formatToTime()
                        ),
                        PasswordProperty(
                            imageVector = Icons.Default.EventRepeat,
                            title = "Last Used",
                            field = password.lastUsed.formatToTime()
                        )
                    ),
                    showBiometricPrompt = ::showBiometricPrompt
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
        passwordPropertyList: List<PasswordProperty>,
        modifier: Modifier,
        showBiometricPrompt: () -> Unit
    ) {
        DefaultCard(
            modifier = modifier,
            content = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    content = {
                        val contentModifier = Modifier.fillMaxWidth()
                        passwordPropertyList.forEachIndexed { index, passwordProperty ->
                            DisplayFieldContent(
                                modifier = contentModifier,
                                passwordProperty = passwordProperty,
                                fingerPrintOnClick = if (passwordProperty.isPassword) showBiometricPrompt else null
                            )
                            if (index != passwordPropertyList.lastIndex) {
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

    @Composable
    private fun DisplayFieldContent(
        modifier: Modifier,
        passwordProperty: PasswordProperty,
        fingerPrintOnClick: (() -> Unit)? = null
    ) {
        ConstraintLayout(
            modifier = modifier
                .setSizeLimitation()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            content = {
                val (iconRef, titleRef, contentRef, endIcon) = createRefs()
                Icon(
                    modifier = Modifier.constrainAs(
                        ref = iconRef,
                        constrainBlock = {
                            this.top.linkTo(parent.top)
                            this.bottom.linkTo(parent.bottom)
                            this.start.linkTo(parent.start)
                        }
                    ),
                    imageVector = passwordProperty.imageVector,
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = null
                )
                Text(
                    modifier = Modifier.constrainAs(
                        ref = titleRef,
                        constrainBlock = {
                            this.top.linkTo(parent.top)
                            this.bottom.linkTo(contentRef.top)
                            this.start.linkTo(anchor = iconRef.end, margin = 24.dp)
                            this.end.linkTo(endIcon.start)
                            width = Dimension.fillToConstraints
                        }
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = PassMarkFonts.font,
                    fontSize = PassMarkFonts.Label.medium,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = passwordProperty.title,
                )
                Text(
                    modifier = Modifier.constrainAs(
                        ref = contentRef,
                        constrainBlock = {
                            this.top.linkTo(titleRef.bottom)
                            this.bottom.linkTo(parent.bottom)
                            this.start.linkTo(anchor = iconRef.end, margin = 24.dp)
                            this.end.linkTo(endIcon.start)
                            width = Dimension.fillToConstraints
                        }
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = PassMarkFonts.font,
                    fontSize = PassMarkFonts.Title.medium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = passwordProperty.field,
                )
                IconButton(
                    modifier = Modifier
                        .constrainAs(
                            ref = endIcon,
                            constrainBlock = {
                                this.top.linkTo(parent.top)
                                this.end.linkTo(parent.end)
                                this.bottom.linkTo(parent.bottom)
                                visibility =
                                    if (fingerPrintOnClick == null) Visibility.Gone
                                    else Visibility.Visible
                            }
                        ),
                    onClick = { fingerPrintOnClick?.invoke() },
                    content = {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = null
                        )
                    }
                )
                createVerticalChain(titleRef, contentRef)
            }
        )
    }
}

@Composable
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
                website = null, // "www.google.com",
                notes = null, // "Some note",
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

private class PasswordProperty(
    val imageVector: ImageVector,
    val title: String,
    val field: String,
    val isPassword: Boolean = false
)