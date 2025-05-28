package easter.egg.passmark.ui.main.password_edit

import android.util.Patterns
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Web
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import easter.egg.passmark.data.models.Vault
import easter.egg.passmark.data.models.Vault.Companion.getIcon
import easter.egg.passmark.data.models.password.Password
import easter.egg.passmark.ui.main.MainViewModel
import easter.egg.passmark.ui.shared_components.CustomLoader
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.annotation.MobilePreview
import easter.egg.passmark.utils.extensions.customTopBarModifier
import easter.egg.passmark.utils.testing.TestTags
import easter.egg.passmark.utils.testing.TestTags.applyTag
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
        navigateBack: () -> Unit,
        passwordToEdit: Password?,
        defaultVaultId: Int?
    ) {
        LaunchedEffect(
            key1 = Unit,
            block = {
                val vaultList = (mainViewModel.screenState.value as? ScreenState.Loaded)
                    ?.result?.vaultListState?.value
                viewModel.loadInitialData(
                    password = passwordToEdit,
                    vault = vaultList?.find { v ->
                        v.id == (if (passwordToEdit == null) defaultVaultId else passwordToEdit.vaultId)
                    }
                )
            }
        )

        val context = LocalContext.current
        LaunchedEffect(
            key1 = viewModel.screenState.collectAsState().value,
            block = {
                when (val state = viewModel.screenState.value) {
                    is ScreenState.PreCall, is ScreenState.Loading -> {}
                    is ScreenState.Loaded -> {
                        val result =
                            (mainViewModel.screenState.value as? ScreenState.Loaded)?.result

                        passwordToEdit?.let {
                            if (passwordToEdit.localId != state.result.localId || passwordToEdit.cloudId != state.result.cloudId) {
                                result?.deletePassword(password = it)
                            }
                        }
                        result?.upsertPassword(password = state.result)
                        navigateBack()
                    }

                    is ScreenState.ApiError -> state.manageToastActions(context = context)
                }
            }
        )
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
        Scaffold(
            modifier = modifier,
            topBar = {
                EditTopBar(
                    modifier = Modifier.customTopBarModifier(),
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
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterHorizontally
            ),
            content = {
                Box(
                    modifier = Modifier
                        .applyTag(testTag = TestTags.EditPassword.DISMISS.name)
                        .size(size = PassMarkDimensions.minTouchSize)
                        .clip(shape = CircleShape)
                        .clickable(
                            enabled = !passwordEditViewModel.screenState.collectAsState().value.isLoading,
                            onClick = navigateBack
                        )
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
                        .applyTag(testTag = TestTags.EditPassword.SELECT_VAULT_BUTTON.name)
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
                            val passwordList =
                                (mainViewModel.screenState.collectAsState().value as? ScreenState.Loaded)
                                    ?.result?.passwordListState?.collectAsState()?.value
                            VaultSelectionBottomSheet(
                                dismissDropDown = {
                                    coroutineScope
                                        .launch { sheetState.hide() }
                                        .invokeOnCompletion { sheetIsVisible.value = false }
                                },
                                vaultList = (mainViewModel.screenState.collectAsState().value as? ScreenState.Loaded)
                                    ?.result?.vaultListState?.collectAsState()?.value
                                    ?: listOf(),
                                passwordList = passwordList ?: listOf(),
                                sheetState = sheetState,
                                passwordEditViewModel = passwordEditViewModel
                            )
                        }
                    }
                )

                val isLoading = passwordEditViewModel.screenState.collectAsState().value.isLoading
                Box(
                    modifier = Modifier
                        .applyTag(testTag = TestTags.EditPassword.SAVE_BUTTON.name)
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
                            CustomLoader.ButtonLoader(
                                modifier = Modifier,
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
        passwordList: List<Password>,
        sheetState: SheetState,
        passwordEditViewModel: PasswordEditViewModel
    ) {
        @Composable
        fun Selectable(
            modifier: Modifier,
            vault: Vault?
        ) {
            val passwordCount = passwordList.filter { it.vaultId == vault?.id }.size
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
                    .clickable(
                        onClick = {
                            passwordEditViewModel.updateSelectedVault(vault = vault)
                            dismissDropDown()
                        }
                    )
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
                        text = "$passwordCount passwords",
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
                    modifier = Modifier
                        .applyTag(testTag = TestTags.EditPassword.SELECT_VAULT_DIALOG_CHOOSE_VAULT.name)
                        .fillMaxWidth(),
                    text = "Choose Vault",
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                    fontFamily = PassMarkFonts.font,
                    fontSize = PassMarkFonts.Headline.medium,
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
                        val tempVaultList = vaultList
                            .toMutableList<Vault?>()
                            .also { it.add(index = 0, element = null) }
                        items(
                            items = tempVaultList,
                            itemContent = {
                                Selectable(
                                    modifier = Modifier
                                        .applyTag(
                                            testTag = TestTags.EditPassword.ChooseVault
                                                .getVaultTestTag(
                                                    vaultName =
                                                        it?.name ?: Vault.VAULT_NAME_FOR_ALL_ITEMS
                                                )
                                        )
                                        .fillMaxWidth(),
                                    vault = it
                                )
                                if (it != tempVaultList.last()) {
                                    HorizontalDivider(
                                        modifier = Modifier.fillMaxWidth(),
                                        thickness = 1.dp,
                                    )
                                }
                            }
                        )
                    }
                )

                if (vaultList.isEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 16.dp
                            )
                            .clip(shape = RoundedCornerShape(size = 16.dp))
                            .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                shape = RoundedCornerShape(size = 16.dp)
                            )
                            .padding(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            ),
                        horizontalArrangement = Arrangement.spacedBy(
                            space = 12.dp,
                            alignment = Alignment.CenterHorizontally
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        content = {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null
                            )
                            Text(
                                modifier = Modifier.weight(weight = 1f),
                                text = "No custom Vaults detected. Create a new Vault to add passwords to.",
                                fontFamily = PassMarkFonts.font,
                                fontSize = PassMarkFonts.Label.medium,
                                lineHeight = PassMarkFonts.Label.medium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    )
                }
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
                .verticalScroll(state = scrollState)
                .padding(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(space = 16.dp),
            content = {
                AnimatedVisibility(
                    modifier = Modifier.fillMaxWidth(),
                    visible = (!passwordRequirementsMet && viewModel.showFieldError.collectAsState().value),
                    content = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            content = {
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
                            }
                        )
                    }
                )
                val textFieldModifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 72.dp)
                    .padding(horizontal = 8.dp)
                //-----------------------------------------------------------------------------title
                DefaultCard(
                    modifier = Modifier
                        .fillMaxWidth(),
                    content = {
                        CustomTextField(
                            modifier = textFieldModifier,
                            leadingIcon = null,
                            label = "Title",
                            placeHolder = "Untitled",
                            text = viewModel.title.collectAsState().value,
                            onTextChange = { viewModel.title.value = it },
                            textStyle = LocalTextStyle.current.copy(fontSize = PassMarkFonts.Title.large),
                            isEnabled = !isLoading,
                            inputOption = InputOption.TITLE,
                            testTag = TestTags.EditPassword.TITLE_TEXT_FIELD.name
                        )
                    }
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height = 0.dp)
                )
                DefaultCard(
                    modifier = Modifier.fillMaxWidth(),
                    content = {
                        //---------------------------------------------------------------------email
                        CustomTextField(
                            modifier = textFieldModifier,
                            leadingIcon = Icons.Outlined.Email,
                            label = "Email",
                            placeHolder = "abc@def.xyz",
                            text = viewModel.email.collectAsState().value,
                            onTextChange = { viewModel.email.value = it },
                            isEnabled = !isLoading,
                            inputOption = InputOption.EMAIL,
                            testTag = TestTags.EditPassword.EMAIL_TEXT_FIELD.name
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
                            isEnabled = !isLoading,
                            inputOption = InputOption.USERNAME,
                            testTag = TestTags.EditPassword.USER_NAME_TEXT_FIELD.name
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
                            isEnabled = !isLoading,
                            inputOption = InputOption.PASSWORD,
                            testTag = TestTags.EditPassword.PASSWORD_TEXT_FIELD.name
                        )
                    }
                )
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
                            isEnabled = !isLoading,
                            inputOption = InputOption.WEBSITE,
                            testTag = TestTags.EditPassword.WEBSITE_TEXT_FIELD.name
                        )
                    }
                )
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
                            inputOption = InputOption.NOTES,
                            singleLine = false,
                            testTag = TestTags.EditPassword.NOTES_TEXT_FIELD.name
                        )
                    }
                )
                //-------------------------------------------------------------------use-fingerprint
                CustomSwitch(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Use fingerprint to access",
                    isChecked = viewModel.useFingerPrint.collectAsState().value,
                    onCheckedChange = { viewModel.useFingerPrint.value = it },
                    isEnabled = !isLoading,
                    testTag = TestTags.EditPassword.USE_FINGERPRINT_SWITCH.name
                )
                //--------------------------------------------------------------------on-device-only
                CustomSwitch(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Keep On Device Only",
                    isChecked = viewModel.saveToLocalOnly.collectAsState().value,
                    onCheckedChange = { viewModel.saveToLocalOnly.value = it },
                    isEnabled = !isLoading,
                    testTag = TestTags.EditPassword.KEEP_LOCAL_SWITCH.name
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
        content: @Composable ColumnScope.() -> Unit,
        onClick: (() -> Unit)? = null,
        isSelected: Boolean = false
    ) {
        val shape = RoundedCornerShape(size = 12.dp)
        Column(
            modifier = modifier
                .clip(shape)
                .clickable(
                    enabled = onClick != null,
                    onClick = onClick ?: {}
                )
                .background(
                    color =
                        if (isSelected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceContainer
                )
                .border(
                    width = 1.dp,
                    color =
                        if (isSelected) MaterialTheme.colorScheme.inversePrimary
                        else MaterialTheme.colorScheme.surfaceContainerHighest,
                    shape = shape
                ),
            horizontalAlignment = Alignment.Start,
            content = content
        )
    }

    private enum class InputOption { TITLE, EMAIL, USERNAME, PASSWORD, WEBSITE, NOTES }

    @Composable
    private fun CustomTextField(
        modifier: Modifier,
        leadingIcon: ImageVector?,
        label: String,
        placeHolder: String,
        text: String,
        onTextChange: (String) -> Unit,
        textStyle: TextStyle = LocalTextStyle.current,
        isEnabled: Boolean,
        inputOption: InputOption,
        singleLine: Boolean = true,
        testTag: String
    ) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
            content = {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .applyTag(testTag = testTag),
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = (inputOption !in listOf(
                            InputOption.USERNAME,
                            InputOption.PASSWORD
                        )),
                        keyboardType = when (inputOption) {
                            InputOption.EMAIL -> KeyboardType.Email
                            InputOption.PASSWORD -> KeyboardType.Text // TODO: use this after testing: [KeyboardType.Password]
                            InputOption.WEBSITE -> KeyboardType.Uri
                            InputOption.USERNAME, InputOption.TITLE, InputOption.NOTES -> KeyboardType.Text
                        },
                    ),
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
                    singleLine = singleLine,
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
        isEnabled: Boolean,
        testTag: String? = null
    ) {
        DefaultCard(
            modifier = modifier,
            onClick = {
                onCheckedChange(!isChecked)
            },
            isSelected = isChecked,
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
                            lineHeight = PassMarkFonts.Body.medium,
                            fontFamily = PassMarkFonts.font,
                            fontWeight = FontWeight.Medium,
                            color =
                                if (isChecked) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onSurface,
                            text = text
                        )
                        Switch(
                            modifier = if (testTag == null) Modifier else Modifier.applyTag(testTag = testTag),
                            enabled = isEnabled,
                            checked = isChecked,
                            onCheckedChange = onCheckedChange
                        )
                    }
                )
            }
        )
    }
}

@Composable
@MobilePreview
private fun PasswordEditScreenPreview() {
    PasswordEditScreen.Screen(
        modifier = Modifier.fillMaxSize(),
        viewModel = PasswordEditViewModel.getTestViewModel(),
        navigateBack = {},
        mainViewModel = MainViewModel.getTestViewModel(),
        passwordToEdit = null,
        defaultVaultId = null
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@MobilePreview
private fun VaultSelectionBottomSheetPreview() {
    PasswordEditScreen.VaultSelectionBottomSheet(
        dismissDropDown = {},
        vaultList = listOf(
            Vault(id = 1, name = "Banking", iconChoice = 5),
            Vault(id = 3, name = "Shopping", iconChoice = 8),
            Vault(id = 4, name = "OTT", iconChoice = 11),
        ),
        passwordList = listOf(),
        sheetState = rememberModalBottomSheetState().apply { runBlocking { this@apply.show() } },
        passwordEditViewModel = PasswordEditViewModel.getTestViewModel()
    )
}