package easter.egg.passmark

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import easter.egg.passmark.ui.main.home.HomeScreen
import easter.egg.passmark.ui.auth.loader.LoaderScreen
import easter.egg.passmark.ui.auth.loader.LoaderViewModel
import easter.egg.passmark.ui.auth.login.LoginScreen
import easter.egg.passmark.ui.auth.login.LoginViewModel
import easter.egg.passmark.ui.main.password_edit.PasswordEditScreen
import easter.egg.passmark.ui.main.password_edit.PasswordEditViewModel
import easter.egg.passmark.ui.auth.master_key.MasterKeyScreen
import easter.egg.passmark.ui.auth.master_key.UserEditViewModel
import easter.egg.passmark.ui.theme.PassMarkTheme
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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
        NavHost(
            modifier = modifier.fillMaxSize(),
            navController = navController,
            startDestination = Screens.Loader,
            builder = {
                val composableModifier = Modifier.fillMaxSize()
                composable<Screens.Loader>(
                    content = {
                        val viewModel: LoaderViewModel = hiltViewModel(viewModelStoreOwner = it)
                        val navOptions = NavOptions.Builder()
                            .setPopUpTo<Screens.Loader>(inclusive = true)
                            .build()
                        LoaderScreen.Screen(
                            modifier = composableModifier,
                            viewModel = viewModel,
                            toHomeScreen = {
                                navController.navigate(
                                    route = Screens.Home,
                                    navOptions = navOptions
                                )
                            },
                            toLoginScreen = {
                                navController.navigate(
                                    route = Screens.Login,
                                    navOptions = navOptions
                                )
                            },
                            toMasterKeyScreen = { isNewUser: Boolean ->
                                navController.navigate(
                                    route = Screens.MasterKey(isNewUser = isNewUser),
                                    navOptions = navOptions
                                )
                            }
                        )
                    }
                )
                composable<Screens.Login>(
                    content = {
                        val viewModel: LoginViewModel = hiltViewModel(viewModelStoreOwner = it)
                        LoginScreen.Screen(
                            modifier = composableModifier,
                            viewModel = viewModel,
                            toLoaderScreen = {
                                navController.navigate(
                                    route = Screens.Loader,
                                    navOptions = NavOptions
                                        .Builder()
                                        .setPopUpTo<Screens.Login>(inclusive = true)
                                        .build()
                                )
                            }
                        )
                    }
                )
                composable<Screens.MasterKey>(
                    content = {
                        val viewModel: UserEditViewModel = hiltViewModel(viewModelStoreOwner = it)
                        val isNewUser = it.arguments!!.getBoolean(Screens.MasterKey::isNewUser.name)
                        MasterKeyScreen.Screen(
                            modifier = composableModifier,
                            viewModel = viewModel,
                            isNewUser = isNewUser,
                            toLoaderScreen = {
                                navController.navigate(
                                    route = Screens.Loader,
                                    navOptions = NavOptions
                                        .Builder()
                                        .setPopUpTo<Screens.MasterKey>(inclusive = true)
                                        .build()
                                )
                            }
                        )
                    }
                )
                composable<Screens.Home>(
                    content = {
                        HomeScreen.Screen(
                            modifier = composableModifier,
                            toAddNewPasswordScreen = { navController.navigate(route = Screens.PasswordEdit) }
                        )
                    }
                )
                composable<Screens.PasswordEdit>(
                    content = {
                        val viewModel: PasswordEditViewModel =
                            hiltViewModel(viewModelStoreOwner = it)
                        PasswordEditScreen.Screen(
                            modifier = composableModifier,
                            viewModel = viewModel,
                            navigateBack = { navController.navigateUp() }
                        )
                    }
                )
            }
        )
    }
}

sealed class Screens {
    @Serializable
    data object Loader : Screens()

    @Serializable
    data object Login : Screens()

    @Serializable
    data class MasterKey(val isNewUser: Boolean) : Screens()

    @Serializable
    data object Home : Screens()

    @Serializable
    data object PasswordEdit : Screens()
}