package com.kigz.javillasafarihub.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// =============================================================
// COLOR SCHEMES
// 70% savannah gold (backgrounds/surfaces — the dominant tone)
// 30% forest green (primary actions, accents, selected states)
// Animal-pattern imagery elsewhere in the app is untouched —
// this only changes surrounding chrome colors.
// =============================================================

private val DarkColorScheme = darkColorScheme(
    primary = LightSavannahGold,
    onPrimary = ForestGreen,
    primaryContainer = LightSavannahGold,
    onPrimaryContainer = ForestGreen,
    secondary = SavannahGold,
    onSecondary = ForestGreen,
    secondaryContainer = SavannahGold,
    onSecondaryContainer = ForestGreen,
    background = ForestGreen,
    onBackground = LightSavannahGold,
    surface = ForestGreen,
    onSurface = LightSavannahGold,
    surfaceVariant = SavannahGold,
    onSurfaceVariant = ForestGreen,
    surfaceContainerLowest = NightGreenLowest,
    surfaceContainerLow = NightGreenLow,
    surfaceContainer = ForestGreen,
    surfaceContainerHigh = NightGreenHigh,
    surfaceContainerHighest = NightGreenHighest,
    outline = SavannahGold,
    outlineVariant = NightOutlineVariant,
    inverseSurface = LightSavannahGold,
    inverseOnSurface = ForestGreen,
    inversePrimary = ForestGreen
)

private val LightColorScheme = lightColorScheme(
    primary = ForestGreen,
    onPrimary = LightSavannahGold,
    primaryContainer = ForestGreen,
    onPrimaryContainer = LightSavannahGold,
    secondary = SavannahGold,
    onSecondary = ForestGreen,
    secondaryContainer = LightSavannahGold,
    onSecondaryContainer = ForestGreen,
    background = LightSavannahGold,
    onBackground = ForestGreen,
    surface = LightSavannahGold,
    onSurface = ForestGreen,
    surfaceVariant = SavannahGold,
    onSurfaceVariant = ForestGreen,
    surfaceContainerLowest = IvorySavannah,
    surfaceContainerLow = LightSavannahGold,
    surfaceContainer = safari6,
    surfaceContainerHigh = safari5,
    surfaceContainerHighest = SavannahGold,
    outline = SavannahGold,
    outlineVariant = LightSavannahGold,
    inverseSurface = ForestGreen,
    inverseOnSurface = LightSavannahGold,
    inversePrimary = LightSavannahGold
)

@Composable
fun JavillaSafariHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is disabled for a consistent safari branding
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
