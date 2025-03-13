package easter.egg.passmark.ui.sections.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import easter.egg.passmark.R
import easter.egg.passmark.utils.MobilePreview

object LoginScreen {
    @Composable
    fun Screen(
        modifier: Modifier
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            content = {
                Image(
                    modifier = Modifier
                        .fillMaxWidth(fraction = 0.4f)
                        .widthIn(max = 360.dp)
                        .clip(shape = RoundedCornerShape(size = 16.dp))
                        .background(color = MaterialTheme.colorScheme.primaryContainer)
                        .padding(all = 28.dp),
                    painter = painterResource(id = R.drawable.passmark_app_icon),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
                )
                Text(text = "login screen")
            }
        )
    }
}

@Composable
@MobilePreview
private fun LoginScreenPrev() {
    LoginScreen.Screen(modifier = Modifier.fillMaxSize())
}