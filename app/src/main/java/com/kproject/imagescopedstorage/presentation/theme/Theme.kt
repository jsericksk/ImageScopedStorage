package com.kproject.imagescopedstorage.presentation.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = PrimaryDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    background = BackgroundDark,
)

@Composable
fun ImageScopedStorageTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = DarkColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}