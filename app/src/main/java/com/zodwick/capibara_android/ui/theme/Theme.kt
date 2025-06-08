package com.zodwick.capibara_android.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Removed DarkColorScheme since we're forcing light mode only

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
fun SanctuaryTheme(
    darkTheme: Boolean = false, // Force light mode always
    // Dynamic color disabled to show our beautiful Ghibli colors
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Always use light color scheme regardless of system settings
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            dynamicLightColorScheme(context) // Force light even for dynamic colors
        }
        else -> LightColorScheme // Always use light scheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}