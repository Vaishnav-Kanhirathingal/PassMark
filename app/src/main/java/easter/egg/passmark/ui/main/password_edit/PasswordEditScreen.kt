package easter.egg.passmark.ui.main.password_edit

import android.util.Patterns
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Web
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import easter.egg.passmark.data.models.content.Vault
import easter.egg.passmark.data.models.content.Vault.Companion.getIcon
import easter.egg.passmark.data.supabase.api.PasswordApi
import easter.egg.passmark.di.supabase.SupabaseModule
import easter.egg.passmark.ui.main.MainViewModel
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.annotation.MobilePreview
import easter.egg.passmark.utils.values.PassMarkDimensions
import easter.egg.passmark.utils.values.PassMarkFonts
import easter.egg.passmark.utils.values.setSizeLimitation
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object PasswordEditScreen {
    private val TAG = this::class.simpleName

    @Composable
    fun Screen(
        modifier: Modifier,
        viewModel: PasswordEditViewModel,
        mainViewModel: MainViewModel,
        navigateBack: () -> Unit
    ) {
        val barModifier = Modifier
            .fillMaxWidth()
            .heightIn(min = PassMarkDimensions.minTouchSize)
        val passwordRequirementsMet = combine(
            viewModel.title,
            viewModel.password,
            viewModel.email,
            transform = { title, password, email ->
                title.isNotEmpty() &&
                        password.isNotEmpty() &&
                        email.let { it.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(it).matches() }
            }
        ).collectAsState(initial = false)
        val context = LocalContext.current
        LaunchedEffect(
            key1 = viewModel.screenState.collectAsState().value,
            block = {
                when (val state = viewModel.screenState.value) {
                    is ScreenState.PreCall, is ScreenState.Loading -> {}
                    is ScreenState.Loaded -> {
                        // TODO: save to [MainViewModel]'s home list
                        navigateBack()
                    }

                    is ScreenState.ApiError -> {
                        if (!state.errorHasBeenDisplayed) {
                            Toast.makeText(
                                context,
                                state.generalToastMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                            state.setErrorHasBeenDisplayed()
                        }
                    }
                }
            }
        )
        Scaffold(
            modifier = modifier,
            topBar = {
                EditTopBar(
                    modifier = barModifier,
                    navigateBack = navigateBack,
                    passwordEditViewModel = viewModel,
                    passwordRequirementsMet = passwordRequirementsMet.value,
                    mainViewModel = mainViewModel
                )
            },
            content = {
                EditContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = it),
                    viewModel = viewModel,
                    passwordRequirementsMet = passwordRequirementsMet.value
                )
            },
            bottomBar = { EditBottomBar(modifier = barModifier) }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun EditTopBar(
        modifier: Modifier,
        navigateBack: () -> Unit,
        passwordEditViewModel: PasswordEditViewModel,
        passwordRequirementsMet: Boolean,
        mainViewModel: MainViewModel
    ) {
        Row(
            modifier = modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterHorizontally
            ),
            content = {
                Box(
                    modifier = Modifier
                        .size(size = PassMarkDimensions.minTouchSize)
                        .clickable(
                            enabled = !passwordEditViewModel.screenState.collectAsState().value.isLoading,
                            onClick = navigateBack
                        )
                        .clip(shape = CircleShape)
                        .background(color = MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                    content = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                )
                Spacer(
                    modifier = Modifier
                        .weight(weight = 1f)
                        .height(height = PassMarkDimensions.minTouchSize)
                )
                val pillShape = RoundedCornerShape(size = PassMarkDimensions.minTouchSize)

                val sheetIsVisible = remember { mutableStateOf(false) }
                val sheetState = rememberModalBottomSheetState()

                val coroutineScope = rememberCoroutineScope()
                Row(
                    modifier = Modifier
                        .setSizeLimitation()
                        .clip(shape = pillShape)
                        .background(color = MaterialTheme.colorScheme.primaryContainer)
                        .clickable(
                            enabled = !passwordEditViewModel.screenState.collectAsState().value.isLoading,
                            onClick = {
                                sheetIsVisible.value = true
                                coroutineScope.launch { sheetState.show() }
                            }
                        )
                        .padding(
                            start = 16.dp,
                            end = 16.dp
                        ),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 4.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    content = {
                        Icon(
                            imageVector = passwordEditViewModel.selectedVault.collectAsState().value.getIcon(),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .wrapContentWidth()
                                .widthIn(
                                    min = PassMarkDimensions.minTouchSize,
                                    max = PassMarkDimensions.minTouchSize * 2
                                ),
                            text = passwordEditViewModel.selectedVault.collectAsState().value?.name
                                ?: Vault.VAULT_NAME_FOR_ALL_ITEMS,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = PassMarkFonts.Body.medium,
                            fontWeight = FontWeight.Medium,
                            fontFamily = PassMarkFonts.font,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        if (sheetIsVisible.value) {
                            VaultSelectionBottomSheet(
                                dismissDropDown = {
                                    coroutineScope
                                        .launch { sheetState.hide() }
                                        .invokeOnCompletion { sheetIsVisible.value = false }
                                },
                                vaultList = (mainViewModel.screenState.collectAsState().value as? ScreenState.Loaded)
                                    ?.result?.vaultListState?.collectAsState()?.value
                                    ?: listOf(),
                                sheetState = sheetState,
                                mainViewModel = mainViewModel,
                                passwordEditViewModel = passwordEditViewModel
                            )
                        }
                    }
                )

                val isLoading = passwordEditViewModel.screenState.collectAsState().value.isLoading
                Box(
                    modifier = Modifier
                        .setSizeLimitation()
                        .clip(shape = pillShape)
                        .background(color = MaterialTheme.colorScheme.primary)
                        .clickable(
                            enabled = !isLoading,
                            onClick = {
                                if (passwordRequirementsMet) {
                                    passwordEditViewModel.savePassword(
                                        passwordCryptographyHandler = mainViewModel.passwordCryptographyHandler
                                    )
                                } else {
                                    passwordEditViewModel.updateShowFieldError()
                                }
                            }
                        )
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.Center,
                    content = {
                        Text(
                            modifier = Modifier.alpha(alpha = if (isLoading) 0f else 1f),
                            text = "Save",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = PassMarkFonts.Body.medium,
                            fontWeight = FontWeight.Medium,
                            fontFamily = PassMarkFonts.font,
                        )
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(size = 24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                )
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun VaultSelectionBottomSheet(
        dismissDropDown: () -> Unit,
        vaultList: List<Vault>,
        sheetState: SheetState,
        mainViewModel: MainViewModel,
        passwordEditViewModel: PasswordEditViewModel
    ) {
        @Composable
        fun Selectable(
            modifier: Modifier,
            vault: Vault?
        ) {
            val passwordCount =
                (mainViewModel.screenState.collectAsState().value as? ScreenState.Loaded)
                    ?.result?.passwordListState?.collectAsState()?.value
                    ?.filter { it.vaultId == vault?.id }?.size
                    ?: 0
            val isSelected =
                vault?.id == passwordEditViewModel.selectedVault.collectAsState().value?.id
            ConstraintLayout(
                modifier = modifier
                    .background(
                        color =
                            if (isSelected) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                    .setSizeLimitation()
                    .clickable(onClick = { passwordEditViewModel.selectedVault.value = vault })
                    .padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ),
                content = {
                    val (name, subtitle, vaultIcon) = createRefs()
                    val tint =
                        if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    Icon(
                        modifier = Modifier.constrainAs(
                            ref = vaultIcon,
                            constrainBlock = {
                                this.start.linkTo(parent.start)
                                this.top.linkTo(parent.top)
                                this.bottom.linkTo(parent.bottom)
                            }
                        ),
                        imageVector = vault.getIcon(),
                        contentDescription = null,
                        tint = tint
                    )
                    createVerticalChain(
                        name, subtitle,
                        chainStyle = ChainStyle.Packed
                    )
                    Text(
                        modifier = Modifier.constrainAs(
                            ref = name,
                            constrainBlock = {
                                this.top.linkTo(parent.top)
                                this.start.linkTo(
                                    anchor = vaultIcon.end,
                                    margin = 16.dp
                                )
                                this.bottom.linkTo(subtitle.top)
                                this.end.linkTo(parent.end)
                                this.width = Dimension.fillToConstraints
                            }
                        ),
                        text = vault?.name ?: Vault.VAULT_NAME_FOR_ALL_ITEMS,
                        maxLines = 1,
                        fontFamily = PassMarkFonts.font,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = PassMarkFonts.Title.medium,
                        fontSize = PassMarkFonts.Title.medium,
                        color = tint
                    )
                    Text(
                        modifier = Modifier.constrainAs(
                            ref = subtitle,
                            constrainBlock = {
                                this.top.linkTo(name.bottom)
                                this.start.linkTo(
                                    anchor = vaultIcon.end,
                                    margin = 16.dp
                                )
                                this.bottom.linkTo(parent.bottom)
                                this.end.linkTo(parent.end)
                                this.width = Dimension.fillToConstraints
                            }
                        ),
                        text = "$passwordCount passwords", // TODO:
                        maxLines = 1,
                        fontFamily = PassMarkFonts.font,
                        lineHeight = PassMarkFonts.Label.medium,
                        fontSize = PassMarkFonts.Label.medium,
                        fontWeight = FontWeight.Medium,
                        color = tint
                    )
                }
            )
        }
        ModalBottomSheet(
            onDismissRequest = dismissDropDown,
            sheetState = sheetState,
            content = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Choose a Vault for storing current password",
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                    fontFamily = PassMarkFonts.font,
                    fontSize = PassMarkFonts.Title.medium,
                    fontWeight = FontWeight.SemiBold
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .clip(shape = RoundedCornerShape(size = 16.dp)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    content = {
                        items(
                            items = vaultList.toMutableList<Vault?>().apply {
                                this.add(index = 0, element = null)
                            },
                            itemContent = {
                                Selectable(
                                    modifier = Modifier.fillMaxWidth(),
                                    vault = it
                                )
                                if (it != vaultList.last()) {
                                    HorizontalDivider(
                                        modifier = Modifier.fillMaxWidth(),
                                        thickness = 1.dp,
                                    )
                                }
                            }
                        )
                    }
                )
            }
        )
    }

    @Composable
    private fun EditContent(
        modifier: Modifier,
        viewModel: PasswordEditViewModel,
        passwordRequirementsMet: Boolean
    ) {
        val isLoading = viewModel.screenState.collectAsState().value.isLoading
        val scrollState = rememberScrollState()
        Column(
            modifier = modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(state = scrollState),
            content = {
                val smallSpacerModifier = Modifier
                    .fillMaxWidth()
                    .height(height = 16.dp)
                val largeSpacerModifier = Modifier
                    .fillMaxWidth()
                    .height(height = 32.dp)
                AnimatedVisibility(
                    modifier = Modifier.fillMaxWidth(),
                    visible = (!passwordRequirementsMet && viewModel.showFieldError.collectAsState().value),
                    content = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            content = {
                                Spacer(modifier = smallSpacerModifier)
                                DefaultCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    content = {
                                        Text(
                                            modifier = Modifier.padding(
                                                horizontal = 24.dp,
                                                vertical = 16.dp
                                            ),
                                            text = "Make sure all the requirements are met:\n" +
                                                    "- Title cannot be empty.\n" +
                                                    "- Email can either be empty or of correct format.\n" +
                                                    "- Password cannot be empty.",
                                            fontFamily = PassMarkFonts.font,
                                            fontSize = PassMarkFonts.Body.medium,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                )
                                Spacer(modifier = smallSpacerModifier)
                            }
                        )
                    }
                )
                Spacer(modifier = smallSpacerModifier)
                val textFieldModifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 72.dp)
                    .padding(horizontal = 8.dp)
                //-----------------------------------------------------------------------------title
                DefaultCard(
                    modifier = Modifier.fillMaxWidth(),
                    content = {
                        CustomTextField(
                            modifier = textFieldModifier,
                            leadingIcon = null,
                            label = "Title",
                            placeHolder = "Untitled",
                            text = viewModel.title.collectAsState().value,
                            onTextChange = { viewModel.title.value = it },
                            textStyle = LocalTextStyle.current.copy(fontSize = PassMarkFonts.Title.large),
                            isEnabled = !isLoading
                        )
                    }
                )
                Spacer(modifier = largeSpacerModifier)
                DefaultCard(
                    modifier = Modifier.fillMaxWidth(),
                    content = {
                        CustomTextField(
                            modifier = textFieldModifier,
                            leadingIcon = Icons.Outlined.Email,
                            label = "Email",
                            placeHolder = "abc@def.xyz",
                            text = viewModel.email.collectAsState().value,
                            onTextChange = { viewModel.email.value = it },
                            isEnabled = !isLoading
                        )
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.surfaceContainerHighest
                        )
                        //-----------------------------------------------------------------user-name
                        CustomTextField(
                            modifier = textFieldModifier,
                            leadingIcon = Icons.Outlined.Person,
                            label = "UserName",
                            placeHolder = "John Doe",
                            text = viewModel.userName.collectAsState().value,
                            onTextChange = { viewModel.userName.value = it },
                            isEnabled = !isLoading
                        )
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.surfaceContainerHighest
                        )
                        //------------------------------------------------------------------password
                        CustomTextField(
                            modifier = textFieldModifier,
                            leadingIcon = Icons.Default.Password,
                            label = "Password",
                            placeHolder = "",
                            text = viewModel.password.collectAsState().value,
                            onTextChange = { viewModel.password.value = it },
                            isEnabled = !isLoading
                        )
                    }
                )
                Spacer(modifier = smallSpacerModifier)
                //---------------------------------------------------------------------------website
                DefaultCard(
                    modifier = Modifier.fillMaxWidth(),
                    content = {
                        CustomTextField(
                            modifier = textFieldModifier,
                            leadingIcon = Icons.Default.Web,
                            label = "Website",
                            placeHolder = "www.abc.com",
                            text = viewModel.website.collectAsState().value,
                            onTextChange = { viewModel.website.value = it },
                            isEnabled = !isLoading
                        )
                    }
                )
                Spacer(modifier = smallSpacerModifier)
                //------------------------------------------------------------------------------note
                DefaultCard(
                    modifier = Modifier.fillMaxWidth(),
                    content = {
                        CustomTextField(
                            modifier = textFieldModifier,
                            leadingIcon = Icons.Default.EditNote,
                            label = "notes",
                            placeHolder = "Add a note",
                            text = viewModel.notes.collectAsState().value,
                            onTextChange = { viewModel.notes.value = it },
                            isEnabled = !isLoading,
                        )
                    }
                )
                Spacer(modifier = smallSpacerModifier)
                //-------------------------------------------------------------------use-fingerprint
                CustomSwitch(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Use fingerprint to access",
                    isChecked = viewModel.useFingerPrint.collectAsState().value,
                    onCheckedChange = { viewModel.useFingerPrint.value = it },
                    isEnabled = !isLoading
                )
                Spacer(modifier = smallSpacerModifier)
                //--------------------------------------------------------------------on-device-only
                CustomSwitch(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Keep On Device Only",
                    isChecked = viewModel.saveToLocalOnly.collectAsState().value,
                    onCheckedChange = { viewModel.saveToLocalOnly.value = it },
                    isEnabled = !isLoading
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height = PassMarkDimensions.minTouchSize * 2)
                )
            }
        )
    }

    @Composable
    fun DefaultCard(
        modifier: Modifier,
        content: @Composable ColumnScope.() -> Unit
    ) {
        val shape = RoundedCornerShape(size = 12.dp)
        Column(
            modifier = modifier
                .clip(shape)
                .background(color = MaterialTheme.colorScheme.surfaceContainer)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    shape = shape
                ),
            content = content
        )
    }

    @Composable
    fun CustomTextField(
        modifier: Modifier,
        leadingIcon: ImageVector?,
        label: String,
        placeHolder: String,
        text: String,
        onTextChange: (String) -> Unit,
        textStyle: TextStyle = LocalTextStyle.current,
        isEnabled: Boolean
    ) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
            content = {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isEnabled,
                    label = { Text(text = label) },
                    placeholder = { Text(text = placeHolder) },
                    leadingIcon = leadingIcon?.let {
                        {
                            Icon(
                                imageVector = it,
                                contentDescription = null
                            )
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent
                    ),
                    value = text,
                    onValueChange = onTextChange,
                    textStyle = textStyle,
                    trailingIcon = text.takeUnless { it.isEmpty() }?.let {
                        {
                            IconButton(
                                onClick = { onTextChange("") },
                                content = {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                )
            }
        )
    }

    @Composable
    fun CustomSwitch(
        modifier: Modifier,
        text: String,
        isChecked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        isEnabled: Boolean
    ) {
        DefaultCard(
            modifier = modifier.clickable(
                enabled = isEnabled,
                onClick = { onCheckedChange(!isChecked) }
            ),
            content = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    content = {
                        Text(
                            modifier = Modifier.weight(1f),
                            fontSize = PassMarkFonts.Body.medium,
                            fontFamily = PassMarkFonts.font,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            text = text
                        )
                        Switch(
                            enabled = isEnabled,
                            checked = isChecked,
                            onCheckedChange = onCheckedChange
                        )
                    }
                )
            }
        )
    }

    @Composable
    private fun EditBottomBar(
        modifier: Modifier
    ) {
        // TODO: pending
    }
}

@Composable
@MobilePreview
//@MobileHorizontalPreview
private fun PasswordEditScreenPreview() {
    PasswordEditScreen.Screen(
        modifier = Modifier.fillMaxSize(),
        viewModel = PasswordEditViewModel(
            passwordApi = PasswordApi(
                supabaseClient = SupabaseModule.mockClient
            )
        ),
        navigateBack = {},
        mainViewModel = MainViewModel.getTestViewModel()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@MobilePreview
private fun VaultSelectionBottomSheetPreview() {
    PasswordEditScreen.VaultSelectionBottomSheet(
        dismissDropDown = {},
        vaultList = listOf(
            Vault(name = "Banking", iconChoice = 5),
            Vault(name = "Websites", iconChoice = 3),
            Vault(name = "Shopping", iconChoice = 8),
            Vault(name = "OTT", iconChoice = 11),
        ),
        sheetState = rememberModalBottomSheetState().apply { runBlocking { this@apply.show() } },
        mainViewModel = MainViewModel.getTestViewModel(),
        passwordEditViewModel = PasswordEditViewModel(passwordApi = PasswordApi(SupabaseModule.mockClient))
    )
}