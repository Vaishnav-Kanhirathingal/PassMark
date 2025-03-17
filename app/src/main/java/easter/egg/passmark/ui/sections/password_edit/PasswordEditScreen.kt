package easter.egg.passmark.ui.sections.password_edit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.Visibility
import easter.egg.passmark.data.shared.PassMarkDimensions
import easter.egg.passmark.data.shared.PassMarkFonts
import easter.egg.passmark.data.shared.setSizeLimitation
import easter.egg.passmark.utils.annotation.MobileHorizontalPreview
import easter.egg.passmark.utils.annotation.MobilePreview

object PasswordEditScreen {
    @Composable
    fun Screen(modifier: Modifier) {
        // TODO: take [title], [email, username, password], [website], [note]
        val barModifier = Modifier
            .fillMaxWidth()
            .heightIn(min = PassMarkDimensions.minTouchSize)
        Scaffold(
            modifier = modifier,
            topBar = { EditTopBar(modifier = barModifier) },
            content = {
                EditContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = it)
                )
            },
            bottomBar = { EditBottomBar(modifier = barModifier) }
        )
    }

    @Composable
    private fun EditTopBar(
        modifier: Modifier
    ) {
        Row(
            modifier = modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            content = {
                Box(
                    modifier = Modifier
                        .size(size = PassMarkDimensions.minTouchSize)
                        .clickable(
                            onClick = {
                                TODO()
                            }
                        )
                        .clip(shape = CircleShape)
                        .background(color = MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                    content = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                )
                Spacer(
                    modifier = Modifier
                        .weight(weight = 1f)
                        .height(height = PassMarkDimensions.minTouchSize)
                )

                Button(
                    modifier = Modifier.setSizeLimitation(),
                    onClick = { TODO() },
                    content = { Text(text = "Save") }
                )
            }
        )
        // TODO: pending
    }

    @Composable
    private fun EditContent(
        modifier: Modifier
    ) {
        // TODO: pending
    }

    @Composable
    fun CustomTextField(
        modifier: Modifier,
        startIcon: ImageVector,
        heading: String,
        hint: String,
        text: String,
        onTextChange: (String) -> Unit
    ) {
        val hasFocus = true
        BasicTextField(
            modifier = modifier,
            value = text,
            onValueChange = onTextChange,
            textStyle = TextStyle(
                fontSize = PassMarkFonts.Body.large,
                fontWeight = FontWeight.Medium,
                fontFamily = PassMarkFonts.font,
            ),
            decorationBox = {
                ConstraintLayout(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    content = {
                        val (iconRef, headingRef, hintRef) = createRefs()
                        Icon(
                            modifier = Modifier.constrainAs(
                                ref = iconRef,
                                constrainBlock = {
                                    start.linkTo(parent.start)
                                    top.linkTo(parent.top)
                                    bottom.linkTo(parent.bottom)
                                }
                            ),
                            imageVector = startIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        val hintComposableIsVisible = (hasFocus || text.isNotEmpty())
                        Box(
                            modifier = Modifier.constrainAs(
                                ref = hintRef,
                                constrainBlock = {
                                    start.linkTo(anchor = iconRef.end, margin = 4.dp)
                                    top.linkTo(iconRef.top)
                                    bottom.linkTo(iconRef.bottom)
                                    width = Dimension.fillToConstraints
                                    visibility =
                                        if (hintComposableIsVisible) Visibility.Visible
                                        else Visibility.Gone
                                }
                            ),
                            content = {
                                if (text.isEmpty()) {
                                    Text(
                                        modifier = Modifier,
                                        text = hint,
                                        textAlign = TextAlign.Start,
                                        fontSize = PassMarkFonts.Body.large,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = PassMarkFonts.font,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                } else {
                                    it()
                                }
                            }
                        )
//                        Text(
//                            modifier = Modifier.constrainAs(
//                                ref = hintRef,
//                                constrainBlock = {
//                                    start.linkTo(anchor = iconRef.end, margin = 4.dp)
//                                    top.linkTo(iconRef.top)
//                                    bottom.linkTo(iconRef.bottom)
//                                    width = Dimension.fillToConstraints
//                                    visibility =
//                                        if (hintComposableIsVisible) Visibility.Visible
//                                        else Visibility.Gone
//                                }
//                            ),
//                            text = hint,
//                            textAlign = TextAlign.Start,
//                            fontSize = PassMarkFonts.Body.large,
//                            fontWeight = FontWeight.Medium,
//                            fontFamily = PassMarkFonts.font,
//                            color = MaterialTheme.colorScheme.onPrimaryContainer
//                        )
                        Text(
                            modifier = Modifier.constrainAs(
                                ref = headingRef,
                                constrainBlock = {
                                    if (hintComposableIsVisible) {
                                        bottom.linkTo(hintRef.top)
                                    } else {
                                        top.linkTo(iconRef.top)
                                        bottom.linkTo(iconRef.bottom)
                                    }
                                    start.linkTo(anchor = iconRef.end, margin = 4.dp)
                                    end.linkTo(parent.end)
                                    width = Dimension.fillToConstraints
                                }
                            ),
                            text = heading,
                            textAlign = TextAlign.Start,
                            fontSize = PassMarkFonts.Label.small,
                            fontWeight = FontWeight.Normal,
                            fontFamily = PassMarkFonts.font,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                    }
                )
            }
        )
    }

    @Composable
    private fun EditBottomBar(
        modifier: Modifier
    ) {
        // TODO: pending
    }
}

@Composable
@MobilePreview
@MobileHorizontalPreview
private fun PasswordEditScreenPreview() {
    PasswordEditScreen.Screen(modifier = Modifier.fillMaxSize())
}

@Composable
@Preview(
    widthDp = 360,
    heightDp = 180,
    showBackground = true
)
fun CustomTextFieldPreview() {
    PasswordEditScreen.CustomTextField(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
//            .requiredHeight(height = 96.dp)
            .padding(all = 16.dp)
            .clip(shape = RoundedCornerShape(size = 8.dp))
            .background(color = MaterialTheme.colorScheme.primaryContainer),
        startIcon = Icons.Default.Search,
        heading = "Some Heading",
        hint = "Some Hint",
        text = "hi",
        onTextChange = {}
    )
}