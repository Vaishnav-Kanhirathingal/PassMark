package easter.egg.passmark.ui.shared_components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.annotation.MobilePreview
import easter.egg.passmark.utils.values.PassMarkDimensions
import easter.egg.passmark.utils.values.PassMarkFonts
import easter.egg.passmark.utils.values.setSizeLimitation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationDialog(
    modifier: Modifier,
    titleText: String,
    contentText: String,
    negativeButtonText: String,
    onNegativeClicked: () -> Unit,
    positiveButtonText: String,
    onPositiveClicked: () -> Unit,
    screenState: ScreenState<Unit>
) {
    BasicAlertDialog(
        modifier = modifier,
        onDismissRequest = onNegativeClicked,
        content = {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(size = PassMarkDimensions.dialogRadius)
                    ),
                content = {
                    val (titleRef, subtitleRef, negativeButtonRef, positiveButtonRef) = createRefs()
                    Text(
                        modifier = Modifier.constrainAs(
                            ref = titleRef,
                            constrainBlock = {
                                this.top.linkTo(anchor = parent.top, margin = 16.dp)
                                this.start.linkTo(anchor = parent.start, margin = 16.dp)
                                this.end.linkTo(anchor = parent.end, margin = 16.dp)
                                width = Dimension.fillToConstraints
                            }
                        ),
                        fontFamily = PassMarkFonts.font,
                        fontSize = PassMarkFonts.Headline.medium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        text = titleText
                    )
                    Text(
                        modifier = Modifier.constrainAs(
                            ref = subtitleRef,
                            constrainBlock = {
                                this.top.linkTo(anchor = titleRef.bottom, margin = 8.dp)
                                this.start.linkTo(titleRef.start)
                                this.end.linkTo(titleRef.end)
                                width = Dimension.fillToConstraints
                            }
                        ),
                        text = contentText,
                        fontFamily = PassMarkFonts.font,
                        fontSize = PassMarkFonts.Body.medium,
                        lineHeight = PassMarkFonts.Body.medium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    val buttonSpacing = 8.dp

                    @Composable
                    fun CustomButton(
                        modifier: Modifier,
                        text: String,
                        isLoading: Boolean,
                        containerColor: Color,
                        onContainerColor: Color,
                        onClick: () -> Unit
                    ) {
                        Box(
                            modifier = modifier
                                .setSizeLimitation()
                                .clip(shape = RoundedCornerShape(size = PassMarkDimensions.dialogRadius - buttonSpacing))
                                .background(color = containerColor)
                                .clickable(
                                    enabled = !screenState.isLoading,
                                    onClick = onClick
                                ),
                            contentAlignment = Alignment.Center,
                            content = {
                                if (isLoading) {
                                    CustomLoader.ButtonLoader(
                                        modifier = Modifier.size(size = 24.dp),
                                        color = onContainerColor,
                                    )
                                } else {
                                    Text(
                                        text = text,
                                        fontFamily = PassMarkFonts.font,
                                        fontSize = PassMarkFonts.Title.medium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = onContainerColor
                                    )
                                }
                            }
                        )
                    }


                    CustomButton(
                        modifier = Modifier
                            .constrainAs(
                                ref = negativeButtonRef,
                                constrainBlock = {
                                    this.start.linkTo(
                                        anchor = parent.start,
                                        margin = buttonSpacing
                                    )
                                    this.end.linkTo(
                                        anchor = positiveButtonRef.start,
                                        margin = (buttonSpacing / 2)
                                    )
                                    this.top.linkTo(
                                        anchor = subtitleRef.bottom,
                                        margin = buttonSpacing
                                    )
                                    this.bottom.linkTo(
                                        anchor = parent.bottom,
                                        margin = buttonSpacing
                                    )
                                    width = Dimension.fillToConstraints
                                }
                            ),
                        text = negativeButtonText,
                        isLoading = false,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        onContainerColor = MaterialTheme.colorScheme.onSurface,
                        onClick = onNegativeClicked
                    )


                    CustomButton(
                        modifier = Modifier
                            .constrainAs(
                                ref = positiveButtonRef,
                                constrainBlock = {
                                    this.start.linkTo(
                                        anchor = negativeButtonRef.end,
                                        margin = (buttonSpacing / 2)
                                    )
                                    this.end.linkTo(anchor = parent.end, margin = buttonSpacing)
                                    this.top.linkTo(negativeButtonRef.top)
                                    this.bottom.linkTo(negativeButtonRef.bottom)
                                    width = Dimension.fillToConstraints
                                }
                            ),
                        text = positiveButtonText,
                        isLoading = screenState.isLoading,
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        onContainerColor = MaterialTheme.colorScheme.onErrorContainer,
                        onClick = onPositiveClicked
                    )
                }
            )
        },
    )
}

@Composable
@MobilePreview
private fun ConfirmationDialogPreview() {
    ConfirmationDialog(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp),
        titleText = "Delete?",
        contentText = "Deletion is permanent",
        negativeButtonText = "cancel",
        onNegativeClicked = {},
        positiveButtonText = "Delete",
        onPositiveClicked = {},
        screenState = ScreenState.PreCall(),
    )
}
