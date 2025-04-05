package easter.egg.passmark.ui.main.home.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import easter.egg.passmark.data.models.content.Password
import easter.egg.passmark.data.models.content.PasswordData
import easter.egg.passmark.utils.annotation.MobileHorizontalPreview
import easter.egg.passmark.utils.annotation.MobilePreview
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
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
        LazyColumn(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 4.dp,
                alignment = Alignment.Top
            ),
            content = {
                items(
                    items = passwordList,
                    key = { it.id!! },
                    itemContent = {
//                        PasswordListItem(
//                            modifier = listItemModifier,
//                            password = it
//                        )
                    }
                )
            }
        )
    }

    @Composable
    fun PasswordListItem(
        modifier: Modifier,
        password: Password
    ) {
        ConstraintLayout(
            modifier = modifier
                .clip(shape = RoundedCornerShape(size = 12.dp))
                .background(color = MaterialTheme.colorScheme.primaryContainer)
                .clickable(onClick = { TODO() }),
            content = {
                val (title, optionButton, websiteLink) = createRefs()
                IconButton(
                    modifier = Modifier
                        .size(size = 60.dp)
                        .constrainAs(
                            ref = optionButton,
                            constrainBlock = {
                                this.top.linkTo(parent.top)
                                this.bottom.linkTo(parent.bottom)
                                this.end.linkTo(parent.end)
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
                Text(
                    modifier = Modifier.constrainAs(
                        ref = title,
                        constrainBlock = {
                            this.top.linkTo(parent.top, margin = 4.dp)
                            this.bottom.linkTo(websiteLink.top, margin = 0.dp)
                            this.start.linkTo(parent.start, margin = 16.dp)
                            this.end.linkTo(optionButton.start, margin = 8.dp)
                            width = Dimension.fillToConstraints
                        }
                    ),
                    maxLines = 2,
                    text = password.data.title,
                    fontFamily = PassMarkFonts.font,
                    fontSize = PassMarkFonts.Title.medium,
                    lineHeight = PassMarkFonts.Title.medium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    modifier = Modifier.constrainAs(
                        ref = websiteLink,
                        constrainBlock = {
                            width = Dimension.fillToConstraints
                            this.start.linkTo(title.start)
                            this.end.linkTo(title.end)
                            this.top.linkTo(title.bottom)
                            this.bottom.linkTo(parent.bottom, margin = 4.dp)
                        }
                    ),
                    fontFamily = PassMarkFonts.font,
                    fontSize = PassMarkFonts.Label.small,
                    lineHeight = PassMarkFonts.Label.small,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    text = password.data.website
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
                website = "www.google.com"
            )
        )
    )
}