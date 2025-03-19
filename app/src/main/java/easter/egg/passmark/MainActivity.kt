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
import easter.egg.passmark.ui.sections.home.HomeScreen
import easter.egg.passmark.ui.sections.loader.LoaderScreen
import easter.egg.passmark.ui.sections.loader.LoaderViewModel
import easter.egg.passmark.ui.sections.login.LoginScreen
import easter.egg.passmark.ui.sections.login.LoginViewModel
import easter.egg.passmark.ui.sections.password_edit.PasswordEditScreen
import easter.egg.passmark.ui.sections.password_edit.PasswordEditViewModel
import easter.egg.passmark.ui.theme.PassMarkTheme

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
            startDestination = MainDestinations.LOADER.path,
            builder = {
                val composableModifier = Modifier.fillMaxSize()
                composable(
                    route = MainDestinations.LOADER.path,
                    content = {
                        val viewModel: LoaderViewModel by viewModels()
                        val navOptions = NavOptions.Builder()
                            .setPopUpTo(route = MainDestinations.LOADER.path, inclusive = true)
                            .build()
                        LoaderScreen.Screen(
                            modifier = composableModifier,
                            viewModel = viewModel,
                            toHomeScreen = {
                                navController.navigate(
                                    route = MainDestinations.HOME.path,
                                    navOptions = navOptions
                                )
                            },
                            toLoginScreen = {
                                navController.navigate(
                                    route = MainDestinations.LOGIN.path,
                                    navOptions = navOptions
                                )
                            }
                        )
                    }
                )
                composable(
                    route = MainDestinations.LOGIN.path,
                    content = {
                        val viewModel by viewModels<LoginViewModel>()
                        LoginScreen.Screen(
                            modifier = composableModifier,
                            viewModel = viewModel,
                            toHomeScreen = {
                                navController.navigate(
                                    route = MainDestinations.HOME.path,
                                    navOptions = NavOptions
                                        .Builder()
                                        .setPopUpTo(
                                            route = MainDestinations.LOGIN.path,
                                            inclusive = true
                                        )
                                        .build()
                                )
                            }
                        )
                    }
                )
                composable(
                    route = MainDestinations.HOME.path,
                    content = {
                        HomeScreen.Screen(
                            modifier = composableModifier,
                            toAddNewPasswordScreen = { navController.navigate(route = MainDestinations.PASSWORD_EDIT.path) }
                        )
                    }
                )
                composable(
                    route = MainDestinations.PASSWORD_EDIT.path,
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

enum class MainDestinations {
    LOADER,
    LOGIN,
    HOME,
    PASSWORD_EDIT;

    val path: String get() = "${this}_PATH"
}