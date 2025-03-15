package easter.egg.passmark.ui.sections.loader

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import easter.egg.passmark.data.shared.PassMarkDimensions
import easter.egg.passmark.utils.annotation.MobileHorizontalPreview
import easter.egg.passmark.utils.annotation.MobilePreview

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
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
            content = {
                CircularProgressIndicator(
                    modifier = Modifier.size(size = PassMarkDimensions.minTouchSize)
                )
            }
        )
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