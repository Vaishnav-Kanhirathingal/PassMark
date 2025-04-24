package easter.egg.passmark.ui.shared_components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import easter.egg.passmark.ui.main.settings.DeletionStages
import easter.egg.passmark.utils.annotation.MobilePreview
import easter.egg.passmark.utils.values.PassMarkDimensions
import easter.egg.passmark.utils.values.PassMarkFonts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StagedLoaderDialog(
    modifier: Modifier,
    currentActiveStage: Int,
    totalStages: Int,
    showCurrentStageError: Boolean,
    title: String,
    subtitle: String
) {
    BasicAlertDialog(
        modifier = modifier
            .clip(shape = RoundedCornerShape(size = PassMarkDimensions.dialogRadius))
            .background(color = MaterialTheme.colorScheme.surfaceContainer),
        onDismissRequest = {},
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    space = 8.dp,
                    alignment = Alignment.CenterVertically
                ),
                content = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        fontFamily = PassMarkFonts.font,
                        fontSize = PassMarkFonts.Title.medium,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                        text = title
                    )
                    CustomStagedLoader(
                        currentActiveStage = currentActiveStage,
                        totalStages = totalStages,
                        showCurrentStageError = showCurrentStageError
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        fontFamily = PassMarkFonts.font,
                        fontSize = PassMarkFonts.Body.medium,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                        text = subtitle
                    )
                }
            )
        }
    )
}

@Composable
fun CustomStagedLoader(
    currentActiveStage: Int,
    totalStages: Int,
    startSize: Dp = PassMarkDimensions.minTouchSize,
    layerWidth: Dp = 4.dp,
    layerGap: Dp = 2.dp,
    showCurrentStageError: Boolean
) {
    val loaderColor = MaterialTheme.colorScheme.primary
    val loaderTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest

    Box(
        modifier = Modifier,
        contentAlignment = Alignment.Center,
        content = {
            repeat(
                times = totalStages,
                action = { stage ->
                    val loaderModifier = Modifier.size(
                        size = startSize + ((layerWidth + layerGap) * 2 * stage)
                    )
                    if (stage == currentActiveStage) {
                        CircularProgressIndicator(
                            modifier = loaderModifier,
                            color =
                                if (showCurrentStageError) MaterialTheme.colorScheme.error
                                else loaderColor,
                            strokeWidth = layerWidth,
                            trackColor = loaderTrackColor
                        )
                    } else {
                        CircularProgressIndicator(
                            modifier = loaderModifier,
                            color = loaderColor,
                            strokeWidth = layerWidth,
                            trackColor = loaderTrackColor,
                            progress = { if (currentActiveStage > stage) 1f else 0f },
                        )
                    }

                }
            )
        }
    )
}

@Composable
@MobilePreview
private fun DeleteProgressDialogPreview() {
    StagedLoaderDialog(
        modifier = Modifier.padding(horizontal = 40.dp),
        currentActiveStage = DeletionStages.USER_TABLE_ITEM.ordinal + 1,
        totalStages = DeletionStages.entries.size,
        showCurrentStageError = true,
        title = "Deleting everything. Avoid closing the app to prevent data corruption.",
        subtitle = DeletionStages.USER_TABLE_ITEM.getTaskMessage()
    )
}