package easter.egg.passmark.utils.extensions

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

fun Modifier.customTopBarModifier(): Modifier =
    this
        .fillMaxWidth()
        .padding(
            horizontal = 16.dp,
            vertical = 8.dp
        )