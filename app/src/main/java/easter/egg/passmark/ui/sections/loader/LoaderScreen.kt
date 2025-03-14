package easter.egg.passmark.ui.sections.loader

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import easter.egg.passmark.utils.MobileHorizontalPreview
import easter.egg.passmark.utils.MobilePreview

object LoaderScreen {
    private val TAG = this::class.simpleName

    @Composable
    fun Screen(
        modifier: Modifier,
        viewModel: LoaderViewModel,
        toHomeScreen: () -> Unit,
        toLoginScreen: () -> Unit
    ) {
        LaunchedEffect(
            key1 = viewModel.hasUser.value,
            block = {
                when (viewModel.hasUser.value) {
                    true -> toHomeScreen()
                    false -> toLoginScreen()
                    else -> Log.d(TAG, "loading in process")
                }
            }
        )
        CircularProgressIndicator()
    }
}

@Composable
@MobilePreview
@MobileHorizontalPreview
fun LoaderScreenPreview() {
    LoaderScreen.Screen(
        modifier = Modifier.fillMaxSize(),
        viewModel = LoaderViewModel(),
        toHomeScreen = {},
        toLoginScreen = {}
    )
}