package easter.egg.passmark

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import easter.egg.passmark.ui.sections.home.HomeScreen
import easter.egg.passmark.ui.sections.loader.LoaderScreen
import easter.egg.passmark.ui.sections.loader.LoaderViewModel
import easter.egg.passmark.ui.sections.login.LoginScreen
import easter.egg.passmark.ui.sections.login.LoginViewModel
import easter.egg.passmark.ui.sections.password_edit.PasswordEditScreen
import easter.egg.passmark.ui.sections.password_edit.PasswordEditViewModel
import easter.egg.passmark.ui.sections.user_edit.UserEditScreen
import easter.egg.passmark.ui.sections.user_edit.UserEditViewModel
import easter.egg.passmark.ui.theme.PassMarkTheme
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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
                        val viewModel: LoaderViewModel by viewModels()
                        val navOptions = NavOptions.Builder()
                            .setPopUpTo(route = Screens.Loader, inclusive = true)
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
                            toEditUserScreen = { isNewUser: Boolean ->
                                navController.navigate(
                                    route = Screens.UserEdit(isNewUser = isNewUser)
                                )
                            }
                        )
                    }
                )
                composable<Screens.Login>(
                    content = {
                        val viewModel by viewModels<LoginViewModel>()
                        LoginScreen.Screen(
                            modifier = composableModifier,
                            viewModel = viewModel,
                            toHomeScreen = {
                                navController.navigate(
                                    route = Screens.Home,
                                    navOptions = NavOptions
                                        .Builder()
                                        .setPopUpTo(
                                            route = Screens.Login,
                                            inclusive = true
                                        )
                                        .build()
                                )
                            }
                        )
                    }
                )
                composable<Screens.UserEdit>(
                    content = {
                        val viewModel by viewModels<UserEditViewModel>()
                        UserEditScreen.Screen(
                            modifier = composableModifier,
                            viewModel = viewModel,
                            isNewUser = it.arguments!!.getBoolean(Screens.UserEdit::isNewUser.name)
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
                        val viewModel: PasswordEditViewModel by viewModels()
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
    data class UserEdit(val isNewUser: Boolean) : Screens()

    @Serializable
    data object Home : Screens()

    @Serializable
    data object PasswordEdit : Screens()
}