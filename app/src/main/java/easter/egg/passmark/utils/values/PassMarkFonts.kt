package easter.egg.passmark.utils.values

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import easter.egg.passmark.R

object PassMarkFonts {
    object Display : SubStyleFonts {
        override val small: TextUnit = 36.sp
        override val medium: TextUnit = 45.sp
        override val large: TextUnit = 57.sp
    }

    object Headline : SubStyleFonts {
        override val small: TextUnit = 24.sp
        override val medium: TextUnit = 28.sp
        override val large: TextUnit = 32.sp
    }

    object Title : SubStyleFonts {
        override val small: TextUnit = 14.sp
        override val medium: TextUnit = 16.sp
        override val large: TextUnit = 22.sp
    }

    object Body : SubStyleFonts {
        override val small: TextUnit = 12.sp
        override val medium: TextUnit = 14.sp
        override val large: TextUnit = 16.sp
    }

    object Label : SubStyleFonts {
        override val small: TextUnit = 11.sp
        override val medium: TextUnit = 12.sp
        override val large: TextUnit = 14.sp
    }

    val font = FontFamily(
        Font(resId = R.font.roboto_thin, weight = FontWeight.Thin),
        Font(resId = R.font.roboto_regular, weight = FontWeight.Normal),
        Font(resId = R.font.roboto_medium, weight = FontWeight.Medium),
        Font(resId = R.font.roboto_semi_bold, weight = FontWeight.SemiBold),
        Font(resId = R.font.roboto_bold, weight = FontWeight.Bold),
        Font(resId = R.font.roboto_extra_bold, weight = FontWeight.ExtraBold),
    )
}

private interface SubStyleFonts {
    val small: TextUnit
    val medium: TextUnit
    val large: TextUnit
}
