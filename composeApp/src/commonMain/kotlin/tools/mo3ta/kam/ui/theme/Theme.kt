package tools.mo3ta.kam.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Teal200 = Color(0xFF80CBC4)
private val Teal400 = Color(0xFF26A69A)
private val Teal700 = Color(0xFF00796B)
private val Cyan200 = Color(0xFF80DEEA)
private val Cyan700 = Color(0xFF0097A7)
private val DarkSurface = Color(0xFF121218)
private val DarkBackground = Color(0xFF0D0D12)
private val DarkSurfaceVariant = Color(0xFF1E1E28)
private val DarkOnSurface = Color(0xFFE6E1E5)
private val LightBackground = Color(0xFFF8FAFB)
private val LightSurface = Color(0xFFFFFFFF)
private val LightSurfaceVariant = Color(0xFFE8F0F2)
private val ErrorColor = Color(0xFFEF5350)
private val GreenAccent = Color(0xFF66BB6A)

private val DarkColorScheme = darkColorScheme(
    primary = Teal400,
    onPrimary = Color.Black,
    primaryContainer = Teal700,
    onPrimaryContainer = Teal200,
    secondary = Cyan200,
    onSecondary = Color.Black,
    secondaryContainer = Cyan700,
    onSecondaryContainer = Cyan200,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFCAC4D0),
    error = ErrorColor,
    onError = Color.White,
    tertiary = GreenAccent,
    onTertiary = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = Teal700,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB2DFDB),
    onPrimaryContainer = Teal700,
    secondary = Cyan700,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB2EBF2),
    onSecondaryContainer = Cyan700,
    background = LightBackground,
    onBackground = Color(0xFF1C1B1F),
    surface = LightSurface,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF49454F),
    error = ErrorColor,
    onError = Color.White,
    tertiary = GreenAccent,
    onTertiary = Color.Black
)

@Composable
fun KamTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
