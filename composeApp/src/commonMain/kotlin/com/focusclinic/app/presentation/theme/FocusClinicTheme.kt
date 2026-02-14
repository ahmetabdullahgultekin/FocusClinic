package com.focusclinic.app.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF3F51B5),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC5CAE9),
    onPrimaryContainer = Color(0xFF1A237E),
    secondary = Color(0xFFFFC107),
    onSecondary = Color(0xFF1C1B1F),
    secondaryContainer = Color(0xFFFFF8E1),
    onSecondaryContainer = Color(0xFF4E342E),
    tertiary = Color(0xFF7E57C2),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE1BEE7),
    onTertiaryContainer = Color(0xFF4A148C),
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE8EAF6),
    onSurfaceVariant = Color(0xFF49454F),
    error = Color(0xFFB3261E),
    onError = Color.White,
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF9FA8DA),
    onPrimary = Color(0xFF1A237E),
    primaryContainer = Color(0xFF303F9F),
    onPrimaryContainer = Color(0xFFC5CAE9),
    secondary = Color(0xFFFFD54F),
    onSecondary = Color(0xFF1C1B1F),
    secondaryContainer = Color(0xFFF57F17),
    onSecondaryContainer = Color(0xFFFFF8E1),
    tertiary = Color(0xFFB39DDB),
    onTertiary = Color(0xFF4A148C),
    tertiaryContainer = Color(0xFF512DA8),
    onTertiaryContainer = Color(0xFFE1BEE7),
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
)

@Composable
fun IradeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        content = content,
    )
}
