package easter.egg.passmark.ui.main.home.screens

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import easter.egg.passmark.R
import easter.egg.passmark.data.models.Vault
import easter.egg.passmark.data.models.Vault.Companion.getIcon
import easter.egg.passmark.ui.main.MainViewModel
import easter.egg.passmark.ui.main.home.HomeViewModel
import easter.egg.passmark.ui.main.home.VaultDialogActionOptions
import easter.egg.passmark.ui.main.home.VaultDialogResult
import easter.egg.passmark.ui.shared_components.CustomLoader
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.annotation.MobileHorizontalPreview
import easter.egg.passmark.utils.annotation.MobilePreview
import easter.egg.passmark.utils.testing.TestTags
import easter.egg.passmark.utils.testing.TestTags.applyTag
import easter.egg.passmark.utils.values.PassMarkDimensions
import easter.egg.passmark.utils.values.PassMarkFonts
import easter.egg.passmark.utils.values.setSizeLimitation

object HomeDrawer {
    @Composable
    fun DrawerContent(
        modifier: Modifier,
        viewModel: HomeViewModel,
        mainViewModel: MainViewModel,
        selectVault: (vaultId: Int?) -> Unit,
        toSettingsScreen: () -> Unit
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
                                .applyTag(testTag = TestTags.Home.Drawer.TOP_TITLE.name)
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
                                .fillMaxWidth(),
                            homeViewModel = viewModel,
                            mainViewModel = mainViewModel,
                            selectVault = selectVault
                        )
                        DrawerTitle(text = "Options")
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(height = 8.dp))
                        @Composable
                        fun DrawerButton(
                            modifier: Modifier = Modifier,
                            text: String,
                            icon: ImageVector,
                            onclick: () -> Unit
                        ) {
                            Row(
                                modifier = modifier
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
                            modifier = Modifier.applyTag(testTag = TestTags.Home.Drawer.SETTINGS.name),
                            text = "Settings",
                            icon = Icons.Default.Settings,
                            onclick = toSettingsScreen
                        )
                        val activity = LocalActivity.current
                        val uriHandler = LocalUriHandler.current
                        DrawerButton(
                            text = "Documentation",
                            icon = Icons.Default.Info,
                            onclick = {
                                uriHandler.openUri(uri = "https://github.com/Vaishnav-Kanhirathingal/PassMark")
                            }
                        )
                        DrawerButton(
                            text = "Exit",
                            icon = Icons.AutoMirrored.Filled.ExitToApp,
                            onclick = { activity?.finishAffinity() }
                        )
                    }
                )
            }
        )
    }

    @Composable
    private fun VaultList(
        modifier: Modifier,
        homeViewModel: HomeViewModel,
        mainViewModel: MainViewModel,
        selectVault: (vaultId: Int?) -> Unit
    ) {
        Column(
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
                Spacer(modifier = spacerModifier)
                val cornerSize = 12.dp
                val vaultIdSelected = homeViewModel.vaultIdSelected.collectAsState().value
                val result =
                    (mainViewModel.screenState.collectAsState().value as? ScreenState.Loaded)
                        ?.result
                val vaultList = (result?.vaultListState?.collectAsState()?.value ?: listOf())
                    .toMutableList<Vault?>()
                    .apply { this.add(index = 0, element = null) }
                val vaultSelectableModifier = Modifier.fillMaxWidth()
                vaultList.forEach { vault ->
                    var size = 0
                    val passwordList = result?.passwordListState?.collectAsState()?.value
                    if (vault?.id == null) {
                        size = passwordList?.size ?: 0
                    } else {
                        passwordList?.forEach {
                            if (it.vaultId == vault.id) size++
                        }
                    }
                    VaultSelectable(
                        modifier = vaultSelectableModifier,
                        vaultName = vault?.name ?: Vault.VAULT_NAME_FOR_ALL_ITEMS,
                        vaultIcon = vault.getIcon(),
                        passwordsInVault = size,
                        isSelected = vaultIdSelected == vault?.id,
                        cornerSize = cornerSize,
                        onClick = { selectVault(vault?.id) },
                        onLongPressed = {
                            vault?.let { v ->
                                homeViewModel.vaultDialogState.showDialog(vault = v)
                            }
                        }
                    )
                }
                if (vaultList.size < 5) {
                    Box(
                        modifier = Modifier
                            .applyTag(testTag = TestTags.Home.Drawer.CREATE_NEW_VAULT_BUTTON.name)
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
                            .clickable(
                                onClick = {
                                    homeViewModel.vaultDialogState.showDialog(vault = null)
                                }
                            )
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
                }
                if (homeViewModel.vaultDialogState.isVisible.collectAsState().value) {
                    VaultDialog(
                        modifier = Modifier.fillMaxWidth(),
                        homeViewModel = homeViewModel,
                        handleResult = {
                            val homeListResult =
                                (mainViewModel.screenState.value as? ScreenState.Loaded)
                                    ?.result
                            try {
                                when (it.action) {
                                    VaultDialogActionOptions.UPDATE -> homeListResult?.upsertNewVault(
                                        vault = it.vault
                                    )

                                    VaultDialogActionOptions.DELETE -> homeListResult?.deleteVaultAndAssociates(
                                        vaultId = it.vault.id!!
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                mainViewModel.refreshHomeList(silentReload = true)
                            }
                        }
                    )
                }
                Spacer(modifier = spacerModifier)
            }
        )
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun VaultSelectable(
        modifier: Modifier,
        vaultName: String,
        vaultIcon: ImageVector,
        passwordsInVault: Int,
        isSelected: Boolean,
        cornerSize: Dp,
        onClick: () -> Unit,
        onLongPressed: () -> Unit
    ) {
        val onContainerColor =
            if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
            else MaterialTheme.colorScheme.onSurface
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
                .combinedClickable(
                    onLongClick = onLongPressed,
                    onClick = onClick
                )
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
                    imageVector = vaultIcon,
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
                    text = vaultName
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
                    text = "$passwordsInVault passwords"
                )
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun VaultDialog(
        modifier: Modifier,
        homeViewModel: HomeViewModel,
        handleResult: (VaultDialogResult) -> Unit,
    ) {
        val screenState = homeViewModel.vaultDialogState.apiCallState.collectAsState().value
        val context = LocalContext.current
        LaunchedEffect(
            key1 = screenState,
            block = {
                when (screenState) {
                    is ScreenState.PreCall, is ScreenState.Loading -> {}
                    is ScreenState.Loaded -> {
                        handleResult(screenState.result)
                        homeViewModel.vaultDialogState.resetAndDismiss()
                    }

                    is ScreenState.ApiError -> {
                        screenState.manageToastActions(context = context)
                    }
                }
            }
        )
        BasicAlertDialog(
            modifier = modifier
                .clip(shape = RoundedCornerShape(size = 16.dp))
                .background(color = MaterialTheme.colorScheme.surfaceContainer),
            onDismissRequest = { homeViewModel.vaultDialogState.resetAndDismiss() },
            properties = (!screenState.isLoading).let { dismissAllowed ->
                DialogProperties(
                    dismissOnClickOutside = dismissAllowed,
                    dismissOnBackPress = dismissAllowed,
                )
            },
            content = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(
                        space = 4.dp,
                        alignment = Alignment.CenterVertically
                    ),
                    content = {
                        val iconSelected =
                            homeViewModel.vaultDialogState.iconChoice.collectAsState().value
                        val isSavedAlready =
                            homeViewModel.vaultDialogState.isAlreadyAVault.collectAsState(initial = false).value

                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = 16.dp,
                                    start = 16.dp,
                                    end = 16.dp,
                                    bottom = 8.dp
                                ),
                            text = if (isSavedAlready) "Edit your vault" else "Create a new vault",
                            fontFamily = PassMarkFonts.font,
                            fontSize = PassMarkFonts.Title.medium,
                            fontWeight = FontWeight.SemiBold
                        )
                        val dialogText = homeViewModel.vaultDialogState.text.collectAsState().value
                        OutlinedTextField(
                            modifier = Modifier
                                .applyTag(testTag = TestTags.Home.Drawer.VaultDialog.TEXT_FIELD.name)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            label = { Text(text = "Name") },
                            placeholder = { Text(text = "Vault ABC") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Vault.iconList[iconSelected],
                                    contentDescription = null
                                )
                            },
                            onValueChange = homeViewModel.vaultDialogState::updateText,
                            value = dialogText,
                            enabled = !screenState.isLoading
                        )
                        LazyVerticalGrid(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalArrangement = Arrangement.SpaceEvenly,
                            columns = GridCells.Adaptive(minSize = PassMarkDimensions.minTouchSize),
                            content = {
                                items(
                                    count = Vault.iconList.size,
                                    itemContent = {
                                        val isSelected = (it == iconSelected)
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            content = {
                                                Box(
                                                    modifier = Modifier
                                                        .applyTag(
                                                            testTag = TestTags.Home.Drawer
                                                                .VaultDialog.getIconTag(index = it)
                                                        )
                                                        .size(size = PassMarkDimensions.minTouchSize)
                                                        .clip(shape = CircleShape)
                                                        .background(
                                                            color =
                                                                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                                                else Color.Transparent
                                                        )
                                                        .clickable(
                                                            enabled = !screenState.isLoading,
                                                            onClick = {
                                                                homeViewModel.vaultDialogState.updateIconChoice(
                                                                    choice = it
                                                                )
                                                            }
                                                        ),
                                                    contentAlignment = Alignment.Center,
                                                    content = {
                                                        Icon(
                                                            imageVector = Vault.iconList[it],
                                                            contentDescription = null,
                                                            tint =
                                                                if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                                                else MaterialTheme.colorScheme.onSurface
                                                        )
                                                    }
                                                )
                                            }
                                        )
                                    }
                                )
                            }
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    bottom = 8.dp
                                ),
                            horizontalArrangement = Arrangement.spacedBy(
                                space = 4.dp,
                                alignment = Alignment.CenterHorizontally
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            content = {
                                val loaderSelected =
                                    homeViewModel.dialogButtonPressed.collectAsState().value

                                if (isSavedAlready) {
                                    Box(
                                        modifier = Modifier
                                            .applyTag(testTag = TestTags.Home.Drawer.VaultDialog.DELETE_BUTTON.name)
                                            .size(size = PassMarkDimensions.minTouchSize)
                                            .clip(shape = CircleShape)
                                            .background(color = MaterialTheme.colorScheme.errorContainer)
                                            .clickable(
                                                enabled = !screenState.isLoading,
                                                onClick = {
                                                    homeViewModel.performVaultAction(
                                                        action = VaultDialogActionOptions.DELETE
                                                    )
                                                }
                                            ),
                                        contentAlignment = Alignment.Center,
                                        content = {
                                            if (loaderSelected == VaultDialogActionOptions.DELETE && screenState.isLoading) {
                                                CustomLoader.ButtonLoader(
                                                    modifier = Modifier.size(size = 24.dp),
                                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                                )
                                            } else {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                                )
                                            }
                                        }
                                    )
                                }
                                Spacer(modifier = Modifier.weight(weight = 1f))
                                @Composable
                                fun CustomTextButton(
                                    modifier: Modifier,
                                    text: String,
                                    enabled: Boolean,
                                    onClick: () -> Unit,
                                    isLoading: Boolean
                                ) {
                                    Box(
                                        modifier = modifier
                                            .clip(shape = RoundedCornerShape(size = PassMarkDimensions.minTouchSize))
                                            .clickable(
                                                enabled = enabled,
                                                onClick = onClick
                                            ),
                                        contentAlignment = Alignment.Center,
                                        content = {
                                            val color = ButtonDefaults.textButtonColors().let {
                                                if (enabled) it.contentColor else it.disabledContentColor
                                            }
                                            Text(
                                                modifier = Modifier
                                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                                    .alpha(alpha = if (isLoading) 0f else 1f),
                                                text = text,
                                                fontFamily = PassMarkFonts.font,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = PassMarkFonts.Title.medium,
                                                color = color
                                            )
                                            if (isLoading) {
                                                CustomLoader.ButtonLoader(
                                                    modifier = Modifier.size(size = 24.dp),
                                                    color = color
                                                )
                                            }
                                        }
                                    )
                                }


                                CustomTextButton(
                                    modifier = Modifier.setSizeLimitation(),
                                    text = "Cancel",
                                    enabled = !screenState.isLoading,
                                    onClick = homeViewModel.vaultDialogState::resetAndDismiss,
                                    isLoading = false
                                )
                                CustomTextButton(
                                    modifier = Modifier
                                        .applyTag(testTag = TestTags.Home.Drawer.VaultDialog.CONFIRM_BUTTON.name)
                                        .setSizeLimitation(),
                                    text = if (isSavedAlready) "Update" else "Create",
                                    enabled = (!screenState.isLoading && (dialogText.isNotBlank())),
                                    onClick = { homeViewModel.performVaultAction(action = VaultDialogActionOptions.UPDATE) },
                                    isLoading = screenState.isLoading && (loaderSelected == VaultDialogActionOptions.UPDATE)
                                )
                            }
                        )
                    }
                )
            }
        )
    }
}

@Composable
@MobilePreview
@MobileHorizontalPreview
private fun HomeScreenDrawerPreview() {
    HomeDrawer.DrawerContent(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.7f),
        viewModel = HomeViewModel.getTestViewModel(),
        mainViewModel = MainViewModel.getTestViewModel(),
        selectVault = {},
        toSettingsScreen = {}
    )
}

@Composable
@Preview(widthDp = 360, heightDp = 360, showBackground = true)
fun VaultDialogPreview() {
    HomeDrawer.VaultDialog(
        modifier = Modifier.fillMaxWidth(),
        homeViewModel = HomeViewModel.getTestViewModel(),
        handleResult = {}
    )
}