package easter.egg.passmark.ui.sections.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalPostOffice
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import easter.egg.passmark.R
import easter.egg.passmark.data.models.Vault
import easter.egg.passmark.utils.annotation.MobileHorizontalPreview
import easter.egg.passmark.utils.annotation.MobilePreview
import easter.egg.passmark.utils.values.PassMarkDimensions
import easter.egg.passmark.utils.values.PassMarkFonts
import easter.egg.passmark.utils.values.setSizeLimitation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object HomeScreen {
    @Composable
    fun Screen(
        modifier: Modifier,
        toAddNewPasswordScreen: () -> Unit
    ) {
        val coroutineScope = rememberCoroutineScope()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        ModalNavigationDrawer(
            modifier = modifier,
            drawerState = drawerState,
            drawerContent = {
                // TODO: fix the width limitation problem
                DrawerContent(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = 0.7f)
                )
            },
            content = {
                val searchText: MutableState<String?> = remember { mutableStateOf(null) }
                Scaffold(
                    modifier = modifier,
                    topBar = {
                        HomeTopBar(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = PassMarkDimensions.minTouchSize),
                            searchText = searchText.value,
                            onSearch = { searchText.value = it },
                            openNavigationDrawer = {
                                coroutineScope.launch {
                                    drawerState.open()
                                }
                            }
                        )
                    },
                    content = {
                        HomeContent(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues = it)
                        )
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = toAddNewPasswordScreen,
                            content = {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null
                                )
                            }
                        )
                    }
                )
            }
        )
    }

    @Composable
    fun DrawerContent(
        modifier: Modifier
    ) {
        val scrollState = rememberScrollState()
        Box(
            modifier = modifier,
            contentAlignment = Alignment.CenterStart,
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .widthIn(max = 300.dp)
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.surface)
                        .verticalScroll(state = scrollState),
                    verticalArrangement = Arrangement.Top,
                    content = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = 24.dp,
                                    bottom = 16.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(
                                space = 8.dp,
                                alignment = Alignment.CenterHorizontally
                            ),
                            content = {
                                Icon(
                                    modifier = Modifier
                                        .size(size = PassMarkDimensions.minTouchSize),
                                    painter = painterResource(id = R.drawable.ic_launcher_uncropped),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    fontSize = PassMarkFonts.Headline.medium,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = PassMarkFonts.font,
                                    text = stringResource(R.string.app_name)
                                )
                            }
                        )
                        @Composable
                        fun DrawerTitle(text: String) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                fontFamily = PassMarkFonts.font,
                                fontSize = PassMarkFonts.Title.medium,
                                fontWeight = FontWeight.SemiBold,
                                text = text
                            )
                        }
                        DrawerTitle(text = "Vaults")
                        HorizontalDivider()
                        VaultList(
                            modifier = Modifier
                                .heightIn(min = 300.dp)
                                .fillMaxWidth()
                        )
                        DrawerTitle(text = "Options")
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(height = 8.dp))
                        @Composable
                        fun DrawerButton(
                            text: String,
                            icon: ImageVector,
                            onclick: () -> Unit
                        ) {
                            Row(
                                modifier = Modifier
                                    .setSizeLimitation()
                                    .fillMaxWidth()
                                    .clickable(onClick = onclick),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(
                                    space = 12.dp,
                                    alignment = Alignment.CenterHorizontally
                                ),
                                content = {
                                    val padding = 16.dp
                                    Spacer(modifier = Modifier.width(width = padding))
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null
                                    )
                                    Text(
                                        modifier = Modifier.weight(1f),
                                        fontSize = PassMarkFonts.Body.medium,
                                        fontFamily = PassMarkFonts.font,
                                        fontWeight = FontWeight.SemiBold,
                                        text = text
                                    )
                                    Spacer(modifier = Modifier.width(width = padding))
                                }
                            )
                        }
                        DrawerButton(
                            text = "Settings",
                            icon = Icons.Default.Settings,
                            onclick = { TODO() }
                        )
                        DrawerButton(
                            text = "Documentation",
                            icon = Icons.Default.Info,
                            onclick = { TODO() }
                        )
                        DrawerButton(
                            text = "Exit",
                            icon = Icons.AutoMirrored.Filled.ExitToApp,
                            onclick = { TODO() }
                        )
                    }
                )
            }
        )
    }

    @Composable
    private fun HomeTopBar(
        modifier: Modifier,
        searchText: String?,
        onSearch: (String?) -> Unit,
        openNavigationDrawer: () -> Unit
    ) {
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
                val componentHeight = PassMarkDimensions.minTouchSize
                val focusRequester = remember { FocusRequester() }
                val keyboardController = LocalSoftwareKeyboardController.current
                val coroutineScope = rememberCoroutineScope()
                if (searchText == null) {
                    Box(
                        modifier = Modifier
                            .size(size = componentHeight)
                            .clip(shape = CircleShape)
                            .background(color = MaterialTheme.colorScheme.primaryContainer)
                            .clickable(onClick = openNavigationDrawer),
                        contentAlignment = Alignment.Center,
                        content = {
                            Image(
                                painter = painterResource(id = R.drawable.ic_monochrome),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
                            )
                        }
                    )
                    Row(
                        modifier = Modifier
                            .height(height = componentHeight)
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                            .clip(shape = RoundedCornerShape(size = 16.dp))
                            .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
                            .padding(horizontal = 12.dp)
                            .clickable {
                                onSearch("")
                                coroutineScope.launch {
                                    delay(100)
                                    try {
                                        focusRequester.requestFocus()
                                        keyboardController?.show()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        content = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null
                            )
                            Text(
                                modifier = Modifier.weight(1f),
                                text = "Search passwords"
                            )
                        }
                    )
                } else {
                    IconButton(
                        modifier = Modifier.size(
                            width = componentHeight,
                            height = componentHeight
                        ),
                        onClick = { onSearch(null) },
                        content = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    )
                    BasicTextField(
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = PassMarkDimensions.minTouchSize)
                            .focusRequester(focusRequester = focusRequester),
                        value = searchText,
                        onValueChange = onSearch,
                        singleLine = true,
                        maxLines = 1,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant),
                        decorationBox = { text ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = PassMarkDimensions.minTouchSize)
                                    .padding(horizontal = 8.dp),
                                contentAlignment = Alignment.CenterStart,
                                content = {
                                    if (searchText.isEmpty()) {
                                        Text(
                                            text = "Search in all items...",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    text()
                                }
                            )
                        }
                    )
                }
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
    private fun VaultList(
        modifier: Modifier
    ) {
        val testList = listOf( // TODO: remove this
            Vault(id = 0, name = "All", iconChoice = 0),
            Vault(id = 1, name = "Work", iconChoice = 1),
            Vault(id = 2, name = "Devices", iconChoice = 2),
            Vault(id = 3, name = "Unimportant", iconChoice = 3)
        )

        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 4.dp,
                alignment = Alignment.Top
            ),
            content = {
                val spacerModifier = Modifier
                    .fillMaxWidth()
                    .height(height = 8.dp)
                Spacer(modifier = spacerModifier)

                val vaultSelectableModifier = Modifier.fillMaxWidth()
                val cornerSize = 12.dp
                testList.forEachIndexed { index, vault ->
                    VaultSelectable(
                        modifier = vaultSelectableModifier,
                        vault = vault,
                        itemsInList = 10, // TODO: get
                        isSelected = index == 0,
                        cornerSize = cornerSize
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(size = 60.dp)
                        .setSizeLimitation()
                        .clip(
                            shape = RoundedCornerShape(
                                topStart = 0.dp,
                                topEnd = cornerSize,
                                bottomStart = cornerSize,
                                bottomEnd = cornerSize
                            )
                        )
                        .clickable(onClick = { TODO() })
                        .background(color = MaterialTheme.colorScheme.primaryContainer)
                        .align(alignment = Alignment.End),
                    contentAlignment = Alignment.Center,
                    content = {
                        Icon(
                            modifier = Modifier.size(size = 28.dp),
                            imageVector = Icons.Default.Add,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            contentDescription = null
                        )
                    }
                )
                Spacer(modifier = spacerModifier)
            }
        )
    }

    @Composable
    fun VaultSelectable(
        modifier: Modifier,
        vault: Vault,
        itemsInList: Int,
        isSelected: Boolean,
        cornerSize: Dp
    ) {
        val onContainerColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
        else MaterialTheme.colorScheme.onSecondaryContainer
        ConstraintLayout(
            modifier = modifier
                .setSizeLimitation()
                .padding(horizontal = 8.dp)
                .clip(shape = RoundedCornerShape(size = cornerSize))
                .background(
                    color =
                        if (isSelected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceContainer,
                )
                .clickable(onClick = { TODO() })
                .padding(horizontal = 16.dp, vertical = 16.dp),
            content = {
                val (icon, title, subTitle) = createRefs()
                Icon(
                    modifier = Modifier
                        .size(size = 28.dp)
                        .constrainAs(
                            ref = icon,
                            constrainBlock = {
                                this.top.linkTo(parent.top)
                                this.start.linkTo(parent.start)
                                this.bottom.linkTo(parent.bottom)
                            }
                        ),
                    imageVector = when (vault.iconChoice) {
                        0 -> Icons.Default.Web
                        1 -> Icons.Default.Web
                        2 -> Icons.Default.Web
                        else -> Icons.Default.LocalPostOffice
                    },
                    tint = onContainerColor,
                    contentDescription = null
                )

                Text(
                    modifier = Modifier
                        .constrainAs(
                            ref = title,
                            constrainBlock = {
                                this.top.linkTo(icon.top)
                                this.bottom.linkTo(subTitle.top)
                                this.start.linkTo(
                                    anchor = icon.end,
                                    margin = 16.dp
                                )
                                this.end.linkTo(parent.end)
                                width = Dimension.fillToConstraints
                            }
                        ),
                    fontSize = PassMarkFonts.Title.large,
                    lineHeight = PassMarkFonts.Title.large,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = onContainerColor,
                    text = vault.name
                )
                Text(
                    modifier = Modifier
                        .constrainAs(
                            ref = subTitle,
                            constrainBlock = {
                                this.top.linkTo(title.bottom)
                                this.start.linkTo(
                                    anchor = icon.end,
                                    margin = 16.dp
                                )
                                this.bottom.linkTo(icon.bottom)
                                this.end.linkTo(parent.end)
                                width = Dimension.fillToConstraints
                            }
                        ),
                    fontSize = PassMarkFonts.Label.medium,
                    lineHeight = PassMarkFonts.Label.medium,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = onContainerColor,
                    text = "$itemsInList passwords"
                )
            }
        )
    }

    @Composable
    private fun HomeContent(modifier: Modifier) {
        Column(
            modifier = modifier,
            content = {
                // TODO: pending
            }
        )
    }
}

@Composable
@MobilePreview
@MobileHorizontalPreview
private fun HomeScreenPreview() {
    HomeScreen.Screen(
        modifier = Modifier.fillMaxSize(),
        toAddNewPasswordScreen = {}
    )
}

@Composable
@MobilePreview
@MobileHorizontalPreview
private fun HomeScreenDrawerPreview() {
    HomeScreen.DrawerContent(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.7f)
    )
}