package tools.mo3ta.kam.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kam.composeapp.generated.resources.Res
import kam.composeapp.generated.resources.hanken_grotesk_variable
import kam.composeapp.generated.resources.space_grotesk_variable
import org.jetbrains.compose.resources.Font

/** Display = Space Grotesk, Body = Hanken Grotesk (both variable fonts). */
class KamFonts(val display: FontFamily, val body: FontFamily)

@Composable
fun rememberKamFonts(): KamFonts {
    val display = FontFamily(
        Font(Res.font.space_grotesk_variable, FontWeight.Medium),
        Font(Res.font.space_grotesk_variable, FontWeight.SemiBold),
        Font(Res.font.space_grotesk_variable, FontWeight.Bold),
    )
    val body = FontFamily(
        Font(Res.font.hanken_grotesk_variable, FontWeight.Normal),
        Font(Res.font.hanken_grotesk_variable, FontWeight.Medium),
        Font(Res.font.hanken_grotesk_variable, FontWeight.SemiBold),
        Font(Res.font.hanken_grotesk_variable, FontWeight.Bold),
    )
    return KamFonts(display, body)
}

val LocalKamFonts = staticCompositionLocalOf<KamFonts> {
    error("KamFonts not provided — wrap content in RevampTheme")
}

private const val TNUM = "tnum"

/**
 * Central text styles matching the design handoff type scale. Screen code should
 * use these via [LocalKamText] rather than re-deriving sizes, so weights/spacing
 * stay consistent across the parallel-built screens.
 */
