package easter.egg.passmark

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import easter.egg.passmark.ui.sections.login.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent(
            content = {
                MaterialTheme {
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
            startDestination = MainDestinations.LOGIN.path,
            builder = {
                val composableModifier = Modifier.fillMaxSize()
                composable(
                    route = MainDestinations.LOGIN.path,
                    content = { LoginScreen.Screen(modifier = composableModifier) }
                )
                composable(
                    route = MainDestinations.ACCOUNT_SETUP.path,
                    content = { TODO() }
                )
                composable(
                    route = MainDestinations.HOME.path,
                    content = { TODO() }
                )
                composable(
                    route = MainDestinations.PASSWORD_EDIT_SCREEN.path,
                    content = { TODO() }
                )
            }
        )
    }
}

enum class MainDestinations {
    LOGIN,
    ACCOUNT_SETUP,
    HOME,
    PASSWORD_EDIT_SCREEN;

    val path: String get() = "${this}_PATH"
}