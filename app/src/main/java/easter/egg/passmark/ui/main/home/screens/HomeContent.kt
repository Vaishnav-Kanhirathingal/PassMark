package easter.egg.passmark.ui.main.home.screens

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Web
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import easter.egg.passmark.R
import easter.egg.passmark.data.models.content.Password
import easter.egg.passmark.data.models.content.PasswordData
import easter.egg.passmark.data.models.content.PasswordSortingOptions
import easter.egg.passmark.data.supabase.api.VaultApi
import easter.egg.passmark.di.supabase.SupabaseModule
import easter.egg.passmark.ui.main.MainViewModel
import easter.egg.passmark.ui.main.home.HomeViewModel
import easter.egg.passmark.ui.main.home.SecurityChoices
import easter.egg.passmark.ui.main.home.SecurityPromptState
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.annotation.MobileHorizontalPreview
import easter.egg.passmark.utils.annotation.MobilePreview
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
        toViewPasswordScreen: (passwordId: Int) -> Unit,
        toPasswordEditScreen: (passwordId: Int) -> Unit,
    ) {
        val listItemModifier = Modifier
            .setSizeLimitation()
            .fillMaxWidth()
        val vaultId = homeViewModel.vaultIdSelected.collectAsState().value
        val homeResult = (mainViewModel.screenState.collectAsState().value as? ScreenState.Loaded)
            ?.result
        val passwordList = homeResult
            ?.getFilteredPasswordList(
                vaultId = vaultId,
                passwordSortingOptions = PasswordSortingOptions.NAME,
                ascending = true
            )
            ?.collectAsState(initial = listOf())
            ?.value
            ?: listOf()
        if (passwordList.isEmpty()) {
            EmptyListUI(
                modifier = modifier,
                vaultSelectedName = homeResult?.vaultListState?.collectAsState()?.value
                    ?.find { v -> v.id == homeViewModel.vaultIdSelected.collectAsState().value }
                    ?.name
            )
        } else {
            val sheetState = rememberModalBottomSheetState()
            val optionSheetIsVisible: MutableState<Password?> = remember { mutableStateOf(null) }
            val coroutineScope = rememberCoroutineScope()
            optionSheetIsVisible.value?.let { password ->
                PasswordOptionDrawer(
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
                                toPasswordEditScreen(password.id!!)
                                optionSheetIsVisible.value = null
                            }
                    },
                    setPromptState = { homeViewModel.securityPromptState.value = it }
                )
            }
            val securityPromptState = homeViewModel.securityPromptState.collectAsState().value
            val clipboardManager = LocalClipboardManager.current
            val context = LocalContext.current

            LaunchedEffect(
                key1 = securityPromptState,
                block = {
                    if (securityPromptState?.securityChoices == SecurityChoices.BIOMETRICS) {
                        (context as? FragmentActivity)?.let {
                            BiometricsHandler.performBiometricAuthentication(
                                activity = it,
                                onComplete = { homeViewModel.securityPromptState.value = null },
                                onSuccess = {
                                    copyToClipBoard(
                                        clipboardManager = clipboardManager,
                                        context = context,
                                        str = securityPromptState.password
                                    )
                                },
                                showToast = { toastMessage ->
                                    Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
                                }
                            )
                            Log.d(TAG, "showing prompt")
                        }
                    }
                }
            )
            if (securityPromptState?.securityChoices == SecurityChoices.MASTER_PASSWORD) {
                TODO("show master password prompt")
            }

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
        vaultSelectedName: String?
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
                    text = vaultSelectedName.let {
                        if (it == null) {
                            "You do not have any saved passwords"
                        } else {
                            "Vault '$vaultSelectedName' does not have any saved passwords"
                        }
                    },
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PasswordOptionDrawer(
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
                        fun ColumnScope.SheetButton(
                            startIcon: ImageVector,
                            title: String,
                            onClick: () -> Unit,
                            endIcon: ImageVector,
                            useDivider: Boolean = true
                        ) {
                            Row(
                                modifier = Modifier
                                    .setSizeLimitation()
                                    .fillMaxWidth()
                                    .clickable(onClick = onClick)
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(
                                    space = 8.dp,
                                    alignment = Alignment.CenterHorizontally
                                ),
                                verticalAlignment = Alignment.CenterVertically,
                                content = {
                                    Icon(
                                        imageVector = startIcon,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        modifier = Modifier.weight(weight = 1f),
                                        fontFamily = PassMarkFonts.font,
                                        fontSize = PassMarkFonts.Body.medium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        text = title
                                    )
                                    Icon(
                                        imageVector = endIcon,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            )
                            if (useDivider) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
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
                        password.data.website?.let { website ->
                            SheetButton(
                                startIcon = Icons.Default.Web,
                                title = "Website",
                                onClick = { copy(str = website) },
                                endIcon = Icons.Default.ContentCopy,
                            )
                        }
                        password.data.email?.let { email ->
                            SheetButton(
                                startIcon = Icons.Outlined.Email,
                                title = "Copy email",
                                onClick = { copy(str = email) },
                                endIcon = Icons.Default.ContentCopy
                            )
                        }
                        password.data.userName?.let { userName ->
                            SheetButton(
                                startIcon = Icons.Outlined.Person,
                                title = "Copy user name",
                                onClick = { copy(str = userName) },
                                endIcon = Icons.Default.ContentCopy,
                            )
                        }
                        SheetButton(
                            startIcon = Icons.Default.Password,
                            title = "Copy password",
                            onClick = {
                                if (password.data.useFingerPrint) {
                                    val securityChoice = getSecurityChoices(context = context)
                                    if (securityChoice == null) { // show toast or open settings
                                        if (Build.VERSION.SDK_INT > 29) {
                                            try {
                                                val enrollIntent =
                                                    Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                                                        putExtra(
                                                            Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                                            BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                                                        )
                                                    }
                                                context.startActivity(enrollIntent)
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        }
                                        Toast.makeText(
                                            context,
                                            "Biometrics not enabled on device. Go to Settings -> Security -> Fingerprint -> set fingerprint",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        setPromptState(
                                            SecurityPromptState(
                                                password = password.data.password,
                                                securityChoices = securityChoice
                                            )
                                        )
                                    }
                                } else {
                                    copy(str = password.data.password)
                                }
                                dismissSheet()
                            },
                            endIcon =
                                if (password.data.useFingerPrint) Icons.Default.Fingerprint
                                else Icons.Default.ContentCopy,
                        )

                        SheetButton(
                            startIcon = Icons.Default.Edit,
                            title = "Edit Password",
                            onClick = toPasswordEditScreen,
                            endIcon = Icons.AutoMirrored.Filled.ArrowRight,
                            useDivider = false
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

    private fun getSecurityChoices(
        context: Context,
    ): SecurityChoices? {
        return when (
            BiometricManager.from(context).canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
        ) {
            BiometricManager.BIOMETRIC_SUCCESS -> SecurityChoices.BIOMETRICS
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> null
            else -> SecurityChoices.MASTER_PASSWORD
        }
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
        mainViewModel = MainViewModel.getTestViewModel(),
        toViewPasswordScreen = {},
        toPasswordEditScreen = {},
        homeViewModel = HomeViewModel(
            vaultApi = VaultApi(SupabaseModule.mockClient)
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
        ),
        viewPassword = {},
        openOptions = {}
    )
}

@Composable
@MobilePreview
@MobileHorizontalPreview
private fun EmptyState() {
    HomeContent.EmptyListUI(
        modifier = Modifier.fillMaxSize(),
        vaultSelectedName = "Banking"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@MobilePreview
private fun PasswordOptionDrawerPreview() {
    val now = System.currentTimeMillis()
    HomeContent.PasswordOptionDrawer(
        password = Password(
            data = PasswordData(
                title = "Google",
                email = "someone@gmail.com",
                userName = "some_user",
                password = "Some password",
                website = "www.somewebsite.com",
                useFingerPrint = true,
                saveToLocalOnly = true,
                notes = null
            ),
            created = now,
            lastUsed = now,
            lastModified = now,
            usedCount = 0
        ),
        sheetState = rememberModalBottomSheetState().apply { runBlocking { this@apply.show() } },
        dismissSheet = {},
        toPasswordEditScreen = {},
        setPromptState = {}
    )
}