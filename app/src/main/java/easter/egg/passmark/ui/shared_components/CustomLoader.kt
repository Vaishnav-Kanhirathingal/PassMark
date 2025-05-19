package easter.egg.passmark.ui.shared_components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.StartOffsetType
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue

object CustomLoader {
    @Composable
    fun FullScreenLoader(
        modifier: Modifier,
    ) {
        val config = object {
            val spacing = 2.dp
            val barWidth = 10.dp
            val fullWidth = (barWidth * 5) + (spacing * 4)
        }
        Row(
            modifier = modifier.sizeIn(
                minWidth = config.fullWidth,
                minHeight = config.fullWidth,
            ),
            horizontalArrangement = Arrangement.spacedBy(
                space = config.spacing,
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
                            initialValue = config.barWidth.value,
                            targetValue = config.fullWidth.value,
                            animationSpec = infiniteRepeatable(
                                animation = tween(
                                    durationMillis = 700,
                                    easing = FastOutSlowInEasing,
                                    delayMillis = 200
                                ),
                                initialStartOffset = StartOffset(
                                    offsetMillis = (230 * impliedIndex),
                                    offsetType = StartOffsetType.FastForward
                                ),
                                repeatMode = RepeatMode.Reverse
                            )
                        )
                        Box(
                            modifier = Modifier
                                .width(width = config.barWidth)
                                .clip(shape = RoundedCornerShape(size = 4.dp))
                                .background(
                                    color = when (impliedIndex % 3) {
                                        0 -> MaterialTheme.colorScheme.primary
                                        1 -> MaterialTheme.colorScheme.secondary
                                        2 -> MaterialTheme.colorScheme.tertiary
                                        else -> throw IllegalStateException()
                                    }
                                )
                                .height(height = springValue.value.dp),
                        )
                    }
                )
            }
        )
    }

    @Composable
    fun ButtonLoader(
        modifier: Modifier,
        color: Color
    ) {
        val config = object {
            val barWidth = 4.dp
            val spacing = 1.dp
            val totalBarCount = 5
        }

        val totalSizeMin = config.run {
            (barWidth * totalBarCount) + (spacing * (totalBarCount - 1))
        }
        Row(
            modifier = modifier.sizeIn(
                minWidth = totalSizeMin,
                minHeight = totalSizeMin
            ),
            horizontalArrangement = Arrangement.spacedBy(
                space = config.spacing,
                alignment = Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically,
            content = {
                repeat(
                    times = config.totalBarCount,
                    action = { index ->
                        val height = rememberInfiniteTransition()
                            .animateFloat(
                                initialValue = config.barWidth.value,
                                targetValue = totalSizeMin.value,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(
                                        durationMillis = 800,
                                        delayMillis = 0,
                                        easing = FastOutSlowInEasing
                                    ),
                                    repeatMode = RepeatMode.Reverse,
                                    initialStartOffset = StartOffset(
                                        offsetMillis = (150 * index),
                                    )
                                )
                            )
                        Box(
                            modifier = Modifier
                                .size(
                                    width = config.barWidth,
                                    height = height.value.dp
                                )
                                .background(
                                    color = color,
                                    shape = CircleShape
                                )
                        )
                    }
                )
            }
        )
    }
}

@Composable
@Preview(widthDp = 100, heightDp = 100, showBackground = true)
private fun FullScreenLoaderPreview() {
    CustomLoader.FullScreenLoader(modifier = Modifier.fillMaxSize())
}

@Composable
@Preview(widthDp = 200, heightDp = 48, showBackground = true)
private fun CustomLoaderPreview() {
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(
                    size = 16.dp
                )
            ),
        contentAlignment = Alignment.Center,
        content = {
            CustomLoader.ButtonLoader(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    )
}