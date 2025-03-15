package easter.egg.passmark.ui.sections.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import easter.egg.passmark.R
import easter.egg.passmark.data.shared.PassMarkDimensions
import easter.egg.passmark.data.shared.PassMarkFonts
import easter.egg.passmark.utils.annotation.MobilePreview

object HomeScreen {
    @Composable
    fun Screen(
        modifier: Modifier
    ) {
        val appBarModifier = Modifier
            .fillMaxWidth()
            .heightIn(min = PassMarkDimensions.minTouchSize)
        val searchText = remember { mutableStateOf("Sample") }
        Scaffold(
            modifier = modifier,
            topBar = {
                HomeTopBar(
                    modifier = appBarModifier,
                    searchText = searchText.value,
                    onSearch = { searchText.value = it }
                )
            },
            content = {
                HomeContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = it)
                )
            },
            bottomBar = { HomeBottomBar(modifier = appBarModifier) }
        )
    }

    @Composable
    private fun HomeTopBar(
        modifier: Modifier,
        searchText: String,
        onSearch: (String) -> Unit
    ) {
        val componentHeight = PassMarkDimensions.minTouchSize
        Row(
            modifier = modifier.padding(
                top = 8.dp,
                bottom = 8.dp,
                start = 16.dp,
                end = 4.dp
            ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            content = {
                Box(
                    modifier = Modifier
                        .size(size = componentHeight)
                        .clip(shape = CircleShape)
                        .background(color = MaterialTheme.colorScheme.primaryContainer)
                        .clickable { TODO() },
                    contentAlignment = Alignment.Center,
                    content = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_monochrome),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
                        )
                    }
                )
                Spacer(modifier = Modifier.width(width = 8.dp))
                Box(
                    modifier = Modifier
                        .height(height = componentHeight)
                        .weight(1f)
                        .clip(shape = RoundedCornerShape(size = 16.dp))
                        .background(color = MaterialTheme.colorScheme.surfaceContainerHigh),
                    content = {
                        BasicTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(componentHeight),
                            value = searchText,
                            onValueChange = onSearch,
                            singleLine = true,
                            maxLines = 1,
                            textStyle = TextStyle(
                                fontSize = PassMarkFonts.Body.medium,
                                fontFamily = PassMarkFonts.font,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            decorationBox = { text: @Composable () -> Unit ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 24.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start,
                                    content = {
                                        text()
                                    }
                                )
                            }
                        )
                    }
                )
                IconButton(
                    modifier = Modifier.size(
                        width = PassMarkDimensions.minTouchSize,
                        height = componentHeight
                    ),
                    onClick = { TODO() },
                    content = {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null
                        )
                    }
                )
            }
        )
    }

    @Composable
    private fun HomeContent(modifier: Modifier) {
        // TODO: pending

        Column(
            modifier = modifier,
            content = {
                // TODO: pending
            }
        )
    }

    @Composable
    private fun HomeBottomBar(modifier: Modifier) {
        Row(
            modifier = modifier,
            content = {

                // TODO: pending
            }
        )
    }
}

@Composable
@MobilePreview
//@MobileHorizontalPreview
fun HomeScreenPreview() {
    HomeScreen.Screen(modifier = Modifier.fillMaxSize())
}