class KamTextStyles(d: FontFamily, b: FontFamily) {
    // Home
    val wordmark = TextStyle(fontFamily = d, fontWeight = FontWeight.Bold, fontSize = 23.sp, letterSpacing = (-0.5).sp)
    val heroH1 = TextStyle(fontFamily = d, fontWeight = FontWeight.SemiBold, fontSize = 33.sp, lineHeight = 36.sp, letterSpacing = (-1).sp)
    val heroSub = TextStyle(fontFamily = b, fontWeight = FontWeight.Normal, fontSize = 15.5.sp, lineHeight = 22.5.sp)
    val cityLabel = TextStyle(fontFamily = b, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, letterSpacing = 1.4.sp)
    val cityName = TextStyle(fontFamily = d, fontWeight = FontWeight.SemiBold, fontSize = 21.sp, letterSpacing = (-0.4).sp)
    val cityCountry = TextStyle(fontFamily = b, fontWeight = FontWeight.Normal, fontSize = 13.sp)
    val sfLabel = TextStyle(fontFamily = b, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, letterSpacing = 1.2.sp)
    val sfAmount = TextStyle(fontFamily = d, fontWeight = FontWeight.SemiBold, fontSize = 30.sp, letterSpacing = (-0.6).sp, fontFeatureSettings = TNUM)
    val sfCur = TextStyle(fontFamily = b, fontWeight = FontWeight.Medium, fontSize = 13.sp)
    val liveStrip = TextStyle(fontFamily = b, fontWeight = FontWeight.Normal, fontSize = 15.sp)
    val cta = TextStyle(fontFamily = d, fontWeight = FontWeight.SemiBold, fontSize = 16.5.sp, letterSpacing = (-0.2).sp)
    val offerAdd = TextStyle(fontFamily = b, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    val ofValue = TextStyle(fontFamily = d, fontWeight = FontWeight.SemiBold, fontSize = 24.sp, letterSpacing = (-0.5).sp, fontFeatureSettings = TNUM)

    // Results
    val routeMini = TextStyle(fontFamily = d, fontWeight = FontWeight.SemiBold, fontSize = 14.5.sp, letterSpacing = (-0.2).sp)
    val verdict = TextStyle(fontFamily = d, fontWeight = FontWeight.SemiBold, fontSize = 27.sp, lineHeight = 32.sp, letterSpacing = (-0.7).sp)
    val cardHead = TextStyle(fontFamily = b, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, letterSpacing = 1.3.sp)
    val cardBadge = TextStyle(fontFamily = b, fontWeight = FontWeight.Bold, fontSize = 11.sp)
    val hcTag = TextStyle(fontFamily = b, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, letterSpacing = 1.6.sp)
    val hcAmount = TextStyle(fontFamily = d, fontWeight = FontWeight.SemiBold, fontSize = 46.sp, lineHeight = 46.sp, letterSpacing = (-1.5).sp, fontFeatureSettings = TNUM)
    val hcPer = TextStyle(fontFamily = d, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
    val hcDesc = TextStyle(fontFamily = b, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 21.sp)
    val hcfK = TextStyle(fontFamily = b, fontWeight = FontWeight.Medium, fontSize = 11.5.sp)
    val hcfV = TextStyle(fontFamily = d, fontWeight = FontWeight.SemiBold, fontSize = 19.sp, fontFeatureSettings = TNUM)
    val hcfPill = TextStyle(fontFamily = b, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    val ocAmount = TextStyle(fontFamily = d, fontWeight = FontWeight.SemiBold, fontSize = 36.sp, lineHeight = 36.sp, letterSpacing = (-1).sp, fontFeatureSettings = TNUM)
    val ocDesc = TextStyle(fontFamily = b, fontWeight = FontWeight.Normal, fontSize = 13.5.sp, lineHeight = 21.sp)
    val powerHeadline = TextStyle(fontFamily = d, fontWeight = FontWeight.Medium, fontSize = 18.sp, letterSpacing = (-0.3).sp)
    val powerLabels = TextStyle(fontFamily = b, fontWeight = FontWeight.Medium, fontSize = 11.5.sp)
    val tierName = TextStyle(fontFamily = d, fontWeight = FontWeight.Bold, fontSize = 24.sp, letterSpacing = (-0.5).sp)
    val tierBlurb = TextStyle(fontFamily = b, fontWeight = FontWeight.Normal, fontSize = 13.5.sp, lineHeight = 19.5.sp)
    val tierSegLabel = TextStyle(fontFamily = b, fontWeight = FontWeight.SemiBold, fontSize = 10.5.sp)
    val bdLabel = TextStyle(fontFamily = b, fontWeight = FontWeight.Medium, fontSize = 13.sp)
    val bdDiff = TextStyle(fontFamily = d, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, fontFeatureSettings = TNUM)
    val legend = TextStyle(fontFamily = b, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
    val footNote = TextStyle(fontFamily = b, fontWeight = FontWeight.Normal, fontSize = 11.5.sp, lineHeight = 17.sp)

    // Sheets
    val sheetTitle = TextStyle(fontFamily = d, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, letterSpacing = (-0.4).sp)
    val searchInput = TextStyle(fontFamily = b, fontWeight = FontWeight.Normal, fontSize = 15.5.sp)
    val clrName = TextStyle(fontFamily = d, fontWeight = FontWeight.SemiBold, fontSize = 16.5.sp, letterSpacing = (-0.3).sp)
    val clrCountry = TextStyle(fontFamily = b, fontWeight = FontWeight.Normal, fontSize = 12.5.sp)
    val clrIndex = TextStyle(fontFamily = d, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, fontFeatureSettings = TNUM)
    val kpSym = TextStyle(fontFamily = d, fontWeight = FontWeight.Medium, fontSize = 26.sp)
    val kpNum = TextStyle(fontFamily = d, fontWeight = FontWeight.SemiBold, fontSize = 48.sp, letterSpacing = (-1.5).sp, fontFeatureSettings = TNUM)
    val kpCur = TextStyle(fontFamily = b, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    val kpKey = TextStyle(fontFamily = d, fontWeight = FontWeight.SemiBold, fontSize = 24.sp)
    val regionChip = TextStyle(fontFamily = d, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp)
}

val LocalKamText = staticCompositionLocalOf<KamTextStyles> {
    error("KamTextStyles not provided — wrap content in RevampTheme")
}
