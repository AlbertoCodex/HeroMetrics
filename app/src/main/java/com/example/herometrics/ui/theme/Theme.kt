package com.example.herometrics.ui.theme

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

private val LightColorScheme = lightColorScheme(
    primary = OrangePrimary,
    onPrimary = OrangeOnPrimary,
    primaryContainer = OrangePrimaryContainer,
    onPrimaryContainer = OrangeOnPrimaryContainer,
    secondary = OrangeSecondary,
    onSecondary = OrangeOnSecondary,
    secondaryContainer = OrangeSecondaryContainer,
    onSecondaryContainer = OrangeOnSecondaryContainer,
    background = OrangeBackground,
    onBackground = OrangeOnBackground,
    surface = OrangeSurface,
    onSurface = OrangeOnSurface,
    error = OrangeError,
    onError = OrangeOnError,
    errorContainer = OrangeErrorContainer,
    onErrorContainer = OrangeOnErrorContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = OrangePrimary,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF3A2400),
    onPrimaryContainer = OrangePrimaryContainer,
    secondary = OrangeSecondary,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF442600),
    onSecondaryContainer = OrangeSecondaryContainer,
    background = Color(0xFF1E1E1E),
    onBackground = Color(0xFFF9F9F9),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFF9F9F9),
    error = OrangeError,
    onError = OrangeOnError,
    errorContainer = OrangeErrorContainer,
    onErrorContainer = OrangeOnErrorContainer
)

@Composable
fun HeroMetricsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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
        typography = AppTypography,
        content = content
    )
}