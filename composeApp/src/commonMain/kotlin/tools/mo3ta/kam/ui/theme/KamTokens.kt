package tools.mo3ta.kam.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Design tokens for the Kam revamp — warm "paper" aesthetic, single light theme.
 * Colors ported from the design handoff (:root CSS vars). oklch values were
 * converted to sRGB hex; green uses the brand accent #2E8B57.
 */
object KamColors {
    val paper = Color(0xFFF4F0E6)
    val paper2 = Color(0xFFECE7DA)
    val card = Color(0xFFFCFAF5)
    val card2 = Color(0xFFF3EEE2)

    val ink = Color(0xFF1A1815)
    val ink2 = Color(0xFF6E695D)
    val ink3 = Color(0xFFA8A192)

    val line = Color(0x1A1A1815)   // ink @ 10%
    val line2 = Color(0x0F1A1815)  // ink @ 6%

    val green = Color(0xFF2E8B57)
    val greenSoft = green.copy(alpha = 0.12f)
    val terra = Color(0xFFC45E39)
    val terraSoft = terra.copy(alpha = 0.12f)

    // Lifestyle tier colors, Basic -> Luxury
    val tier = listOf(
        Color(0xFF8B7E69),
        Color(0xFF749156),
        green,
        Color(0xFFC1983A),
    )

    // Cream text used on dark surfaces
    val onDark = Color(0xFFF4F0E6)

    // Hero / breakeven dark card gradient
    val heroGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF211E19), Color(0xFF14120F)),
    )

    // Dark salary field gradient
    val salaryGradient = Brush.verticalGradient(
        colors = listOf(ink, Color(0xFF26231E)),
    )

    // Hero footer "buying power" pills
    val pillUpBg = Color(0x38D97A50)   // rgba(217,122,80,.22)
    val pillUpFg = Color(0xFFF0A985)
    val pillDownBg = Color(0x3378B478) // rgba(120,180,120,.2)
    val pillDownFg = Color(0xFF8FD49B)

    val scrim = Color(0x6B14120F) // rgba(20,18,15,.42)
}

object KamDims {
    val screenH = 20.dp
    val topPad = 32.dp        // below status bar (system insets add the rest)
    val bottomPad = 46.dp
    val cardRadius = 22.dp
    val cardRadiusLg = 26.dp
    val fieldRadius = 18.dp
    val cardGap = 14.dp

    val cardShape = RoundedCornerShape(cardRadius)
    val cardShapeLg = RoundedCornerShape(cardRadiusLg)
    val fieldShape = RoundedCornerShape(fieldRadius)
    val sheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
}

/** Shared letter-spacing-ish constants reused across screens. */
object KamType {
    val labelLetterSpacing = 1.3.sp
}
