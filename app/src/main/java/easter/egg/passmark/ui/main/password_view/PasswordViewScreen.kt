package easter.egg.passmark.ui.main.password_view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import easter.egg.passmark.utils.annotation.MobileHorizontalPreview
import easter.egg.passmark.utils.annotation.MobilePreview
import easter.egg.passmark.utils.values.PassMarkFonts

object PasswordViewScreen {
    @Composable
    fun Screen(
        modifier: Modifier
    ) {
        Scaffold(
            modifier = modifier,
            topBar = {
                PasswordViewTopBar(modifier = Modifier.fillMaxWidth())
            },
            content = {
                PasswordViewContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = it)
                )
            }
        )
    }

    @Composable
    private fun PasswordViewTopBar(
        modifier: Modifier
    ) {
        val barSize = 60.dp
        Row(
            modifier = modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.Start
            ),
            verticalAlignment = Alignment.CenterVertically,
            content = {
                Box(
                    modifier = Modifier
                        .size(size = barSize)
                        .clip(shape = CircleShape)
                        .background(color = MaterialTheme.colorScheme.primaryContainer)
                        .clickable(onClick = { TODO() }),
                    contentAlignment = Alignment.Center,
                    content = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                )
                Spacer(modifier = Modifier.weight(weight = 1f))
                Row(
                    modifier = Modifier
                        .height(height = barSize)
                        .widthIn(min = barSize)
                        .clip(shape = CircleShape)
                        .background(color = MaterialTheme.colorScheme.primaryContainer)
                        .padding(
                            start = 24.dp,
                            end = 24.dp,
                            top = 8.dp,
                            bottom = 8.dp
                        ),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 4.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    content = {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Edit",
                            fontFamily = PassMarkFonts.font,
                            fontSize = PassMarkFonts.Body.medium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                )

                Box(
                    modifier = Modifier
                        .size(size = barSize)
                        .clip(shape = CircleShape)
                        .background(color = MaterialTheme.colorScheme.errorContainer)
                        .clickable(onClick = { TODO() }),
                    contentAlignment = Alignment.Center,
                    content = {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                )
            }
        )
    }

    @Composable
    private fun PasswordViewContent(
        modifier: Modifier
    ) {
    }
}

@Composable
@MobilePreview
@MobileHorizontalPreview
private fun PasswordViewScreenPreview() {
    PasswordViewScreen.Screen(
        modifier = Modifier.fillMaxSize()
    )
}