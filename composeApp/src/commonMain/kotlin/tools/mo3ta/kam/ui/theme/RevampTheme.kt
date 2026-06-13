package tools.mo3ta.kam.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember

/**
 * Provides the revamp fonts + text styles. Wrap the new Home/Results experience
 * in this. Uses the warm paper palette directly via [KamColors] (single theme).
 */
@Composable
fun RevampTheme(content: @Composable () -> Unit) {
    val fonts = rememberKamFonts()
    val text = remember(fonts) { KamTextStyles(fonts.display, fonts.body) }
    CompositionLocalProvider(
        LocalKamFonts provides fonts,
        LocalKamText provides text,
        content = content,
    )
}
