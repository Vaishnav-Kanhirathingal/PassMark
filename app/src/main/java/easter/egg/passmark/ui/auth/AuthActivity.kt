package easter.egg.passmark.ui.auth

import android.content.Intent
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
import easter.egg.passmark.ui.auth.loader.LoaderScreen
import easter.egg.passmark.ui.auth.loader.LoaderViewModel
import easter.egg.passmark.ui.auth.login.LoginScreen
import easter.egg.passmark.ui.auth.login.LoginViewModel
import easter.egg.passmark.ui.auth.master_key.MasterKeyScreen
import easter.egg.passmark.ui.auth.master_key.MasterKeyViewModel
import easter.egg.passmark.ui.main.MainActivity
import easter.egg.passmark.ui.theme.PassMarkTheme
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {
    private val TAG = this::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PassMarkTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    content = { innerPadding ->
                        AuthActivityNavHost(
                            modifier = Modifier.padding(
                                paddingValues = innerPadding
                            )
                        )
                    }
                )
            }
        }
    }

    @Composable
    private fun AuthActivityNavHost(
        modifier: Modifier
    ) {
        val navController = rememberNavController()
        NavHost(
            modifier = modifier.fillMaxSize(),
            navController = navController,
            startDestination = AuthScreens.Loader,
            builder = {
                val composableModifier = Modifier.fillMaxSize()
                composable<AuthScreens.Loader>(
                    content = {
                        val viewModel: LoaderViewModel = hiltViewModel(viewModelStoreOwner = it)
                        val navOptions = NavOptions.Builder()
                            .setPopUpTo<AuthScreens.Loader>(inclusive = true)
                            .build()
                        LoaderScreen.Screen(
                            modifier = composableModifier,
                            viewModel = viewModel,
                            toMainActivity = {
                                this@AuthActivity.startActivity(
                                    Intent(
                                        this@AuthActivity,
                                        MainActivity::class.java
                                    )
                                )
                                this@AuthActivity.finish()
                            },
                            toLoginScreen = {
                                navController.navigate(
                                    route = AuthScreens.Login,
                                    navOptions = navOptions
                                )
                            },
                            toMasterKeyScreen = { isNewUser: Boolean ->
                                navController.navigate(
                                    route = AuthScreens.MasterKey(isNewUser = isNewUser),
                                    navOptions = navOptions
                                )
                            }
                        )
                    }
                )
                composable<AuthScreens.Login>(
                    content = {
                        val viewModel: LoginViewModel = hiltViewModel(viewModelStoreOwner = it)
                        LoginScreen.Screen(
                            modifier = composableModifier,
                            viewModel = viewModel,
                            toLoaderScreen = {
                                navController.navigate(
                                    route = AuthScreens.Loader,
                                    navOptions = NavOptions
                                        .Builder()
                                        .setPopUpTo<AuthScreens.Login>(inclusive = true)
                                        .build()
                                )
                            }
                        )
                    }
                )
                composable<AuthScreens.MasterKey>(
                    content = {
                        val viewModel: MasterKeyViewModel = hiltViewModel(viewModelStoreOwner = it)
                        val isNewUser =
                            it.arguments!!.getBoolean(AuthScreens.MasterKey::isNewUser.name)
                        MasterKeyScreen.Screen(
                            modifier = composableModifier,
                            viewModel = viewModel,
                            isNewUser = isNewUser,
                            toLoaderScreen = {
                                navController.navigate(
                                    route = AuthScreens.Loader,
                                    navOptions = NavOptions
                                        .Builder()
                                        .setPopUpTo<AuthScreens.MasterKey>(inclusive = true)
                                        .build()
                                )
                            }
                        )
                    }
                )
            }
        )
    }
}

private sealed class AuthScreens {
    @Serializable
    data object Loader : AuthScreens()

    @Serializable
    data object Login : AuthScreens()

    @Serializable
    data class MasterKey(val isNewUser: Boolean) : AuthScreens()

}