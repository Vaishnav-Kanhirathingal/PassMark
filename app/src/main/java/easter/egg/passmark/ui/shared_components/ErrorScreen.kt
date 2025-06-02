package easter.egg.passmark.ui.shared_components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import easter.egg.passmark.utils.ScreenState
import easter.egg.passmark.utils.annotation.MobileHorizontalPreview
import easter.egg.passmark.utils.annotation.MobilePreview
import easter.egg.passmark.utils.testing.TestTags
import easter.egg.passmark.utils.testing.TestTags.applyTag
import easter.egg.passmark.utils.values.PassMarkFonts
import easter.egg.passmark.utils.values.setSizeLimitation

object ErrorScreen {
    val errorCardFullScreenModifier = Modifier
        .widthIn(max = 450.dp)
        .fillMaxWidth()
        .padding(horizontal = 24.dp)

    /** @param attemptedAction is the action that was supposed to be performed where the error was
     * thrown. This string will be used in the error message. write something in the blank.
     * `Something went wrong while __________. please try again.`
     *  */
    @Composable
    fun <T> ErrorCard(
        modifier: Modifier,
        screenState: ScreenState.ApiError<T>,
        onRetry: () -> Unit,
        attemptedAction: String
    ) {
        Column(
            modifier = modifier
                .clip(shape = RoundedCornerShape(size = 16.dp))
                .background(color = MaterialTheme.colorScheme.surfaceContainer)
                .padding(all = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 4.dp,
                alignment = Alignment.CenterVertically
            ),
            content = {
                Icon(
                    modifier = Modifier.size(60.dp),
                    imageVector = Icons.Default.Warning,
                    contentDescription = null
                )
                Text(
                    textAlign = TextAlign.Center,
                    text = when (screenState) {
                        is ScreenState.ApiError.NetworkError -> "Network error"
                        is ScreenState.ApiError.SomethingWentWrong -> "Something went wrong"
                    },
                    fontFamily = PassMarkFonts.font,
                    fontSize = PassMarkFonts.Title.medium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    textAlign = TextAlign.Center,
                    text = when (screenState) {
                        is ScreenState.ApiError.NetworkError -> "A network call has failed"
                        is ScreenState.ApiError.SomethingWentWrong -> "Something went wrong"
                    } + " while ${attemptedAction}. Please check your internet connection and try again.",
                    fontFamily = PassMarkFonts.font,
                    fontSize = PassMarkFonts.Body.medium,
                    lineHeight = PassMarkFonts.Body.medium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Box(
                    modifier = Modifier
                        .applyTag(testTag = TestTags.ErrorScreen.RETRY_BUTTON.name)
                        .setSizeLimitation()
                        .clip(shape = RoundedCornerShape(size = 16.dp))
                        .background(color = MaterialTheme.colorScheme.primaryContainer)
                        .clickable(onClick = onRetry)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center,
                    content = {
                        Text(
                            text = "Try again",
                            fontFamily = PassMarkFonts.font,
                            fontSize = PassMarkFonts.Body.medium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                )
            }
        )
    }
}

@MobilePreview
@MobileHorizontalPreview
@Composable
private fun ErrorScreenPreview() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
        content = {
            ErrorScreen.ErrorCard(
                modifier = Modifier
                    .widthIn(max = 360.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                screenState = ScreenState.ApiError.SomethingWentWrong<Unit>(),
                onRetry = {},
                attemptedAction = "doing something"
            )
        }
    )
}