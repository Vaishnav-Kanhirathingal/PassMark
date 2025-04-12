package easter.egg.passmark.ui.main.password_view

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import easter.egg.passmark.utils.values.PassMarkDimensions
import easter.egg.passmark.utils.values.PassMarkFonts
import easter.egg.passmark.utils.values.setSizeLimitation

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
                    onDeleteClicked = { TODO() },
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
        toEditScreen: () -> Unit,
        onDeleteClicked: () -> Unit
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

                Box(
                    modifier = Modifier
                        .size(size = barSize)
                        .clip(shape = CircleShape)
                        .background(color = MaterialTheme.colorScheme.errorContainer)
                        .clickable(onClick = onDeleteClicked),
                    contentAlignment = Alignment.Center,
                    content = {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
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
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            content = {
                ConstraintLayout(
                    modifier = Modifier.fillMaxWidth(),
                    content = {
                        val (passwordIcon, title, subtitle) = createRefs()
                        Box(
                            modifier = Modifier
                                .size(size = 100.dp)
                                .clip(shape = RoundedCornerShape(size = 24.dp))
                                .background(color = MaterialTheme.colorScheme.primaryContainer)
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
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                AsyncImage(
                                    model = model,
                                    modifier = Modifier.size(size = 24.dp),
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
                PropertyListCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
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
                                imageVector = Icons.Default.Email,
                                title = "Password",
                                field = password.data.password
                            )
                        )
                    },
                )
            }
        )
    }

    @Composable
    private fun PropertyListCard(
        passwordPropertyList: List<PasswordProperty>,
        modifier: Modifier
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
                                passwordProperty = passwordProperty
                            )
                            if (index != passwordPropertyList.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.primary
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
                .background(color = MaterialTheme.colorScheme.primaryContainer)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = shape
                ),
            contentAlignment = Alignment.Center,
            content = content
        )
    }

    @Composable
    private fun DisplayFieldContent(
        modifier: Modifier,
        passwordProperty: PasswordProperty
    ) {
        ConstraintLayout(
            modifier = modifier
                .setSizeLimitation()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            content = {
                val (iconRef, titleRef, contentRef) = createRefs()
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
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    contentDescription = null
                )
                Text(
                    modifier = Modifier.constrainAs(
                        ref = titleRef,
                        constrainBlock = {
                            this.top.linkTo(parent.top)
                            this.bottom.linkTo(contentRef.top)
                            this.start.linkTo(anchor = iconRef.end, margin = 16.dp)
                            this.end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        }
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
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
                            this.start.linkTo(anchor = iconRef.end, margin = 16.dp)
                            this.end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        }
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontFamily = PassMarkFonts.font,
                    fontSize = PassMarkFonts.Title.medium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = passwordProperty.field,
                )
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
                email = null,
                userName = null,
                password = "SomePassword",
                website = null,
                notes = null,
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
    val field: String
)