package easter.egg.passmark.ui.main.home.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.Visibility
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import easter.egg.passmark.data.models.content.Password
import easter.egg.passmark.data.models.content.PasswordData
import easter.egg.passmark.utils.annotation.MobileHorizontalPreview
import easter.egg.passmark.utils.annotation.MobilePreview
import easter.egg.passmark.utils.values.PassMarkDimensions
import easter.egg.passmark.utils.values.PassMarkFonts
import easter.egg.passmark.utils.values.setSizeLimitation

object HomeContent {
    private val TAG = this::class.simpleName

    @Composable
    fun HomeContent(
        modifier: Modifier,
        passwordList: List<Password>,
        toViewPasswordScreen: (passwordId: Int) -> Unit
    ) {
        val listItemModifier = Modifier
            .setSizeLimitation()
            .fillMaxWidth()
        LazyColumn(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.Top
            ),
            content = {
                val spacerModifier = Modifier
                    .fillMaxWidth()
                    .height(height = 8.dp)
                item { Spacer(modifier = spacerModifier) }
                items(
                    items = passwordList,
                    key = { it.id!! },
                    itemContent = {
                        PasswordListItem(
                            modifier = listItemModifier,
                            password = it,
                            viewPassword = { toViewPasswordScreen(it.id!!) },
                            openOptions = { TODO() }
                        )
                    }
                )
                item { Spacer(modifier = spacerModifier) }
            }
        )
    }

    @Composable
    fun PasswordListItem(
        modifier: Modifier,
        password: Password,
        viewPassword: () -> Unit,
        openOptions: () -> Unit
    ) {
        val iconSize: Dp = PassMarkDimensions.minTouchSize
        ConstraintLayout(
            modifier = modifier
                .clickable(onClick = viewPassword)
                .padding(vertical = 8.dp),
            content = {
                val (startIcon, title, subtitle, optionButton) = createRefs()
                Box(
                    modifier = Modifier
                        .size(size = iconSize)
                        .clip(shape = RoundedCornerShape(size = 12.dp))
                        .background(color = MaterialTheme.colorScheme.surfaceContainer)
                        .constrainAs(
                            ref = startIcon,
                            constrainBlock = {
                                this.top.linkTo(parent.top)
                                this.bottom.linkTo(parent.bottom)
                                this.start.linkTo(anchor = parent.start, margin = 16.dp)
                            }
                        ),
                    contentAlignment = Alignment.Center,
                    content = {
                        val showText: MutableState<Boolean> = remember { mutableStateOf(true) }
                        val model = ImageRequest.Builder(LocalContext.current)
                            .data(password.data.getFavicon())
                            .crossfade(true)
                            .listener(onSuccess = { _, _ -> showText.value = false })
                            .build()
                        if (showText.value) {
                            Text(
                                textAlign = TextAlign.Center,
                                fontFamily = PassMarkFonts.font,
                                fontSize = PassMarkFonts.Title.medium,
                                fontWeight = FontWeight.Bold,
                                text = password.data.getShortName()
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
                val horizontalPadding = 12.dp
                createVerticalChain(
                    title, subtitle,
                    chainStyle = ChainStyle.Packed
                )
                Text(
                    modifier = Modifier
                        .constrainAs(
                            ref = title,
                            constrainBlock = {
                                this.start.linkTo(startIcon.end, margin = horizontalPadding)
                                this.end.linkTo(optionButton.start, margin = horizontalPadding)
                                width = Dimension.fillToConstraints
                            }
                        ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = password.data.title,
                    fontFamily = PassMarkFonts.font,
                    fontSize = PassMarkFonts.Title.large,
                    lineHeight = PassMarkFonts.Title.large,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                val titleText = password.data.getSubTitle()
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(
                            ref = subtitle,
                            constrainBlock = {
                                this.start.linkTo(
                                    anchor = startIcon.end,
                                    margin = horizontalPadding
                                )
                                this.end.linkTo(
                                    anchor = optionButton.start,
                                    margin = horizontalPadding
                                )
                                width = Dimension.fillToConstraints
                                visibility =
                                    if (titleText == null) Visibility.Gone else Visibility.Visible
                            }
                        ),
                    fontFamily = PassMarkFonts.font,
                    fontSize = PassMarkFonts.Label.small,
                    lineHeight = PassMarkFonts.Label.small,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface,
                    text = titleText ?: "No data"
                )

                IconButton(
                    modifier = Modifier
                        .size(size = iconSize)
                        .constrainAs(
                            ref = optionButton,
                            constrainBlock = {
                                this.top.linkTo(parent.top)
                                this.bottom.linkTo(parent.bottom)
                                this.end.linkTo(anchor = parent.end, margin = 4.dp)
                            }
                        ),
                    onClick = openOptions,
                    content = {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            contentDescription = null,
                        )
                    }
                )
            }
        )
    }
}

private val testBasePasswordData = PasswordData(
    title = "",
    email = "",
    userName = "",
    password = "",
    website = "",
    notes = "",
    useFingerPrint = true,
    saveToLocalOnly = false
)
private val testBasePassword = System.currentTimeMillis().let { now ->
    Password(
        id = 0,
        data = testBasePasswordData.copy(),
        created = now,
        lastUsed = now,
        lastModified = now,
        usedCount = 0
    )
}

@Composable
@MobilePreview
@MobileHorizontalPreview
fun HomeContentPreview() {
    HomeContent.HomeContent(
        modifier = Modifier.fillMaxSize(),
        passwordList = listOf(
            testBasePassword.copy(id = 0, data = testBasePasswordData.copy(title = "Google")),
            testBasePassword.copy(id = 1, data = testBasePasswordData.copy(title = "Facebook")),
            testBasePassword.copy(id = 2, data = testBasePasswordData.copy(title = "Ubisoft")),
            testBasePassword.copy(
                id = 3,
                data = testBasePasswordData.copy(title = "Epic", email = "abc@gmail.com")
            ),
        ),
        toViewPasswordScreen = {}
    )
}

@Composable
@Preview(
    widthDp = 360, heightDp = 120,
    showBackground = true
)
fun PasswordListItemPreview() {
    HomeContent.PasswordListItem(
        modifier = Modifier
            .fillMaxWidth()
            .setSizeLimitation()
            .wrapContentHeight(),
        password = testBasePassword.copy(
            data = testBasePasswordData.copy(
                title = "Google",
                email = "sample@gmail.com",
                userName = "GmailUserName",
                website = "www.google.com"
            )
        ),
        viewPassword = {},
        openOptions = {}
    )
}