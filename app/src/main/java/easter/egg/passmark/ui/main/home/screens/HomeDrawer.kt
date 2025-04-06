package easter.egg.passmark.ui.main.home.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.vector.ImageVector
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
import easter.egg.passmark.data.supabase.api.VaultApi
import easter.egg.passmark.di.supabase.SupabaseModule
import easter.egg.passmark.ui.main.MainViewModel
import easter.egg.passmark.ui.main.home.HomeViewModel
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.annotation.MobileHorizontalPreview
import easter.egg.passmark.utils.annotation.MobilePreview
import easter.egg.passmark.utils.values.PassMarkDimensions
import easter.egg.passmark.utils.values.PassMarkFonts
import easter.egg.passmark.utils.values.setSizeLimitation

object HomeDrawer {
    @Composable
    fun DrawerContent(
        modifier: Modifier,
        viewModel: HomeViewModel,
        mainViewModel: MainViewModel
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
                                .fillMaxWidth(),
                            homeViewModel = viewModel,
                            mainViewModel = mainViewModel
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
    private fun VaultList(
        modifier: Modifier,
        homeViewModel: HomeViewModel,
        mainViewModel: MainViewModel
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

                val vaultSelectableModifier = Modifier.fillMaxWidth()
                val cornerSize = 12.dp

                val vaultIdSelected = homeViewModel.vaultIdSelected.collectAsState().value
                VaultSelectable(
                    modifier = vaultSelectableModifier,
                    vaultName = "All items",
                    vaultIcon = Icons.Default.Password,
                    passwordsInVault = 10,
                    isSelected = vaultIdSelected == null,
                    cornerSize = cornerSize,
                    onClick = { homeViewModel.updateVaultIdSelected(id = null) }
                )
                val result =
                    (mainViewModel.screenState.collectAsState().value as? ScreenState.Loaded)
                        ?.result
                val vaultList = result?.vaultListState?.collectAsState()?.value ?: listOf()

                vaultList.forEach { vault ->
                    var size = 0
                    result?.passwordListState?.collectAsState()?.value?.forEach { if (it.vaultId == vault.id) size++ }
                    VaultSelectable(
                        modifier = vaultSelectableModifier,
                        vaultName = vault.name,
                        vaultIcon = vault.getIcon(),
                        passwordsInVault = size,
                        isSelected = vaultIdSelected == vault.id,
                        cornerSize = cornerSize,
                        onClick = { homeViewModel.updateVaultIdSelected(id = vault.id) }
                    )
                }
                if (vaultList.size < 5) {
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
                            .clickable(onClick = { homeViewModel.vaultDialogState.showDialog() })
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
                        mainViewModel = mainViewModel
                    )
                }
                Spacer(modifier = spacerModifier)
            }
        )
    }

    @Composable
    private fun VaultSelectable(
        modifier: Modifier,
        vaultName: String,
        vaultIcon: ImageVector,
        passwordsInVault: Int,
        isSelected: Boolean,
        cornerSize: Dp,
        onClick: () -> Unit
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
                .clickable(onClick = onClick)
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
        mainViewModel: MainViewModel
    ) {
        val screenState = homeViewModel.vaultDialogState.apiCallState.collectAsState().value
        LaunchedEffect(
            key1 = screenState,
            block = {
                if (screenState is ScreenState.Loaded) {
                    (mainViewModel.screenState.value as? ScreenState.Loaded)?.result?.addNewVault(
                        vault = screenState.result
                    )
                    homeViewModel.vaultDialogState.resetAndDismiss()
                }
            }
        )
        BasicAlertDialog(
            modifier = modifier
                .clip(shape = RoundedCornerShape(size = 16.dp))
                .background(color = MaterialTheme.colorScheme.surface),
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
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = 16.dp,
                                    start = 16.dp,
                                    end = 16.dp,
                                    bottom = 8.dp
                                ),
                            text = "Name your vault",
                            fontFamily = PassMarkFonts.font,
                            fontSize = PassMarkFonts.Title.medium,
                            fontWeight = FontWeight.SemiBold
                        )
                        val dialogText = homeViewModel.vaultDialogState.text.collectAsState().value
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            label = { Text(text = "Name") },
                            placeholder = { Text(text = "Vault ABC") },
                            onValueChange = homeViewModel.vaultDialogState::updateText,
                            value = dialogText,
                            enabled = !screenState.isLoading
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
                                alignment = Alignment.End
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            content = {
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
                                            Text(
                                                modifier = Modifier
                                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                                    .alpha(alpha = if (isLoading) 0f else 1f),
                                                text = text,
                                                fontFamily = PassMarkFonts.font,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = PassMarkFonts.Title.medium,
                                                color = ButtonDefaults.textButtonColors().let {
                                                    if (enabled) it.contentColor else it.disabledContentColor
                                                }
                                            )
                                            if (isLoading) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(size = 24.dp),
                                                    strokeWidth = 2.dp
                                                )
                                            }
                                        }
                                    )
                                }


                                CustomTextButton(
                                    modifier = Modifier.setSizeLimitation(),
                                    text = "Cancel",
                                    enabled = !screenState.isLoading,
                                    onClick = { homeViewModel.vaultDialogState.resetAndDismiss() },
                                    isLoading = false
                                )
                                CustomTextButton(
                                    modifier = Modifier.setSizeLimitation(),
                                    text = "Create",
                                    enabled = (!screenState.isLoading && (dialogText.isNotBlank())),
                                    onClick = { homeViewModel.createNewVault() },
                                    isLoading = screenState.isLoading
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
        viewModel = HomeViewModel(vaultApi = VaultApi(supabaseClient = SupabaseModule.mockClient)),
        mainViewModel = MainViewModel.getTestViewModel()
    )
}

@Composable
@Preview(widthDp = 360, heightDp = 360, showBackground = true)
fun VaultDialogPreview() {
    HomeDrawer.VaultDialog(
        modifier = Modifier.fillMaxWidth(),
        homeViewModel = HomeViewModel(vaultApi = VaultApi(supabaseClient = SupabaseModule.mockClient)),
        mainViewModel = MainViewModel.getTestViewModel()
    )
}