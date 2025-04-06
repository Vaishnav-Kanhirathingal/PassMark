package easter.egg.passmark.ui.main

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import easter.egg.passmark.ui.main.home.screens.HomeScreen
import easter.egg.passmark.ui.main.password_edit.PasswordEditScreen
import easter.egg.passmark.ui.main.password_edit.PasswordEditViewModel
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
        val mainViewModel: MainViewModel by viewModels()
        NavHost(
            modifier = modifier.fillMaxSize(),
            navController = navController,
            startDestination = MainScreens.Home,
            builder = {
                val composableModifier = Modifier.fillMaxSize()
                composable<MainScreens.Home>(
                    content = {
                        HomeScreen.Screen(
                            modifier = composableModifier,
                            toAddNewPasswordScreen = { navController.navigate(route = MainScreens.PasswordEdit) },
                            mainViewModel = mainViewModel,
                            toViewPasswordScreen = { passwordId -> TODO() },
                            homeViewModel = hiltViewModel(viewModelStoreOwner = it)
                        )
                    }
                )
                composable<MainScreens.PasswordEdit>(
                    content = {
                        val viewModel: PasswordEditViewModel =
                            hiltViewModel(viewModelStoreOwner = it)
                        PasswordEditScreen.Screen(
                            modifier = composableModifier,
                            viewModel = viewModel,
                            mainViewModel = mainViewModel,
                            navigateBack = { navController.navigateUp() }
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
    data object PasswordEdit : MainScreens()
}