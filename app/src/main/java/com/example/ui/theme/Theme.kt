package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Emerald400,
    onPrimary = Emerald900,
    primaryContainer = Emerald800,
    onPrimaryContainer = Emerald100,
    secondary = GoldPrimary,
    onSecondary = Emerald900,
    secondaryContainer = Emerald900,
    onSecondaryContainer = GoldLight,
    tertiary = GoldLight,
    onTertiary = Emerald900,
    background = DarkBackground,
    onBackground = TextLightPrimary,
    surface = DarkSurface,
    onSurface = TextLightPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextLightSecondary,
    outline = Color(0xFF284841)
)

private val LightColorScheme = lightColorScheme(
    primary = Emerald700,
    onPrimary = Color.White,
    primaryContainer = Emerald100,
    onPrimaryContainer = Emerald900,
    secondary = GoldDark,
    onSecondary = Color.White,
    secondaryContainer = GoldContainer,
    onSecondaryContainer = GoldOnContainer,
    tertiary = GoldPrimary,
    onTertiary = Emerald900,
    background = WarmBackground,
    onBackground = TextDarkPrimary,
    surface = WarmSurface,
    onSurface = TextDarkPrimary,
    surfaceVariant = WarmSurfaceVariant,
    onSurfaceVariant = TextDarkSecondary,
    outline = Color(0xFFD5DFD9)
)

@Composable
fun VoxoraQuranTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Preserve brand identity by default
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
