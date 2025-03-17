package easter.egg.passmark.ui.sections.password_edit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import easter.egg.passmark.data.shared.PassMarkDimensions
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