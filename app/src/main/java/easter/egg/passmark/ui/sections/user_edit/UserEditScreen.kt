package easter.egg.passmark.ui.sections.user_edit

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import easter.egg.passmark.data.shared.PassMarkFonts
import easter.egg.passmark.data.shared.setSizeLimitation
import easter.egg.passmark.di.supabase.SupabaseModule
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.annotation.MobileHorizontalPreview
import easter.egg.passmark.utils.annotation.MobilePreview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// TODO: change name to master-key screen
object UserEditScreen {
    @Composable
    fun Screen(
        modifier: Modifier,
        viewModel: UserEditViewModel,
        isNewUser: Boolean,
        toLoaderScreen: () -> Unit
    ) {
        val scrollState = rememberScrollState()
        val isLoading = viewModel.screenState.collectAsState().value.isLoading
        val context = LocalContext.current
        LaunchedEffect(
            key1 = viewModel.screenState.collectAsState().value,
            block = {
                when (val state = viewModel.screenState.value) {
                    is ScreenState.PreCall, is ScreenState.Loading -> {}
                    is ScreenState.Loaded -> withContext(Dispatchers.Main) { toLoaderScreen() }
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
                val errorText: String? =
                    viewModel.masterPasswordText.collectAsState().value.length.let {
                        when {
                            it >= 32 -> "Password should be less than 32 in length"
                            it <= 8 -> "Password should be more than 8 in length"
                            else -> null
                        }
                    }
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    value = viewModel.masterPasswordText.collectAsState().value,
                    onValueChange = viewModel::updateMasterPasswordText,
                    label = { Text(text = "Master Password") },
                    placeholder = { Text(text = "Secure Password") },
                    isError = (viewModel.showError.collectAsState().value && (errorText != null)),
                    supportingText = { Text(text = errorText ?: "Password is of correct length") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Password,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        IconButton(
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
                    singleLine = true
                )
                val context = LocalContext.current.applicationContext
                Button(
                    modifier = Modifier
                        .setSizeLimitation()
                        .align(Alignment.End),
                    enabled = !isLoading,
                    onClick = {
                        if (errorText == null) {
                            viewModel.onButtonPress(
                                isNewUser = isNewUser,
                                context = context
                            )
                        } else {
                            viewModel.updateShowError()
                        }
                    },
                    content = {
                        Box(
                            modifier = Modifier,
                            contentAlignment = Alignment.Center,
                            content = {
                                Text(
                                    modifier = Modifier.alpha(alpha = if (isLoading) 0f else 1f),
                                    text = if (isNewUser) "Create" else "Confirm"
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
        val module = SupabaseModule()
        UserEditScreen.Screen(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            viewModel = UserEditViewModel(
                supabaseAccountHelper = module.providesSupabaseAccountHelper(supabaseClient = SupabaseModule.mockClient),
                userApi = module.provideUserApi(supabaseClient = SupabaseModule.mockClient)
            ),
            isNewUser = false,
            toLoaderScreen = {}
        )
        UserEditScreen.Screen(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            viewModel = UserEditViewModel(
                supabaseAccountHelper = module.providesSupabaseAccountHelper(supabaseClient = SupabaseModule.mockClient),
                userApi = module.provideUserApi(supabaseClient = SupabaseModule.mockClient)
            ),
            isNewUser = true,
            toLoaderScreen = {}
        )
    }
}