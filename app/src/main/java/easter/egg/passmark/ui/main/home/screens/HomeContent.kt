package easter.egg.passmark.ui.main.home.screens

import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
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
import androidx.fragment.app.FragmentActivity
import coil3.compose.SubcomposeAsyncImage
import easter.egg.passmark.R
import easter.egg.passmark.data.models.password.Password
import easter.egg.passmark.data.models.password.sensitive.SensitiveContent
import easter.egg.passmark.ui.main.MainViewModel
import easter.egg.passmark.ui.main.home.HomeViewModel
import easter.egg.passmark.ui.main.home.PasswordOptionChoices
import easter.egg.passmark.ui.main.home.SecurityPromptState
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.accessibility.Describable.Companion.setDescription
import easter.egg.passmark.utils.accessibility.home.HomeDescribable
import easter.egg.passmark.utils.annotation.MobileHorizontalPreview
import easter.egg.passmark.utils.annotation.MobilePreview
import easter.egg.passmark.utils.annotation.PreviewRestricted
import easter.egg.passmark.utils.security.biometrics.BiometricsHandler
import easter.egg.passmark.utils.values.PassMarkDimensions
import easter.egg.passmark.utils.values.PassMarkFonts
import easter.egg.passmark.utils.values.setSizeLimitation
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object HomeContent {
    private val TAG = this::class.simpleName

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HomeContent(
        modifier: Modifier,
        mainViewModel: MainViewModel,
        homeViewModel: HomeViewModel,
        toViewPasswordScreen: (password: Password) -> Unit,
        toPasswordEditScreen: (password: Password?) -> Unit,
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
            val optionSheetIsVisible: MutableState<Password?> = remember { mutableStateOf(null) }
            val coroutineScope = rememberCoroutineScope()
            optionSheetIsVisible.value?.let { password ->
                PasswordOptionBottomSheet(
                    password = password,
                    sheetState = sheetState,
                    dismissSheet = {
                        coroutineScope
                            .launch { sheetState.hide() }
                            .invokeOnCompletion { optionSheetIsVisible.value = null }
                    },
                    toPasswordEditScreen = {
                        coroutineScope
                            .launch { sheetState.hide() }
                            .invokeOnCompletion {
                                toPasswordEditScreen(password)
                                optionSheetIsVisible.value = null
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
                        items = passwordList ?: listOf(),
                        itemContent = {
                            PasswordListItem(
                                modifier = listingModifier.setDescription(
                                    describable = HomeDescribable.getPasswordDescribable(
                                        name = it.data.title
                                    )
                                ),
                                password = it,
                                viewPassword = { toViewPasswordScreen(it) },
                                openOptions = {
                                    optionSheetIsVisible.value = it
                                    coroutineScope.launch { sheetState.show() }
                                }
                            )
                        }
                    )
                    item { Spacer(modifier = spacerModifier) }
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
                        .border(
                            width = 1.dp,
                            color = if (password.localId == null) Color.Transparent else MaterialTheme.colorScheme.surfaceContainerHighest,
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
                                text = password.data.getShortName()
                            )
                        }
                        SubcomposeAsyncImage(
                            model = password.data.getFavicon(),
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
                        .setDescription(describable = HomeDescribable.getPasswordOptionsDescribable(name = password.data.title))
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PasswordOptionBottomSheet(
        password: Password,
        sheetState: SheetState,
        dismissSheet: () -> Unit,
        toPasswordEditScreen: () -> Unit,
        setPromptState: (SecurityPromptState) -> Unit
    ) {
        ModalBottomSheet(
            onDismissRequest = dismissSheet,
            sheetState = sheetState,
            content = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    content = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    end = 16.dp,
                                    start = 16.dp,
                                    bottom = 8.dp
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            content = {
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    fontFamily = PassMarkFonts.font,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = PassMarkFonts.Headline.medium,
                                    text = password.data.title,
                                )
                                password.data.getSubTitle()?.let {
                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        maxLines = 1,
                                        fontFamily = PassMarkFonts.font,
                                        fontSize = PassMarkFonts.Label.medium,
                                        fontWeight = FontWeight.Normal,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        text = it
                                    )
                                }
                            }
                        )

                        @Composable
                        fun SheetButton(
                            modifier: Modifier = Modifier,
                            mainIcon: ImageVector,
                            text: String,
                            actionIcon: ImageVector,
                            onClick: () -> Unit,
                        ) {
                            Column(
                                modifier = modifier.width(width = PassMarkDimensions.minTouchSize * 2),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                content = {
                                    ConstraintLayout(
                                        modifier = Modifier
                                            .clip(shape = CircleShape)
                                            .background(MaterialTheme.colorScheme.primaryContainer)
                                            .clickable(onClick = onClick),
                                        content = {
                                            val (mainIconRef, secondaryIconRef) = createRefs()


                                            Icon(
                                                modifier = Modifier
                                                    .size(size = 36.dp)
                                                    .constrainAs(
                                                        ref = mainIconRef,
                                                        constrainBlock = {
                                                            val margin = 16.dp
                                                            this.top.linkTo(
                                                                anchor = parent.top,
                                                                margin = margin
                                                            )
                                                            this.bottom.linkTo(
                                                                anchor = parent.bottom,
                                                                margin = margin
                                                            )
                                                            this.start.linkTo(
                                                                anchor = parent.start,
                                                                margin = margin
                                                            )
                                                            this.end.linkTo(
                                                                anchor = parent.end,
                                                                margin = margin
                                                            )
                                                        }
                                                    ),
                                                imageVector = mainIcon,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                                            )

                                            Icon(
                                                modifier = Modifier
                                                    .size(size = 24.dp)
                                                    .clip(shape = CircleShape)
                                                    .background(color = MaterialTheme.colorScheme.primaryContainer)
                                                    .padding(all = 4.dp)
                                                    .constrainAs(
                                                        ref = secondaryIconRef,
                                                        constrainBlock = {
                                                            this.top.linkTo(mainIconRef.bottom)
                                                            this.bottom.linkTo(mainIconRef.bottom)
                                                            this.start.linkTo(mainIconRef.end)
                                                            this.end.linkTo(mainIconRef.end)
                                                        }
                                                    ),
                                                imageVector = actionIcon,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    )
                                    Text(
                                        modifier = Modifier
                                            .setSizeLimitation()
                                            .padding(horizontal = 4.dp),
                                        text = text,
                                        fontFamily = PassMarkFonts.font,
                                        fontSize = PassMarkFonts.Body.medium,
                                        lineHeight = PassMarkFonts.Body.medium,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 2,
                                        minLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            )
                        }

                        val clipboardManager = LocalClipboardManager.current
                        val context = LocalContext.current
                        fun copy(str: String) {
                            copyToClipBoard(
                                clipboardManager = clipboardManager,
                                context = context,
                                str = str
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(state = rememberScrollState())
                                .padding(
                                    top = 8.dp,
                                    bottom = 16.dp
                                ),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically,
                            content = {
                                password.data.website?.let { website ->
                                    SheetButton(
                                        mainIcon = Icons.Default.Web,
                                        text = "Website",
                                        onClick = { copy(str = website) },
                                        actionIcon = Icons.Default.ContentCopy,
                                    )
                                }
                                password.data.email?.let { email ->
                                    SheetButton(
                                        mainIcon = Icons.Default.Email,
                                        text = "Copy email",
                                        onClick = { copy(str = email) },
                                        actionIcon = Icons.Default.ContentCopy
                                    )
                                }
                                password.data.userName?.let { userName ->
                                    SheetButton(
                                        mainIcon = Icons.Default.Person,
                                        text = "Copy user name",
                                        onClick = { copy(str = userName) },
                                        actionIcon = Icons.Default.ContentCopy,
                                    )
                                }
                                fun Context.findFragmentActivity(): FragmentActivity? {
                                    var ctx = this
                                    while (ctx is ContextWrapper) {
                                        if (ctx is FragmentActivity) return ctx
                                        ctx = ctx.baseContext
                                    }
                                    return null
                                }
                                SheetButton(
                                    mainIcon = Icons.Default.Password,
                                    text = "Copy password",
                                    onClick = {
                                        if (password.data.useFingerPrint) {
                                            (context.findFragmentActivity())?.let { activity ->
                                                BiometricsHandler.performBiometricAuthentication(
                                                    context = context,
                                                    activity = activity,
                                                    onComplete = {
                                                        if (it == BiometricsHandler.BiometricHandlerOutput.AUTHENTICATED) {
                                                            copy(str = password.data.password)
                                                        } else {
                                                            it.handleToast(context = context)
                                                        }
                                                    },
                                                    onBiometricsNotPresent = {
                                                        setPromptState(
                                                            SecurityPromptState(
                                                                password = password.data.password,
                                                                action = PasswordOptionChoices.COPY
                                                            )
                                                        )
                                                    }
                                                )
                                            }
                                        } else {
                                            copy(str = password.data.password)
                                        }
                                        dismissSheet()
                                    },
                                    actionIcon =
                                        if (password.data.useFingerPrint) Icons.Default.Fingerprint
                                        else Icons.Default.ContentCopy,
                                )
                                SheetButton(
                                    modifier = Modifier.setDescription(
                                        describable = HomeDescribable.PasswordOptionsBottomSheet.EDIT_BUTTON
                                    ),
                                    mainIcon = Icons.Default.Edit,
                                    text = "Edit Password",
                                    onClick = {
                                        if (password.data.useFingerPrint) {
                                            Log.d(TAG, "requires fingerprint")
                                            (context.findFragmentActivity())?.let {
                                                Log.d(TAG, "fingerprint prompt opening")
                                                BiometricsHandler.performBiometricAuthentication(
                                                    context = context,
                                                    activity = it,
                                                    onComplete = { biometricHandlerOutput ->
                                                        if (biometricHandlerOutput == BiometricsHandler.BiometricHandlerOutput.AUTHENTICATED) {
                                                            toPasswordEditScreen()
                                                        } else {
                                                            biometricHandlerOutput.handleToast(
                                                                context = context
                                                            )
                                                        }
                                                    },
                                                    onBiometricsNotPresent = {
                                                        setPromptState(
                                                            SecurityPromptState(
                                                                password = password.data.password,
                                                                action = PasswordOptionChoices.EDIT
                                                            )
                                                        )
                                                    }
                                                )
                                            }
                                        } else {
                                            toPasswordEditScreen()
                                        }
                                    },
                                    actionIcon = Icons.AutoMirrored.Filled.ArrowRight
                                )
                            }
                        )
                    }
                )
            }
        )
    }

    private fun copyToClipBoard(
        clipboardManager: ClipboardManager,
        context: Context,
        str: String
    ) {
        clipboardManager.setText(AnnotatedString(text = str))
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            Toast
                .makeText(
                    context,
                    "Text copied to clipboard",
                    Toast.LENGTH_LONG
                )
                .show()
        } else {
            Log.d(TAG, "system has it's own toast")
        }
    }
}

@OptIn(PreviewRestricted::class)
@Composable
@MobilePreview
@MobileHorizontalPreview
private fun HomeContentPreview() {
    HomeContent.HomeContent(
        modifier = Modifier.fillMaxSize(),
        mainViewModel = MainViewModel.getTestViewModel(),
        toViewPasswordScreen = {},
        toPasswordEditScreen = {},
        homeViewModel = HomeViewModel.getTestViewModel()
    )
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
        password = Password.testPassword.copy(
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
@MobileHorizontalPreview
private fun EmptyState() {
    HomeContent.EmptyListUI(
        modifier = Modifier.fillMaxSize(),
        vaultSelectedName = "Banking",
        searchUsed = true
    )
}

@Deprecated(message = "Do not use in production")
@PreviewRestricted
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@MobilePreview
@Preview(
    widthDp = 600,
    heightDp = 800,
    showBackground = true
)
private fun PasswordOptionDrawerPreview() {
    HomeContent.PasswordOptionBottomSheet(
        password = Password.testPassword,
        sheetState = rememberModalBottomSheetState().apply { runBlocking { this@apply.show() } },
        dismissSheet = {},
        toPasswordEditScreen = {},
        setPromptState = {}
    )
}