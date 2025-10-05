package com.gsnamespace.atforecast.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * ATForecast Light Color Scheme
 * Based on the original app's blue theme
 */
private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = androidx.compose.ui.graphics.Color(0xFF7D5260),
    onTertiary = androidx.compose.ui.graphics.Color.White,
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = androidx.compose.ui.graphics.Color(0xFFF9DEDC),
    onErrorContainer = androidx.compose.ui.graphics.Color(0xFF410E0B),
    // Light grey background with white elevated cards
    background = androidx.compose.ui.graphics.Color(0xFFF5F5F5),
    onBackground = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
    surfaceVariant = androidx.compose.ui.graphics.Color.White,
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF49454F),
    surfaceContainer = androidx.compose.ui.graphics.Color.White,
    surfaceContainerHigh = androidx.compose.ui.graphics.Color.White,
    surfaceContainerHighest = androidx.compose.ui.graphics.Color.White,
    surfaceContainerLow = androidx.compose.ui.graphics.Color.White,
    surfaceContainerLowest = androidx.compose.ui.graphics.Color.White,
    inverseSurface = androidx.compose.ui.graphics.Color(0xFF313033),
    inverseOnSurface = androidx.compose.ui.graphics.Color(0xFFF4EFF4),
    outline = androidx.compose.ui.graphics.Color(0xFF79747E),
    outlineVariant = androidx.compose.ui.graphics.Color(0xFFCAC4D0)
)

/**
 * ATForecast Dark Color Scheme
 */
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDarkTheme,
    onPrimary = OnPrimaryDarkTheme,
    primaryContainer = PrimaryContainerDarkTheme,
    onPrimaryContainer = OnPrimaryContainerDarkTheme,
    secondary = SecondaryDarkTheme,
    onSecondary = OnSecondaryDarkTheme,
    secondaryContainer = SecondaryContainerDarkTheme,
    onSecondaryContainer = OnSecondaryContainerDarkTheme,
    tertiary = androidx.compose.ui.graphics.Color(0xFFEFB8C8),
    onTertiary = androidx.compose.ui.graphics.Color(0xFF492532),
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = androidx.compose.ui.graphics.Color(0xFF93000A),
    onErrorContainer = androidx.compose.ui.graphics.Color(0xFFFFDAD6),
    background = androidx.compose.ui.graphics.Color(0xFF121212),
    onBackground = androidx.compose.ui.graphics.Color(0xFFE8E8E8),
    surface = androidx.compose.ui.graphics.Color(0xFF1E1E1E),
    onSurface = androidx.compose.ui.graphics.Color(0xFFE8E8E8),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFF1E1E1E),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFFCAC4D0),
    outline = androidx.compose.ui.graphics.Color(0xFF938F99),
    outlineVariant = androidx.compose.ui.graphics.Color(0xFF49454F)
)

/**
 * ATForecast theme with fixed blue color scheme.
 * Dynamic color is disabled to maintain brand consistency.
 */
@Composable
fun ATForecastTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
