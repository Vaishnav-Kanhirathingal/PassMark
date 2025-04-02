package easter.egg.passmark.utils.values

import androidx.compose.foundation.layout.sizeIn
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

object PassMarkDimensions {
    val minTouchSize = 48.dp
}

fun Modifier.setSizeLimitation(): Modifier {
    return this.sizeIn(
        minWidth = PassMarkDimensions.minTouchSize,
        minHeight = PassMarkDimensions.minTouchSize
    )
}