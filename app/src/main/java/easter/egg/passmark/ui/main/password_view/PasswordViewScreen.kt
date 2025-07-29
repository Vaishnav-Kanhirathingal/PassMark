package easter.egg.passmark.ui.main.password_view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.fragment.app.FragmentActivity
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import easter.egg.passmark.data.models.Vault
import easter.egg.passmark.data.models.Vault.Companion.getIcon
import easter.egg.passmark.data.models.password.Password
import easter.egg.passmark.data.models.password.sensitive.PasswordHistory
import easter.egg.passmark.data.storage.database.PasswordDao
import easter.egg.passmark.data.supabase.api.PasswordApi
import easter.egg.passmark.di.supabase.SupabaseModule
import easter.egg.passmark.ui.main.MainViewModel
import easter.egg.passmark.ui.shared_components.ConfirmationDialog
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.accessibility.Describable.Companion.setDescription
import easter.egg.passmark.utils.accessibility.main.PasswordViewDescribable
import easter.egg.passmark.utils.annotation.MobilePreview
import easter.egg.passmark.utils.annotation.PreviewRestricted
import easter.egg.passmark.utils.extensions.customTopBarModifier
import easter.egg.passmark.utils.security.biometrics.BiometricsHandler
import easter.egg.passmark.utils.values.PassMarkDimensions
import easter.egg.passmark.utils.values.PassMarkFonts
import easter.egg.passmark.utils.values.setSizeLimitation
import java.time.ZoneId

object PasswordViewScreen {
    private val TAG = this::class.simpleName

