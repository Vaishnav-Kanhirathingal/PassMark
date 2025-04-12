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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import easter.egg.passmark.ui.main.home.screens.HomeScreen
import easter.egg.passmark.ui.main.password_edit.PasswordEditScreen
import easter.egg.passmark.ui.main.password_view.PasswordViewScreen
import easter.egg.passmark.ui.theme.PassMarkTheme
import easter.egg.passmark.utils.ScreenState
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
                        HomeScreen.Screen(
                            modifier = composableModifier,
                            toPasswordEditScreen = { passwordId ->
                                navController.navigate(route = MainScreens.PasswordEdit(passwordId = passwordId))
                            },
                            mainViewModel = mainViewModel,
                            toViewPasswordScreen = { passwordId ->
                                navController.navigate(route = MainScreens.PasswordView(passwordId = passwordId))
                            },
                            homeViewModel = hiltViewModel(viewModelStoreOwner = it)
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
                            passwordToEdit = it.arguments!!
                                .getInt(MainScreens.PasswordEdit::passwordId.name, -1)
                                .takeUnless { id -> id == -1 }
                                .let { id -> passwordList?.find { p -> p.id == id } }
                        )
                    }
                )
                composable<MainScreens.PasswordView>(
                    content = { navBackStackEntry ->
                        val receivedId = navBackStackEntry.arguments!!
                            .getInt(MainScreens.PasswordView::passwordId.name, -1)
                            .takeUnless { it == -1 }
                        val password = passwordList!!.find { p -> p.id == receivedId }!!

                        PasswordViewScreen.Screen(
                            modifier = composableModifier,
                            password = password,
                            navigateUp = { navController.navigateUp() },
                            toEditScreen = {
                                navController.navigate(
                                    route = MainScreens.PasswordEdit(passwordId = receivedId)
                                )
                            },
                            associatedVault =password.vaultId?.let {vid-> vaultList?.find {v->v.id==vid  } }
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
        val passwordId: Int?
    ) : MainScreens()

    @Serializable
    data class PasswordView(
        val passwordId: Int
    ) : MainScreens()
}