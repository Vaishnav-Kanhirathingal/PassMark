package easter.egg.passmark.ui.shared_components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue

@Composable
fun CustomLoader(
    modifier: Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(
            space = 1.dp,
            alignment = Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically,
        content = {
            val onSide = 2
            repeat(
                times = (onSide * 2) + 1,
                action = { index ->
                    val impliedIndex = (index - onSide).absoluteValue

                    val springValue = rememberInfiniteTransition(
                        label = "verticalBar [$index]"
                    ).animateFloat(
                        initialValue = 4f,
                        targetValue = 50f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(
                                durationMillis = 1000,
                                easing = FastOutSlowInEasing,
                                delayMillis = 200
                            ),
                            initialStartOffset = StartOffset(
                                offsetMillis = (200 * impliedIndex)
                            ),
                            repeatMode = RepeatMode.Reverse
                        )
                    )

                    val mainColor = when (impliedIndex % 3) {
                        0 -> MaterialTheme.colorScheme.primary
                        1 -> MaterialTheme.colorScheme.secondary
                        2 -> MaterialTheme.colorScheme.tertiary
                        else -> throw IllegalStateException()
                    }
                    val containerColor = when (impliedIndex % 3) {
                        0 -> MaterialTheme.colorScheme.primaryContainer
                        1 -> MaterialTheme.colorScheme.secondaryContainer
                        2 -> MaterialTheme.colorScheme.tertiaryContainer
                        else -> throw IllegalStateException()
                    }

                    Box(
                        modifier = Modifier
                            .width(width = 10.dp)
                            .clip(shape = RoundedCornerShape(size = 4.dp))
                            .background(color = containerColor)
                            .padding(3.dp)
                            .background(
                                color = mainColor,
                                shape = RoundedCornerShape(
                                    size = 2.dp
                                )
                            )
                            .height(height = springValue.value.dp),
                    )
                }
            )
        }
    )
}

@Composable
@Preview(widthDp = 100, heightDp = 100, showBackground = true)
fun CustomLoaderPreview() {
    CustomLoader(modifier = Modifier.fillMaxSize())
}