package easter.egg.passmark.ui.sections.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import easter.egg.passmark.R
import easter.egg.passmark.data.shared.PassMarkDimensions
import easter.egg.passmark.data.shared.PassMarkFonts
import easter.egg.passmark.utils.MobileHorizontalPreview
import easter.egg.passmark.utils.MobilePreview

object LoginScreen {
    @Composable
    fun Screen(
        modifier: Modifier
    ) {
        val verticalColumnPadding = 40.dp
        Column(
            modifier = modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(state = rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterVertically
            ),
            content = {
                Spacer(modifier = Modifier.height(height = verticalColumnPadding))
                Image(
                    modifier = Modifier
                        .widthIn(max = 400.dp)
                        .fillMaxWidth(fraction = 0.4f)
                        .clip(shape = RoundedCornerShape(size = 16.dp))
                        .background(color = MaterialTheme.colorScheme.primaryContainer)
                        .padding(all = 28.dp),
                    painter = painterResource(id = R.drawable.passmark_app_icon),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
                )
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "PassMark",
                    textAlign = TextAlign.Center,
                    fontFamily = PassMarkFonts.font,
                    fontSize = PassMarkFonts.Title.large,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    modifier = Modifier.fillMaxWidth(fraction = 0.8f),
                    text = "Join PassMark and embark on a journey of secure password storage",
                    fontSize = PassMarkFonts.Label.medium,
                    lineHeight = PassMarkFonts.Label.medium,
                    fontFamily = PassMarkFonts.font,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                ElevatedButton(
                    modifier = Modifier.sizeIn(
                        minWidth = PassMarkDimensions.minTouchSize,
                        minHeight = PassMarkDimensions.minTouchSize
                    ),
                    onClick = { TODO() },
                    content = {
                        Image(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(id = R.drawable.google_48),
                            contentScale = ContentScale.Fit,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(width = 8.dp))
                        Text(text = "Google")
                    }
                )
                Spacer(modifier = Modifier.height(height = verticalColumnPadding))
            }
        )
    }
}

@Composable
@MobilePreview
@MobileHorizontalPreview
private fun LoginScreenPrev() {
    LoginScreen.Screen(modifier = Modifier.fillMaxSize())
}