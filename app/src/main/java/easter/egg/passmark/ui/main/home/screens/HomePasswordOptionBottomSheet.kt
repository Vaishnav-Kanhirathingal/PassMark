package easter.egg.passmark.ui.main.home.screens

import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import easter.egg.passmark.data.models.password.PasswordData
import easter.egg.passmark.ui.main.home.PasswordOptionChoices
import easter.egg.passmark.ui.main.home.SecurityPromptState
import easter.egg.passmark.ui.shared_components.CustomLoader
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.accessibility.Describable.Companion.hideFromAccessibility
import easter.egg.passmark.utils.accessibility.Describable.Companion.setDescription
import easter.egg.passmark.utils.accessibility.main.HomeDescribable
import easter.egg.passmark.utils.annotation.MobilePreview
import easter.egg.passmark.utils.annotation.PreviewRestricted
import easter.egg.passmark.utils.functions.SharedFunctions
import easter.egg.passmark.utils.security.biometrics.BiometricsHandler
import easter.egg.passmark.utils.values.PassMarkDimensions
import easter.egg.passmark.utils.values.PassMarkFonts
import easter.egg.passmark.utils.values.setSizeLimitation
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object HomePasswordOptionBottomSheet {
    private val TAG = this::class.simpleName
    private val containerCornerRadius = 16.dp

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PasswordOptionBottomSheet(
        passwordData: PasswordData,
        sheetState: SheetState,
        dismissSheet: () -> Unit,
        toPasswordEditScreen: () -> Unit,
        setPromptState: (SecurityPromptState) -> Unit,
        onDeleteClick: () -> Unit,
        deleteState: State<ScreenState<PasswordData>>
    ) {
        val isLoading = deleteState.value.isLoading
        ModalBottomSheet(
            onDismissRequest = {
                if (!deleteState.value.isLoading) {
                    dismissSheet()
                }
            },
            sheetState = sheetState,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .width(width = PassMarkDimensions.minTouchSize)
                        .padding(top = 16.dp)
                        .height(height = 4.dp)
                        .clip(CircleShape)
                        .background(color = MaterialTheme.colorScheme.outline)
                )
            },
            properties = ModalBottomSheetProperties(shouldDismissOnBackPress = false),
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    content = {
                        val context = LocalContext.current
                        val clipboard = LocalClipboard.current
                        val scope = rememberCoroutineScope()
                        fun copy(text: String) {
                            scope.launch {
                                SharedFunctions.copyToClipboard(
                                    clipboard = clipboard,
                                    text = text,
                                    context = context
                                )
                            }
                        }

                        fun Context.findFragmentActivity(): FragmentActivity? {
                            var ctx = this
                            while (ctx is ContextWrapper) {
                                if (ctx is FragmentActivity) return ctx
                                ctx = ctx.baseContext
                            }
                            return null
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(intrinsicSize = IntrinsicSize.Min)
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            content = {
                                Column(
                                    modifier = Modifier
                                        .weight(weight = 1f)
                                        .clip(shape = RoundedCornerShape(size = containerCornerRadius))
                                        .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                            shape = RoundedCornerShape(size = containerCornerRadius)
                                        )
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    horizontalAlignment = Alignment.Start,
                                    verticalArrangement = Arrangement.Center,
                                    content = {
                                        Text(
                                            modifier = Modifier.fillMaxWidth(),
                                            maxLines = 1,
                                            fontFamily = PassMarkFonts.font,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = PassMarkFonts.Headline.medium,
                                            lineHeight = PassMarkFonts.Headline.medium,
                                            text = passwordData.data.title,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        passwordData.data.getSubTitle()?.let {
                                            Text(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .hideFromAccessibility(),
                                                maxLines = 1,
                                                fontFamily = PassMarkFonts.font,
                                                fontSize = PassMarkFonts.Body.medium,
                                                lineHeight = PassMarkFonts.Body.medium,
                                                fontWeight = FontWeight.Normal,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                text = it,
                                            )
                                        }
                                    }
                                )
                                Box(
                                    modifier = Modifier
                                        .setSizeLimitation()
                                        .fillMaxHeight()
                                        .aspectRatio(ratio = 1.0f)
                                        .clip(shape = CircleShape)
                                        .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                            shape = CircleShape
                                        )
                                        .clickable(
                                            enabled = !isLoading,
                                            onClick = dismissSheet
                                        )
                                        .setDescription(describable = HomeDescribable.PasswordOptionsBottomSheet.DISMISS),
                                    contentAlignment = Alignment.Center,
                                    content = {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardDoubleArrowDown,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                )
                            }
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(
                                space = 8.dp,
                                alignment = Alignment.CenterHorizontally
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            content = {
                                val commonModifier = Modifier.weight(weight = 1f)
                                BigCardButton(
                                    modifier = commonModifier.setDescription(describable = HomeDescribable.PasswordOptionsBottomSheet.COPY_PASSWORD),
                                    mainIcon = Icons.Default.Password,
                                    text = "Copy",
                                    actionIcon = Icons.Default.ContentCopy,
                                    enabled = !isLoading,
                                    onClick = {
                                        if (passwordData.data.useFingerPrint) {
                                            (context.findFragmentActivity())?.let { activity ->
                                                BiometricsHandler.performBiometricAuthentication(
                                                    context = context,
                                                    activity = activity,
                                                    subtitle = "Authenticate to copy password",
                                                    onComplete = {
                                                        if (it == BiometricsHandler.BiometricHandlerOutput.AUTHENTICATED) {
                                                            copy(text = passwordData.data.password)
                                                        } else {
                                                            it.handleToast(context = context)
                                                        }
                                                    },
                                                    onBiometricsNotPresent = {
                                                        setPromptState(
                                                            SecurityPromptState(
                                                                password = passwordData.data.password,
                                                                action = PasswordOptionChoices.COPY
                                                            )
                                                        )
                                                    }
                                                )
                                            }
                                        } else {
                                            copy(text = passwordData.data.password)
                                        }
                                        dismissSheet()
                                    },
                                    isLoading = false
                                )
                                BigCardButton(
                                    modifier = commonModifier.setDescription(describable = HomeDescribable.PasswordOptionsBottomSheet.EDIT_PASSWORD),
                                    mainIcon = Icons.Default.Edit,
                                    text = "Edit",
                                    actionIcon = Icons.AutoMirrored.Filled.ArrowRight,
                                    enabled = !isLoading,
                                    onClick = {
                                        if (passwordData.data.useFingerPrint) {
                                            (context.findFragmentActivity())?.let {
                                                BiometricsHandler.performBiometricAuthentication(
                                                    context = context,
                                                    activity = it,
                                                    subtitle = "Authenticate to edit password",
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
                                                                password = passwordData.data.password,
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
                                    isLoading = false
                                )
                                BigCardButton(
                                    modifier = commonModifier.setDescription(describable = HomeDescribable.PasswordOptionsBottomSheet.DELETE_PASSWORD),
                                    mainIcon = Icons.Default.Delete,
                                    text = "Delete",
                                    actionIcon = Icons.Default.Clear,
                                    useErrorColor = true,
                                    enabled = !isLoading,
                                    onClick = onDeleteClick,
                                    isLoading = isLoading
                                )
                            }
                        )
                        LazyVerticalGrid(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            columns = GridCells.Fixed(count = 2),
                            horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(space = 8.dp),
                            userScrollEnabled = false,
                            content = {
                                val commonModifier = Modifier.weight(weight = 1f)
                                passwordData.data.website?.let { website ->
                                    item {
                                        GridButton(
                                            modifier = commonModifier.setDescription(describable = HomeDescribable.PasswordOptionsBottomSheet.COPY_WEBSITE),
                                            text = "Website",
                                            onClick = { copy(text = website) },
                                            contentIcon = Icons.Default.Web,
                                            enabled = !isLoading
                                        )
                                    }
                                }
                                passwordData.data.email?.let { email ->
                                    item {
                                        GridButton(
                                            modifier = commonModifier.setDescription(describable = HomeDescribable.PasswordOptionsBottomSheet.COPY_EMAIL),
                                            text = "Email",
                                            onClick = { copy(text = email) },
                                            contentIcon = Icons.Default.Email,
                                            enabled = !isLoading
                                        )
                                    }
                                }
                                passwordData.data.userName?.let { userName ->
                                    item {
                                        GridButton(
                                            modifier = commonModifier.setDescription(describable = HomeDescribable.PasswordOptionsBottomSheet.COPY_USERNAME),
                                            text = "Username",
                                            onClick = { copy(text = userName) },
                                            contentIcon = Icons.Default.Person,
                                            enabled = !isLoading
                                        )
                                    }
                                }
                            }
                        )
                    }
                )
            }
        )
    }

    @Composable
    private fun GridButton(
        modifier: Modifier,
        text: String,
        contentIcon: ImageVector,
        enabled: Boolean,
        onClick: () -> Unit
    ) {
        Row(
            modifier = modifier
                .setSizeLimitation()
                .clip(shape = RoundedCornerShape(size = containerCornerRadius))
                .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    shape = RoundedCornerShape(size = containerCornerRadius)
                )
                .clickable(enabled = enabled, onClick = onClick)
                .padding(
                    start = 8.dp, end = 16.dp,
                    top = 4.dp, bottom = 4.dp
                ),
            horizontalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.Start
            ),
            verticalAlignment = Alignment.CenterVertically,
            content = {
                ConstraintLayout(
                    modifier = Modifier.setSizeLimitation(),
                    content = {
                        val (mainIconRef, copyIconRef) = createRefs()
                        Icon(
                            modifier = Modifier.constrainAs(
                                ref = mainIconRef,
                                constrainBlock = {
                                    top.linkTo(parent.top)
                                    bottom.linkTo(parent.bottom)
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                }
                            ),
                            imageVector = contentIcon,
                            contentDescription = null
                        )
                        Box(
                            modifier = Modifier
                                .size(size = 20.dp)
                                .clip(shape = CircleShape)
                                .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
                                .padding(all = 4.dp)
                                .constrainAs(
                                    ref = copyIconRef,
                                    constrainBlock = {
                                        top.linkTo(mainIconRef.bottom)
                                        bottom.linkTo(mainIconRef.bottom)
                                        start.linkTo(mainIconRef.end)
                                        end.linkTo(mainIconRef.end)
                                    }
                                ),
                            contentAlignment = Alignment.Center,
                            content = {
                                Icon(
                                    modifier = Modifier,
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = null
                                )

                            }
                        )
                    }
                )
                Text(
                    modifier = Modifier
                        .weight(weight = 1f)
                        .padding(vertical = 4.dp),
                    fontFamily = PassMarkFonts.font,
                    text = text,
                    fontSize = PassMarkFonts.Body.medium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        )
    }

    @Composable
    private fun BigCardButton(
        modifier: Modifier,
        mainIcon: ImageVector,
        text: String,
        actionIcon: ImageVector,
        useErrorColor: Boolean = false,
        enabled: Boolean,
        onClick: () -> Unit,
        isLoading: Boolean
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(space = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = {
                ConstraintLayout(
                    modifier = Modifier
                        .setSizeLimitation()
                        .widthIn(max = PassMarkDimensions.minTouchSize * 3)
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(shape = RoundedCornerShape(size = containerCornerRadius))
                        .background(
                            if (useErrorColor) MaterialTheme.colorScheme.errorContainer
                            else MaterialTheme.colorScheme.surfaceContainerHigh
                        )
                        .border(
                            width = 1.dp,
                            color =
                                if (useErrorColor) MaterialTheme.colorScheme.errorContainer
                                else MaterialTheme.colorScheme.surfaceContainerHighest,
                            shape = RoundedCornerShape(size = containerCornerRadius)
                        )
                        .clickable(
                            enabled = enabled,
                            onClick = onClick
                        ),
                    content = {
                        val (mainIconRef, secondaryIconRef) = createRefs()
                        Box(
                            modifier = Modifier
                                .fillMaxSize(fraction = 0.5f)
                                .constrainAs(
                                    ref = mainIconRef,
                                    constrainBlock = {
                                        this.top.linkTo(anchor = parent.top)
                                        this.bottom.linkTo(anchor = parent.bottom)
                                        this.start.linkTo(anchor = parent.start)
                                        this.end.linkTo(anchor = parent.end)
                                    }
                                ),
                            contentAlignment = Alignment.Center,
                            content = {
                                val tint =
                                    if (useErrorColor) MaterialTheme.colorScheme.onErrorContainer
                                    else MaterialTheme.colorScheme.onSurface
                                if (isLoading) {
                                    CustomLoader.ButtonLoader(
                                        modifier = Modifier.fillMaxSize(),
                                        color = tint,
                                        barWidth = 8.dp,
                                        spacing = 2.dp
                                    )
                                } else {
                                    Icon(
                                        modifier = Modifier.fillMaxSize(),
                                        imageVector = mainIcon,
                                        contentDescription = null,
                                        tint = tint
                                    )
                                }
                            }
                        )

                        Icon(
                            modifier = Modifier
                                .fillMaxSize(fraction = 0.25f)
                                .clip(shape = CircleShape)
                                .background(
                                    color = if (useErrorColor) MaterialTheme.colorScheme.errorContainer
                                    else MaterialTheme.colorScheme.surfaceContainerHigh
                                )
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
                            tint = if (useErrorColor) MaterialTheme.colorScheme.onErrorContainer
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
                Text(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    text = text,
                    fontFamily = PassMarkFonts.font,
                    fontSize = PassMarkFonts.Body.small,
                    lineHeight = PassMarkFonts.Body.small,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    minLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color =
                        if (useErrorColor) MaterialTheme.colorScheme.onErrorContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        )
    }
}

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
    val passwordData = PasswordData.testPasswordData.copy(
        data = PasswordData.testPasswordData.data.copy(
            useFingerPrint = false
        )
    )
    val deleteState = remember { mutableStateOf(ScreenState.PreCall<PasswordData>()) }
    HomePasswordOptionBottomSheet.PasswordOptionBottomSheet(
        passwordData = passwordData,
        sheetState = rememberModalBottomSheetState().apply { runBlocking { this@apply.show() } },
        dismissSheet = {},
        toPasswordEditScreen = {},
        setPromptState = {},
        onDeleteClick = {},
        deleteState = deleteState,
    )
}