    @Composable
    fun Screen(
        modifier: Modifier,
        password: Password,
        navigateUp: () -> Unit,
        toEditScreen: () -> Unit,
        associatedVault: Vault?,
        passwordViewViewModel: PasswordViewViewModel,
        mainViewModel: MainViewModel
    ) {
        LaunchedEffect(
            key1 = Unit,
            block = {
                passwordViewViewModel.updateUsageStats(
                    password = password,
                    passwordCryptographyHandler = mainViewModel.passwordCryptographyHandler,
                    onComplete = {
                        (mainViewModel.screenState.value as? ScreenState.Loaded)
                            ?.result
                            ?.upsertPassword(it)
                    }
                )
            }
        )
        Scaffold(
            modifier = modifier,
            topBar = {
                PasswordViewTopBar(
                    modifier = Modifier.customTopBarModifier(),
                    navigateUp = navigateUp,
                    toEditScreen = toEditScreen,
                    requireFingerprint = password.data.useFingerPrint
                )
            },
            content = {
                val dialogState = passwordViewViewModel.deleteDialogState.collectAsState().value
                if (passwordViewViewModel.deleteDialogVisibility.collectAsState().value) {
                    ConfirmationDialog(
                        modifier = Modifier.fillMaxWidth(),
                        titleText = "Delete?",
                        contentText = "Note : Deleting this password is unrecoverable/irreversible. " +
                                "You can instead transfer this password to another vault if " +
                                "you wish so.",
                        negativeButtonText = "Cancel",
                        negativeDescribable = PasswordViewDescribable.DeletePasswordDialog.CANCEL_BUTTON,
                        onNegativeClicked = {
                            passwordViewViewModel.setDeleteDialogVisibility(visibility = false)
                        },
                        positiveButtonText = "Delete",
                        positiveDescribable = PasswordViewDescribable.DeletePasswordDialog.DELETE_BUTTON,
                        onPositiveClicked = { passwordViewViewModel.delete(password = password) },
                        screenState = dialogState
                    )
                }

                val context = LocalContext.current
                LaunchedEffect(
                    key1 = dialogState,
                    block = {
                        when (dialogState) {
                            is ScreenState.PreCall, is ScreenState.Loading -> {}
                            is ScreenState.ApiError -> dialogState.manageToastActions(context = context)
                            is ScreenState.Loaded -> {
                                (mainViewModel.screenState.value as? ScreenState.Loaded)
                                    ?.result?.deletePassword(password = password)
                                passwordViewViewModel.setDeleteDialogVisibility(visibility = false)
                                navigateUp()
                            }
                        }
                    }
                )

                PasswordViewContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = it),
                    password = password,
                    lastUpdatedTimeBeforeUpdate = passwordViewViewModel.lastUpdatedTimeBeforeCall,
                    associatedVault = associatedVault,
                    showDialog = { passwordViewViewModel.setDeleteDialogVisibility(visibility = true) }
                )
            }
        )
    }

    @Composable
    private fun PasswordViewTopBar(
        modifier: Modifier,
        navigateUp: () -> Unit,
        toEditScreen: () -> Unit,
        requireFingerprint: Boolean
    ) {
        val barSize = PassMarkDimensions.minTouchSize
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.Start
            ),
            verticalAlignment = Alignment.CenterVertically,
            content = {
                Box(
                    modifier = Modifier
                        .size(size = barSize)
                        .clip(shape = CircleShape)
                        .background(color = MaterialTheme.colorScheme.primaryContainer)
                        .clickable(onClick = navigateUp),
                    contentAlignment = Alignment.Center,
                    content = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                )
                Spacer(modifier = Modifier.weight(weight = 1f))
                val context = LocalContext.current
                Row(
                    modifier = Modifier
                        .setSizeLimitation()
                        .height(height = barSize)
                        .widthIn(min = barSize)
                        .clip(shape = CircleShape)
                        .background(color = MaterialTheme.colorScheme.primaryContainer)
                        .clickable(
                            onClick = {
                                if (requireFingerprint) {
                                    (context as? FragmentActivity)?.let {
                                        BiometricsHandler.performBiometricAuthentication(
                                            context = context,
                                            activity = it,
                                            onComplete = { biometricHandlerOutput ->
                                                if (biometricHandlerOutput == BiometricsHandler.BiometricHandlerOutput.AUTHENTICATED) {
                                                    toEditScreen()
                                                } else {
                                                    biometricHandlerOutput.handleToast(context = context)
                                                }
                                            },
                                        )
                                    }
                                } else {
                                    toEditScreen()
                                }
                            }
                        )
                        .padding(
                            start = 20.dp,
                            end = 24.dp
                        ),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 8.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    content = {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Edit",
                            fontFamily = PassMarkFonts.font,
                            fontSize = PassMarkFonts.Title.medium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                )
            }
        )
    }

    @Composable
    private fun PasswordViewContent(
        modifier: Modifier,
        password: Password,
        lastUpdatedTimeBeforeUpdate: Long?,
        associatedVault: Vault?,
        showDialog: () -> Unit
    ) {
        Column(
            modifier = modifier.verticalScroll(state = rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 16.dp,
                alignment = Alignment.Top
            ),
            content = {
                val biometricAuthenticated = remember { mutableStateOf(false) }
                val showHistory = remember { mutableStateOf(false) }
                val context = LocalContext.current
                fun showBiometricPrompt(
                    forHistory: Boolean
                ) {
                    (context as? FragmentActivity)?.let { activity ->
                        BiometricsHandler.performBiometricAuthentication(
                            context = context,
                            activity = activity,
                            onComplete = { biometricHandlerOutput ->
                                if (biometricHandlerOutput == BiometricsHandler.BiometricHandlerOutput.AUTHENTICATED) {
                                    biometricAuthenticated.value = true
                                    if (forHistory) {
                                        showHistory.value = true
                                    }
                                } else {
                                    biometricHandlerOutput.handleToast(context = context)
                                }
                            },
                        )
                    }
                }

                if (showHistory.value) {
                    PasswordHistoryDialog(
                        modifier = Modifier.fillMaxWidth(),
                        passwordHistory = password.data.passwordHistory.toMutableList().apply {
                            this.add(element = password.currentPasswordAsPasswordHistory())
                        }.reversed(),
                        dismiss = { showHistory.value = false }
                    )
                }

                Heading(
                    modifier = Modifier.fillMaxWidth(),
                    password = password,
                    associatedVault = associatedVault
                )
                val itemModifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                val displayFieldContentModifier = Modifier.fillMaxWidth()
                val clipboardManager = LocalClipboardManager.current
                fun copy(str: String) {
                    copyToClipBoard(
                        clipboardManager = clipboardManager,
                        context = context,
                        str = str
                    )
                }
                PropertyListCard(
                    modifier = itemModifier,
                    itemList = mutableListOf<@Composable () -> Unit>().apply {
                        password.data.email?.let { email ->
                            this.add(
                                element = {
                                    DisplayFieldContent(
                                        modifier = displayFieldContentModifier,
                                        startIcon = Icons.Default.Email,
                                        titleText = "Email",
                                        fieldText = email,
                                        endIcon = Icons.Default.ContentCopy,
                                        endIconOnClick = { copy(str = email) },
                                        endIconDescribable = PasswordViewDescribable.EMAIL_COPY_BUTTON
                                    )
                                }
                            )
                        }
                        password.data.userName?.let { username ->
                            this.add(
                                element = {
                                    DisplayFieldContent(
                                        modifier = displayFieldContentModifier,
                                        startIcon = Icons.Default.Person,
                                        titleText = "Username",
                                        fieldText = username,
                                        endIcon = Icons.Default.ContentCopy,
                                        endIconOnClick = { copy(str = username) },
                                        endIconDescribable = PasswordViewDescribable.USER_NAME_COPY_BUTTON
                                    )
                                }
                            )
                        }
                        this.add(
                            element = {
                                val accessGranted =
                                    derivedStateOf { biometricAuthenticated.value || !password.data.useFingerPrint }
                                DisplayFieldContent(
                                    modifier = displayFieldContentModifier,
                                    startIcon = Icons.Default.Password,
                                    titleText = "Password",
                                    fieldText =
                                        if (accessGranted.value) password.data.password
                                        else "*".repeat(n = 12),
                                    endIcon =
                                        if (accessGranted.value) Icons.Default.ContentCopy
                                        else Icons.Default.Fingerprint,
                                    endIconOnClick = {
                                        if (accessGranted.value) copy(str = password.data.password)
                                        else showBiometricPrompt(forHistory = false)
                                    },
                                    endIconDescribable =
                                        if (accessGranted.value) PasswordViewDescribable.PASSWORD_COPY_BUTTON
                                        else PasswordViewDescribable.PASSWORD_FINGERPRINT_VERIFICATION_BUTTON,
                                    onShowPasswordHistory = {
                                        if (accessGranted.value) {
                                            showHistory.value = true
                                        } else {
                                            showBiometricPrompt(forHistory = true)
                                        }
                                    }
                                )
                            }
                        )
                    },
                )

                password.data.website?.let { website ->
                    DefaultCard(
                        modifier = itemModifier,
                        content = {
                            DisplayFieldContent(
                                modifier = Modifier.fillMaxWidth(),
                                startIcon = Icons.Default.Web,
                                titleText = "Website",
                                fieldText = website,
                                endIcon = Icons.Default.ContentCopy,
                                endIconOnClick = { copy(str = website) },
                                endIconDescribable = PasswordViewDescribable.WEBSITE_COPY_BUTTON
                            )
                        }
                    )
                }
                password.data.notes?.let { notes ->
                    DefaultCard(
                        modifier = itemModifier,
                        content = {
                            DisplayFieldContent(
                                modifier = Modifier.fillMaxWidth(),
                                startIcon = Icons.Default.EditNote,
                                titleText = "Notes",
                                fieldText = notes,
                                singleLine = false,
                                endIconDescribable = null
                            )
                        }
                    )
                }
                PropertyListCard(
                    modifier = itemModifier,
                    itemList = listOf(
                        {
                            DisplayFieldContent(
                                modifier = Modifier.fillMaxWidth(),
                                startIcon = Icons.Default.Cloud,
                                titleText = "Storage Type",
                                fieldText = if (password.localId != null) "Saved to device only" else "Saved on the cloud",
                                endIconDescribable = null
                            )
                        },
                        {
                            DisplayFieldContent(
                                modifier = Modifier.fillMaxWidth(),
                                startIcon = Icons.Default.Fingerprint,
                                titleText = "Fingerprint Authentication",
                                fieldText = if (password.data.useFingerPrint) "Enabled" else "Disabled",
                                endIconDescribable = null
                            )
                        }
                    ),
                )
                val deleteShape = RoundedCornerShape(size = 16.dp)
                PropertyListCard(
                    modifier = itemModifier,
                    itemList = mutableListOf<@Composable () -> Unit>().apply {
                        this.add(
                            element = {
                                DisplayFieldContent(
                                    modifier = displayFieldContentModifier,
                                    startIcon = Icons.Default.CalendarToday,
                                    titleText = "Created",
                                    fieldText = password.created.formatToTime(),
                                    endIconDescribable = null
                                )
                            }
                        )
                        this.add(
                            element = {
                                DisplayFieldContent(
                                    modifier = displayFieldContentModifier,
                                    startIcon = Icons.Default.EditCalendar,
                                    titleText = "Updated",
                                    fieldText = password.lastModified.formatToTime(),
                                    endIconDescribable = null
                                )
                            }
                        )
                        this.add(
                            element = {
                                DisplayFieldContent(
                                    modifier = displayFieldContentModifier,
                                    startIcon = Icons.Default.EventRepeat,
                                    titleText = "Last Used",
                                    fieldText = (lastUpdatedTimeBeforeUpdate ?: password.lastUsed)
                                        .formatToTime(),
                                    endIconDescribable = null
                                )
                            }
                        )
                    },
                )
                Box(
                    modifier = Modifier
                        .setDescription(describable = PasswordViewDescribable.DELETE_PASSWORD_BUTTON)
                        .align(alignment = Alignment.End)
                        .setSizeLimitation()
                        .padding(horizontal = 16.dp)
                        .clip(shape = deleteShape)
                        .background(color = MaterialTheme.colorScheme.error)
                        .clickable(onClick = showDialog),
                    contentAlignment = Alignment.Center,
                    content = {
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            fontFamily = PassMarkFonts.font,
                            fontSize = PassMarkFonts.Body.medium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onError,
                            text = "Delete"
                        )
                    }
                )

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height = PassMarkDimensions.minTouchSize * 3)
                )
            }
        )
    }

    @Composable
    private fun Heading(
        password: Password,
        modifier: Modifier,
        associatedVault: Vault?
    ) {
        ConstraintLayout(
            modifier = modifier,
            content = {
                val (passwordIcon, title, subtitle) = createRefs()
                Box(
                    modifier = Modifier
                        .size(size = 100.dp)
                        .clip(shape = RoundedCornerShape(size = 24.dp))
                        .background(color = MaterialTheme.colorScheme.surfaceContainer)
                        .border(
                            width = if (password.localId != null) 2.dp else 0.dp,
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            shape = RoundedCornerShape(size = 24.dp)
                        )
                        .constrainAs(
                            ref = passwordIcon,
                            constrainBlock = {
                                this.top.linkTo(anchor = parent.top, margin = 16.dp)
                                this.bottom.linkTo(anchor = parent.bottom, margin = 16.dp)
                                this.start.linkTo(anchor = parent.start, margin = 16.dp)
                            }
                        ),
                    contentAlignment = Alignment.Center,
                    content = {
                        val showText: MutableState<Boolean> =
                            remember { mutableStateOf(true) }
                        val model = ImageRequest.Builder(LocalContext.current)
                            .data(password.data.getFavicon())
                            .crossfade(true)
                            .listener(onSuccess = { _, _ -> showText.value = false })
                            .build()
                        if (showText.value) {
                            Text(
                                textAlign = TextAlign.Center,
                                fontFamily = PassMarkFonts.font,
                                fontSize = PassMarkFonts.Display.medium,
                                fontWeight = FontWeight.Bold,
                                text = password.data.getShortName(),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        AsyncImage(
                            model = model,
                            modifier = Modifier.size(size = 72.dp),
                            contentScale = ContentScale.Fit,
                            contentDescription = null,
                            imageLoader = ImageLoader(context = LocalContext.current),
                        )
                    }
                )
                Text(
                    modifier = Modifier.constrainAs(
                        ref = title,
                        constrainBlock = {
                            this.top.linkTo(anchor = parent.top, margin = 16.dp)
                            this.bottom.linkTo(subtitle.top)
                            this.start.linkTo(anchor = passwordIcon.end, margin = 16.dp)
                            this.end.linkTo(anchor = parent.end, margin = 16.dp)
                            width = Dimension.fillToConstraints
                        }
                    ),
                    text = password.data.title,
                    fontFamily = PassMarkFonts.font,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = PassMarkFonts.Display.medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(size = 24.dp))
                        .background(color = MaterialTheme.colorScheme.surfaceContainer)
                        .padding(
                            vertical = 2.dp,
                            horizontal = 4.dp
                        )
                        .constrainAs(
                            ref = subtitle,
                            constrainBlock = {
                                this.start.linkTo(anchor = passwordIcon.end, margin = 16.dp)
                                this.top.linkTo(anchor = title.bottom)
                                width = Dimension.wrapContent
                            }
                        ),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 4.dp,
                        alignment = Alignment.Start
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    content = {
                        Icon(
                            modifier = Modifier.size(size = 16.dp),
                            imageVector = associatedVault.getIcon(),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .widthIn(max = 120.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = associatedVault?.name ?: Vault.VAULT_NAME_FOR_ALL_ITEMS,
                            fontFamily = PassMarkFonts.font,
                            fontSize = PassMarkFonts.Label.small,
                            lineHeight = PassMarkFonts.Label.small,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
            }
        )
    }

    @Composable
    private fun PropertyListCard(
        itemList: List<@Composable () -> Unit>,
        modifier: Modifier,
    ) {
        DefaultCard(
            modifier = modifier,
            content = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    content = {
                        itemList.forEachIndexed { index, composable ->
                            composable()
                            if (index != itemList.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.surfaceContainerHighest
                                )
                            }
                        }
                    }
                )
            }
        )
    }

    @Composable
    private fun DefaultCard(
        modifier: Modifier,
        content: @Composable BoxScope.() -> Unit
    ) {
        val shape = RoundedCornerShape(size = 16.dp)
        Box(
            modifier = modifier
                .clip(shape = shape)
                .background(color = MaterialTheme.colorScheme.surfaceContainer)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    shape = shape
                ),
            contentAlignment = Alignment.Center,
            content = content
        )
    }

    /** make sure to have both [endIcon] and [endIconOnClick] as either null or non null
     */
    @Composable
    private fun DisplayFieldContent(
        modifier: Modifier,
        startIcon: ImageVector,
        titleText: String,
        fieldText: String,
        endIcon: ImageVector? = null,
        endIconOnClick: (() -> Unit)? = null,
        singleLine: Boolean = true,
        endIconDescribable: PasswordViewDescribable?,
        onShowPasswordHistory: (() -> Unit)? = null
    ) {
        Row(
            modifier = modifier
                .setSizeLimitation()
                .padding(all = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            content = {
                Icon(
                    modifier = Modifier.padding(all = 12.dp),
                    imageVector = startIcon,
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = null
                )
                Column(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    content = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = PassMarkFonts.font,
                            fontSize = PassMarkFonts.Label.medium,
                            fontWeight = FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = titleText,
                        )
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = PassMarkFonts.font,
                            fontSize = PassMarkFonts.Title.medium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = if (singleLine) 1 else Int.MAX_VALUE,
                            overflow = TextOverflow.Ellipsis,
                            text = fieldText,
                        )
                    }
                )
                onShowPasswordHistory?.let {
                    IconButton(
                        modifier = Modifier
                            .setDescription(describable = PasswordViewDescribable.PASSWORD_HISTORY_BUTTON)
                            .setSizeLimitation(),
                        onClick = it,
                        content = {
                            Icon(
                                imageVector = Icons.Default.EventRepeat,
                                contentDescription = null
                            )
                        }
                    )
                }
                endIcon?.let { icon ->
                    IconButton(
                        modifier = (
                                if (endIconDescribable == null) Modifier
                                else Modifier.setDescription(describable = endIconDescribable)
                                )
                            .setSizeLimitation(),
                        onClick = { endIconOnClick?.invoke() },
                        content = {
                            Icon(
                                imageVector = icon,
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PasswordHistoryDialog(
        modifier: Modifier,
        passwordHistory: List<PasswordHistory>,
        dismiss: () -> Unit
    ) {
        BasicAlertDialog(
            modifier = modifier
                .clip(shape = RoundedCornerShape(size = PassMarkDimensions.dialogRadius))
                .background(color = MaterialTheme.colorScheme.surfaceContainer),
            onDismissRequest = dismiss,
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    content = {
                        Text(
                            modifier = Modifier.padding(
                                top = 8.dp,
                                bottom = 16.dp
                            ),
                            text = "Password History",
                            fontFamily = PassMarkFonts.font,
                            fontWeight = FontWeight.Bold,
                            fontSize = PassMarkFonts.Title.medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 360.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(
                                space = 8.dp,
                                alignment = Alignment.CenterVertically
                            ),
                            content = {
                                itemsIndexed(
                                    items = passwordHistory,
                                    itemContent = { index, item ->
                                        PasswordHistoryListItem(
                                            passwordHistory = item,
                                            isCurrent = (index == 0)
                                        )
                                    }
                                )
                            }
                        )
                    }
                )
            }
        )
    }

    @Composable
    private fun PasswordHistoryListItem(
        passwordHistory: PasswordHistory,
        isCurrent: Boolean
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = RoundedCornerShape(size = 8.dp)
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    shape = RoundedCornerShape(size = 8.dp)
                )
                .padding(all = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            content = {
                Column(
                    modifier = Modifier
                        .weight(weight = 1f)
                        .padding(start = 12.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    content = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "${passwordHistory.password}${if (isCurrent) " (current)" else ""}",
                            fontFamily = PassMarkFonts.font,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = PassMarkFonts.Title.medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = passwordHistory.discardedOn.formatToTime(),
                            fontFamily = PassMarkFonts.font,
                            fontWeight = FontWeight.Medium,
                            fontSize = PassMarkFonts.Body.medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
                val clipboardManager = LocalClipboardManager.current
                val context = LocalContext.current
                IconButton(
                    modifier = Modifier.setSizeLimitation(),
                    onClick = {
                        copyToClipBoard(
                            clipboardManager = clipboardManager,
                            context = context,
                            str = passwordHistory.password
                        )
                    },
                    content = {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null
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
            Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_LONG).show()
        } else {
            Log.d(TAG, "system has it's own toast")
        }
    }

    private fun Long.formatToTime(): String {
        return try {
            val instant = java.time.Instant.ofEpochMilli(this)
            val dateTime =
                java.time.LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
            val formatted =
                dateTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss | dd/MM/yyyy"))
            formatted!!
        } catch (e: Exception) {
            e.printStackTrace()
            "Time Error"
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@PreviewRestricted
@Composable
@Preview(
    widthDp = 360,
    heightDp = 1200,
    showBackground = true
)
@MobilePreview
private fun PasswordViewScreenPreview() {
    PasswordViewScreen.Screen(
        modifier = Modifier.fillMaxSize(),
        password = Password.testPassword.copy(
            localId = 0,
            cloudId = null
        ),
        navigateUp = {},
        toEditScreen = {},
        associatedVault = null,
        passwordViewViewModel = PasswordViewViewModel(
            passwordApi = PasswordApi(supabaseClient = SupabaseModule.mockClient),
            passwordDao = PasswordDao.getTestingDao()
        ),
        mainViewModel = MainViewModel.getTestViewModel()
    )
}

@PreviewRestricted
@Preview(
    widthDp = 360,
    heightDp = 360,
    showBackground = true
)
@Composable
private fun PasswordHistoryDialogPreview() {
    PasswordViewScreen.PasswordHistoryDialog(
        modifier = Modifier.fillMaxWidth(),
        passwordHistory = listOf(
            PasswordHistory(
                password = "SomePassword",
                discardedOn = System.currentTimeMillis()
            ),
            PasswordHistory(
                password = "SomePassword",
                discardedOn = System.currentTimeMillis()
            ),
        ),
        dismiss = {}
    )
}