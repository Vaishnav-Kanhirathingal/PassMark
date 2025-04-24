package easter.egg.passmark.ui.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import easter.egg.passmark.data.models.content.Password
import easter.egg.passmark.ui.main.home.HomeViewModel
import easter.egg.passmark.ui.main.home.screens.HomeScreen
import easter.egg.passmark.ui.main.password_edit.PasswordEditScreen
import easter.egg.passmark.ui.main.password_view.PasswordViewScreen
import easter.egg.passmark.ui.main.settings.SettingsScreen
import easter.egg.passmark.ui.theme.PassMarkTheme
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.extensions.findPassword
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private val TAG = this::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent(
            content = {
                PassMarkTheme {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        content = { innerPadding ->
                            MainActivityNavHost(
                                modifier = Modifier.padding(
                                    paddingValues = innerPadding
                                )
                            )
                        }
                    )
                }
            }
        )
    }

    @Composable
    fun MainActivityNavHost(
        modifier: Modifier
    ) {
        val navController = rememberNavController()
        val mainViewModel: MainViewModel by viewModels()
        val result =
            (mainViewModel.screenState.collectAsState().value as? ScreenState.Loaded)
                ?.result
        val passwordList = result?.passwordListState?.collectAsState()?.value
        val vaultList = result?.vaultListState?.collectAsState()?.value
        NavHost(
            modifier = modifier.fillMaxSize(),
            navController = navController,
            startDestination = MainScreens.Home,
            builder = {
                val composableModifier = Modifier.fillMaxSize()
                composable<MainScreens.Home>(
                    content = {
                        val homeViewModel: HomeViewModel = hiltViewModel(viewModelStoreOwner = it)
                        HomeScreen.Screen(
                            modifier = composableModifier,
                            toPasswordEditScreen = { password: Password? ->
                                navController.navigate(
                                    route = MainScreens.PasswordEdit(
                                        localId = password?.localId,
                                        cloudId = password?.cloudId,
                                        defaultVaultId = homeViewModel.vaultIdSelected.value
                                    )
                                )
                            },
                            mainViewModel = mainViewModel,
                            toViewPasswordScreen = { password: Password ->
                                navController.navigate(
                                    route = MainScreens.PasswordView(
                                        passwordJson = Gson().toJson(password)
                                    )
                                )
                            },
                            homeViewModel = homeViewModel,
                            toSettingsScreen = { navController.navigate(route = MainScreens.Settings) }
                        )
                    }
                )
                composable<MainScreens.PasswordEdit>(
                    content = {
                        PasswordEditScreen.Screen(
                            modifier = composableModifier,
                            viewModel = hiltViewModel(viewModelStoreOwner = it),
                            mainViewModel = mainViewModel,
                            navigateBack = { navController.navigateUp() },
                            passwordToEdit = it.arguments?.let { args ->
                                passwordList?.findPassword(
                                    cloudId = args
                                        .getInt(MainScreens.PasswordEdit::cloudId.name, -1)
                                        .takeUnless { id -> id == -1 },
                                    localId = args
                                        .getInt(MainScreens.PasswordEdit::localId.name, -1)
                                        .takeUnless { id -> id == -1 }
                                )
                            },
                            defaultVaultId = it.arguments
                                ?.getInt(MainScreens.PasswordEdit::defaultVaultId.name, -1)
                                ?.takeUnless { id -> id == -1 }
                        )
                    }
                )
                composable<MainScreens.PasswordView>(
                    content = { navBackStackEntry ->
                        val defaultPassword = Gson().fromJson(
                            navBackStackEntry.arguments!!.getString(MainScreens.PasswordView::passwordJson.name)!!,
                            Password::class.java
                        )
                        val password = passwordList?.findPassword(
                            localId = defaultPassword.localId,
                            cloudId = defaultPassword.cloudId
                        ) ?: defaultPassword
                        PasswordViewScreen.Screen(
                            modifier = composableModifier,
                            password = password,
                            navigateUp = { navController.navigateUp() },
                            toEditScreen = {
                                navController.navigate(
                                    route = MainScreens.PasswordEdit(
                                        localId = password.localId,
                                        cloudId = password.cloudId,
                                        defaultVaultId = password.vaultId
                                    )
                                )
                            },
                            associatedVault = password.vaultId?.let { vid -> vaultList?.find { v -> v.id == vid } },
                            passwordViewViewModel = hiltViewModel(viewModelStoreOwner = navBackStackEntry),
                            mainViewModel = mainViewModel
                        )
                    }
                )

                composable<MainScreens.Settings>(
                    content = {
                        SettingsScreen.Screen(
                            modifier = composableModifier,
                            settingsViewModel = hiltViewModel(viewModelStoreOwner = it),
                            navigateUp = { navController.navigateUp() }
                        )
                    }
                )
            }
        )
    }
}

private sealed class MainScreens {
    @Serializable
    data object Home : MainScreens()

    @Serializable
    data class PasswordEdit(
        val localId: Int?,
        val cloudId: Int?,
        val defaultVaultId: Int?
    ) : MainScreens()

    @Serializable
    data class PasswordView(
        val passwordJson: String
    ) : MainScreens()

    @Serializable
    data object Settings : MainScreens()
}