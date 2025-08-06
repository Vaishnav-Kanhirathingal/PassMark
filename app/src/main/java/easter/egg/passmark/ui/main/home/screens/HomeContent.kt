package easter.egg.passmark.ui.main.home.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import coil3.compose.SubcomposeAsyncImage
import easter.egg.passmark.R
import easter.egg.passmark.data.models.password.PasswordData
import easter.egg.passmark.data.models.password.sensitive.SensitiveContent
import easter.egg.passmark.ui.main.MainViewModel
import easter.egg.passmark.ui.main.home.HomeViewModel
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.accessibility.Describable.Companion.hideFromAccessibility
import easter.egg.passmark.utils.accessibility.Describable.Companion.setDescription
import easter.egg.passmark.utils.accessibility.main.HomeDescribable
import easter.egg.passmark.utils.annotation.MobilePreview
import easter.egg.passmark.utils.annotation.PreviewRestricted
import easter.egg.passmark.utils.values.PassMarkDimensions
import easter.egg.passmark.utils.values.PassMarkFonts
import easter.egg.passmark.utils.values.setSizeLimitation
import kotlinx.coroutines.launch

object HomeContent {
    private val TAG = this::class.simpleName

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HomeContent(
        modifier: Modifier,
        mainViewModel: MainViewModel,
        homeViewModel: HomeViewModel,
        toViewPasswordScreen: (passwordData: PasswordData) -> Unit,
        toPasswordEditScreen: (passwordData: PasswordData?) -> Unit,
    ) {
        val vaultId = homeViewModel.vaultIdSelected.collectAsState().value
        val homeResult = (mainViewModel.screenState.collectAsState().value as? ScreenState.Loaded)
            ?.result
        val passwordList = homeResult
            ?.getFilteredPasswordList(
                vaultId = vaultId,
                searchString = homeViewModel.searchText.collectAsState().value,
                passwordSortingOptions = homeViewModel.getPasswordSortingOption().value,
                increasingOrder = homeViewModel.getIncreasingOrder().value
            )
            ?.collectAsState(initial = listOf())
            ?.value
        if (passwordList?.isEmpty() == true) {
            EmptyListUI(
                modifier = modifier,
                vaultSelectedName = homeResult.vaultListState.collectAsState().value
                    .find { v -> v.id == homeViewModel.vaultIdSelected.collectAsState().value }
                    ?.name,
                searchUsed = (homeViewModel.searchText.collectAsState().value != null)
            )
        } else {
            val sheetState = rememberModalBottomSheetState()
            val passwordSheetState = homeViewModel.passwordSheetState.collectAsState()

            LaunchedEffect(
                key1 = Unit,
                block = {
                    if (passwordSheetState.value != null) sheetState.show()
                    else sheetState.hide()
                }
            )

            val coroutineScope = rememberCoroutineScope()
            passwordSheetState.value?.let { password ->
                HomePasswordOptionBottomSheet.PasswordOptionBottomSheet(
                    passwordData = password,
                    sheetState = sheetState,
                    dismissSheet = {
                        coroutineScope
                            .launch { sheetState.hide() }
                            .invokeOnCompletion { homeViewModel.dismissPasswordOptionSheet() }
                    },
                    toPasswordEditScreen = {
                        coroutineScope
                            .launch { sheetState.hide() }
                            .invokeOnCompletion {
                                toPasswordEditScreen(password)
                                homeViewModel.dismissPasswordOptionSheet()
                            }
                    },
                    setPromptState = { homeViewModel.securityPromptState.value = it }
                )
            }
            val securityPromptState = homeViewModel.securityPromptState.collectAsState().value

            if (securityPromptState != null) {
                TODO("show master password prompt and use ${securityPromptState.action.name} for performing an action")
            }

            val listingModifier = Modifier
                .setSizeLimitation()
                .fillMaxWidth()
            LazyColumn(
                modifier = modifier.setDescription(describable = HomeDescribable.PASSWORD_LIST),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    space = 8.dp,
                    alignment = Alignment.Top
                ),
                content = {
                    item {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(height = 8.dp)
                        )
                    }
                    items(
                        items = passwordList ?: listOf(),
                        itemContent = {
                            PasswordListItem(
                                modifier = listingModifier,
                                passwordData = it,
                                viewPassword = { toViewPasswordScreen(it) },
                                openOptions = {
                                    homeViewModel.openPasswordOptionSheet(passwordData = it)
                                    coroutineScope.launch { sheetState.show() }
                                },
                            )
                        }
                    )
                    item {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(height = PassMarkDimensions.minTouchSize * 2)
                        )
                    }
                }
            )
        }
    }

    @Composable
    fun EmptyListUI(
        modifier: Modifier,
        vaultSelectedName: String?,
        searchUsed: Boolean
    ) {
        Column(
            modifier = modifier.padding(all = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 4.dp,
                alignment = Alignment.CenterVertically
            ),
            content = {
                val contentModifier = Modifier
                    .sizeIn(maxWidth = 360.dp, maxHeight = 360.dp)
                    .fillMaxWidth(fraction = 0.5f)
                Text(
                    modifier = contentModifier,
                    text =
                        if (searchUsed) "No password matches the given criterion"
                        else if (vaultSelectedName == null) "You do not have any saved passwords"
                        else "Vault '$vaultSelectedName' does not have any saved passwords",
                    textAlign = TextAlign.Center,
                    fontFamily = PassMarkFonts.font,
                    fontSize = PassMarkFonts.Body.medium,
                    lineHeight = PassMarkFonts.Body.medium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Box(
                    modifier = Modifier
                        .sizeIn(maxWidth = 360.dp, maxHeight = 360.dp)
                        .fillMaxSize(fraction = 0.5f),
                    contentAlignment = Alignment.Center,
                    content = {
                        Image(
                            painter = painterResource(id = R.drawable.note_list_0_base),
                            contentDescription = null,
                        )
                        Image(
                            painter = painterResource(id = R.drawable.note_list_1_leaves),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.surfaceContainer)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.note_list_2_phone_frame),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.outline)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.note_list_3_phone_screen_surface),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.surface)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.note_list_4_phone_screen_icons),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSurface)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.note_list_5_phone_screen_primary_elements),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.note_list_6_phone_screen_surface_container_elements),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.surfaceContainerHighest)
                        )
                    }
                )
                Text(
                    modifier = contentModifier,
                    textAlign = TextAlign.Center,
                    text = "To create a new Password, click on the '+' floating button",
                    fontFamily = PassMarkFonts.font,
                    fontSize = PassMarkFonts.Label.medium,
                    lineHeight = PassMarkFonts.Label.medium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )
    }

    @Composable
    fun PasswordListItem(
        modifier: Modifier,
        passwordData: PasswordData,
        viewPassword: () -> Unit,
        openOptions: () -> Unit,
    ) {
        val iconSize: Dp = PassMarkDimensions.minTouchSize
        ConstraintLayout(
            modifier = modifier
                .clickable(onClick = viewPassword)
                .padding(vertical = 8.dp)
                .setDescription(describable = HomeDescribable.getPasswordDescribable(name = passwordData.data.title)),
            content = {
                val (startIcon, title, subtitle, optionButton) = createRefs()
                Box(
                    modifier = Modifier
                        .size(size = iconSize)
                        .clip(shape = RoundedCornerShape(size = 12.dp))
                        .background(color = MaterialTheme.colorScheme.surfaceContainer)
                        .border(
                            width = 1.dp,
                            color = if (passwordData.localId == null) Color.Transparent else MaterialTheme.colorScheme.surfaceContainerHighest,
                            shape = RoundedCornerShape(size = 12.dp)
                        )
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
                        @Composable
                        fun PassTextIcon() {
                            Text(
                                textAlign = TextAlign.Center,
                                fontFamily = PassMarkFonts.font,
                                fontSize = PassMarkFonts.Title.medium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                text = passwordData.data.getShortName()
                            )
                        }
                        SubcomposeAsyncImage(
                            model = passwordData.data.getFavicon(),
                            contentDescription = null,
                            loading = { PassTextIcon() },
                            error = { PassTextIcon() },
                            success = {
                                Image(
                                    painter = it.painter,
                                    contentDescription = null,
                                    modifier = Modifier.size(size = 24.dp),
                                    contentScale = ContentScale.Fit,
                                )
                            }
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
                        )
                        .hideFromAccessibility(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = passwordData.data.title,
                    fontFamily = PassMarkFonts.font,
                    fontSize = PassMarkFonts.Title.large,
                    lineHeight = PassMarkFonts.Title.large,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                val titleText = passwordData.data.getSubTitle()
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
                        .setDescription(
                            describable = HomeDescribable.getPasswordOptionsDescribable(
                                name = passwordData.data.title
                            )
                        )
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
                            contentDescription = null,
                        )
                    }
                )
            }
        )
    }


}

@PreviewRestricted
@Composable
@Preview(
    widthDp = 360, heightDp = 120,
    showBackground = true
)
private fun PasswordListItemPreview() {
    HomeContent.PasswordListItem(
        modifier = Modifier
            .fillMaxWidth()
            .setSizeLimitation()
            .wrapContentHeight(),
        passwordData = PasswordData.testPasswordData.copy(
            data = SensitiveContent.testData.copy(
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

@PreviewRestricted
@Composable
@MobilePreview
private fun EmptyState() {
    HomeContent.EmptyListUI(
        modifier = Modifier.fillMaxSize(),
        vaultSelectedName = "Banking",
        searchUsed = true
    )
}