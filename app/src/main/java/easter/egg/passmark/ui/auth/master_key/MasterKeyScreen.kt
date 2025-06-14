package easter.egg.passmark.ui.auth.master_key

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import easter.egg.passmark.data.supabase.account.SupabaseAccountHelper
import easter.egg.passmark.data.supabase.api.UserApi
import easter.egg.passmark.di.supabase.SupabaseModule
import easter.egg.passmark.ui.auth.AuthViewModel
import easter.egg.passmark.ui.shared_components.CustomLoader
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.annotation.MobileHorizontalPreview
import easter.egg.passmark.utils.annotation.MobilePreview
import easter.egg.passmark.utils.testing.PassMarkConfig
import easter.egg.passmark.utils.testing.TestTags
import easter.egg.passmark.utils.testing.TestTags.applyTag
import easter.egg.passmark.utils.values.PassMarkFonts
import easter.egg.passmark.utils.values.setSizeLimitation

object MasterKeyScreen {
    private val TAG = this::class.simpleName

    @Composable
    fun Screen(
        modifier: Modifier,
        viewModel: MasterKeyViewModel,
        isNewUser: Boolean,
        toLoaderScreen: () -> Unit,
        authViewModel: AuthViewModel
    ) {
        val scrollState = rememberScrollState()
        val screenState = viewModel.screenState.collectAsState()
        val isLoading = screenState.value.isLoading
        val context = LocalContext.current
        LaunchedEffect(
            key1 = screenState.value,
            block = {
                when (val state = viewModel.screenState.value) {
                    is ScreenState.PreCall, is ScreenState.Loading -> {}
                    is ScreenState.Loaded -> {
                        authViewModel.updatePasswordUsed()
                        toLoaderScreen()
                    }

                    is ScreenState.ApiError -> state.manageToastActions(context = context)
                }
            }
        )

        Column(
            modifier = modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(state = scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterVertically
            ),
            content = {
                val spacerModifier = Modifier
                    .fillMaxWidth()
                    .height(height = 8.dp)
                Spacer(modifier = spacerModifier)
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text =
                        if (isNewUser) "Create a Master Key"
                        else "Enter your master key",
                    fontFamily = PassMarkFonts.font,
                    fontSize = PassMarkFonts.Headline.medium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text =
                        if (isNewUser) "Make sure to remember your master key. Without this, account recovery would be impossible."
                        else "Enter the master key you used to create your account.",
                    fontFamily = PassMarkFonts.font,
                    fontSize = PassMarkFonts.Body.medium,
                    lineHeight = PassMarkFonts.Body.medium,
                    fontWeight = FontWeight.Medium
                )
                val passwordTextState: PasswordTextState = PasswordTextState.getEState(
                    password = viewModel.masterPasswordText.collectAsState().value
                )
                OutlinedTextField(
                    modifier = Modifier
                        .applyTag(testTag = TestTags.CreateMasterKey.TEXT_FIELD.name)
                        .fillMaxWidth(),
                    enabled = !isLoading,
                    value = viewModel.masterPasswordText.collectAsState().value,
                    onValueChange = viewModel::updateMasterPasswordText,
                    label = { Text(text = "Master Password") },
                    placeholder = { Text(text = "Secure Password") },
                    isError = (viewModel.showError.collectAsState().value && (passwordTextState != PasswordTextState.OK_LENGTH)),
                    supportingText = { Text(text = passwordTextState.getMessage()) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Password,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            modifier = Modifier.applyTag(testTag = TestTags.CreateMasterKey.VISIBILITY_BUTTON.name),
                            onClick = viewModel::switchVisibility,
                            content = {
                                Icon(
                                    imageVector = viewModel.visible.collectAsState().value.let {
                                        if (it) Icons.Default.Visibility else Icons.Default.VisibilityOff
                                    },
                                    contentDescription = null
                                )
                            }
                        )
                    },
                    visualTransformation =
                        if (viewModel.visible.collectAsState().value) VisualTransformation.None
                        else PasswordVisualTransformation(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = false,
                        keyboardType = PassMarkConfig.getKeyboardTypeForPasswords()
                    )
                )
                val applicationContext = LocalContext.current.applicationContext

                Box(
                    modifier = Modifier
                        .applyTag(testTag = TestTags.CreateMasterKey.CONFIRM_BUTTON.name)
                        .setSizeLimitation()
                        .clip(shape = RoundedCornerShape(size = 16.dp))
                        .background(color = MaterialTheme.colorScheme.primary)
                        .clickable(
                            enabled = !isLoading,
                            onClick = {
                                if (passwordTextState == PasswordTextState.OK_LENGTH) {
                                    viewModel.onButtonPress(
                                        isNewUser = isNewUser,
                                        context = applicationContext
                                    )
                                } else {
                                    viewModel.updateShowError()
                                }
                            }
                        )
                        .padding(horizontal = 16.dp)
                        .align(Alignment.End),
                    contentAlignment = Alignment.Center,
                    content = {
                        Text(
                            modifier = Modifier.alpha(alpha = if (isLoading) 0f else 1f),
                            color = MaterialTheme.colorScheme.onPrimary,
                            text = if (isNewUser) "Create" else "Confirm",
                            fontFamily = PassMarkFonts.font,
                            fontSize = PassMarkFonts.Body.medium,
                            fontWeight = FontWeight.Medium
                        )
                        if (isLoading) {
                            CustomLoader.ButtonLoader(
                                modifier = Modifier.size(size = 24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                )
                Spacer(modifier = spacerModifier)
            }
        )
    }
}

@Composable
@MobilePreview
@MobileHorizontalPreview
private fun UserEditScreenPreview() {
    Column {
        MasterKeyScreen.Screen(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            viewModel = MasterKeyViewModel(
                supabaseAccountHelper = SupabaseAccountHelper(supabaseClient = SupabaseModule.mockClient),
                userApi = UserApi(supabaseClient = SupabaseModule.mockClient)
            ),
            isNewUser = false,
            toLoaderScreen = {},
            authViewModel = AuthViewModel()
        )
        MasterKeyScreen.Screen(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            viewModel = MasterKeyViewModel(
                supabaseAccountHelper = SupabaseAccountHelper(supabaseClient = SupabaseModule.mockClient),
                userApi = UserApi(supabaseClient = SupabaseModule.mockClient)
            ),
            isNewUser = true,
            toLoaderScreen = {},
            authViewModel = AuthViewModel()
        )
    }
}