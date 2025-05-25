package com.zodwick.capibara_android.ui.theme

import android.app.Activity
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
    primary = MoonGlow,
    secondary = TwilightPurple,
    tertiary = WarmAmber,
    background = NightSky,
    surface = DeepForest,
    primaryContainer = DeepForest,
    secondaryContainer = TwilightPurple,
    onPrimary = NightSky,
    onSecondary = MoonGlow,
    onBackground = MoonGlow,
    onSurface = MoonGlow,
    onPrimaryContainer = MoonGlow,
    onSecondaryContainer = MoonGlow
)

private val LightColorScheme = lightColorScheme(
    primary = ForestGreen,
    secondary = SkyBlue,
    tertiary = SunsetOrange,
    background = CloudWhite,
    surface = LavenderMist,
    primaryContainer = Color(0xFFE8F5E8),  // Much lighter green background
    secondaryContainer = LavenderMist,
    tertiaryContainer = Color(0xFFFFF4E6),  // Light orange background
    errorContainer = Color(0xFFFFE6E6),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF2D3748),
    onSurface = Color(0xFF2D3748),
    onPrimaryContainer = Color(0xFF2D5016),  // Dark green text on light background
    onSecondaryContainer = Color(0xFF2D3748),
    onTertiaryContainer = Color(0xFF8B4513),  // Dark brown text
    onErrorContainer = Color(0xFF8B0000)
)

@Composable
fun CapibaraAndroidTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color disabled to show our beautiful Ghibli colors
    dynamicColor: Boolean = false,
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