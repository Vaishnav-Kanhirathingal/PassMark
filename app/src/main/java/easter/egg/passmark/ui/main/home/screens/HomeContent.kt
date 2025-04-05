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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import easter.egg.passmark.data.models.content.Password
import easter.egg.passmark.data.models.content.PasswordData
import easter.egg.passmark.utils.annotation.MobileHorizontalPreview
import easter.egg.passmark.utils.annotation.MobilePreview
import easter.egg.passmark.utils.values.PassMarkDimensions
import easter.egg.passmark.utils.values.PassMarkFonts
import easter.egg.passmark.utils.values.setSizeLimitation

object HomeContent {
    @Composable
    fun HomeContent(
        modifier: Modifier,
        passwordList: List<Password>
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
                            password = it
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
    ) {
        val iconSize: Dp = PassMarkDimensions.minTouchSize
        val innerIconRadius = 16.dp
        ConstraintLayout(
            modifier = modifier
                .clickable(onClick = { TODO() })
                .padding(vertical = 8.dp),
            content = {
                val (startIcon, title, subtitle, optionButton) = createRefs()
                Box(
                    modifier = Modifier
                        .size(size = iconSize)
                        .clip(shape = RoundedCornerShape(size = innerIconRadius))
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
                        Text(
                            textAlign = TextAlign.Center,
                            fontFamily = PassMarkFonts.font,
                            fontSize = PassMarkFonts.Title.medium,
                            fontWeight = FontWeight.Bold,
                            text = password.data.getShortName()
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
                                this.top.linkTo(parent.top)
                                this.bottom.linkTo(subtitle.top)
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
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(
                            ref = subtitle,
                            constrainBlock = {
                                this.top.linkTo(title.bottom)
                                this.bottom.linkTo(parent.bottom)
                                this.start.linkTo(
                                    anchor = startIcon.end,
                                    margin = horizontalPadding
                                )
                                this.end.linkTo(
                                    anchor = optionButton.start,
                                    margin = horizontalPadding
                                )
                                width = Dimension.fillToConstraints
                            }
                        ),
                    fontFamily = PassMarkFonts.font,
                    fontSize = PassMarkFonts.Label.small,
                    lineHeight = PassMarkFonts.Label.small,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface,
                    text = password.data.getSubTitle() ?: "No data"
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
                    onClick = { TODO() },
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
        lastModified = now
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
            testBasePassword.copy(id = 3, data = testBasePasswordData.copy(title = "Epic")),
        )
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
        )
    )
}