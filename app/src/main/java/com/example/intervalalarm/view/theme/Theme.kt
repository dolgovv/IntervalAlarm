package com.example.intervalalarm.view.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import com.example.intervalalarm.view.theme.*

private val DarkColorPalette = darkColors(
    primary = DarkThemePrimary,
    primaryVariant = DarkThemePrimaryVariant,
    onPrimary = DarkThemeOnPrimary,

    secondary = DarkThemeSecondary,
    secondaryVariant = DarkThemeSecondaryVariant,
    onSecondary = DarkThemeOnSecondary,
)

private val LightColorPalette = lightColors(
    primary = LightThemePrimary,
    primaryVariant = LightThemePrimaryVariant,
    onPrimary = LightThemeOnPrimary,

    secondary = LightThemeSecondary,
    secondaryVariant = LightThemeSecondaryVariant,
    onSecondary = LightThemeOnSecondary,

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun IntervalAlarmTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}