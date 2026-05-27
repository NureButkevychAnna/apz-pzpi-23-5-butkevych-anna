package com.example.radiation.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = PrimaryBlueLight,
    primaryContainer = SurfaceVariant,
    onPrimaryContainer = OnBackground,
    secondary = SecondaryBlue,
    onSecondary = PrimaryBlueLight,
    tertiary = CriticalPurple,
    onTertiary = CriticalPurpleLight,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnBackground,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceMuted,
    outline = BorderColor,
    error = DangerRed,
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = PrimaryBlueLight,
    primaryContainer = SurfaceVariant,
    onPrimaryContainer = OnBackground,
    secondary = SecondaryBlue,
    onSecondary = PrimaryBlueLight,
    tertiary = CriticalPurple,
    onTertiary = CriticalPurpleLight,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnBackground,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceMuted,
    outline = BorderColor,
    error = DangerRed,
)

@Composable
fun RadiationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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