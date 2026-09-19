package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = ChampagneGold,
    onPrimary = Color(0xFF0C0E12),
    primaryContainer = ChampagneGoldGlow,
    onPrimaryContainer = ChampagneGoldLight,
    secondary = ButtonActionTextDark,
    onSecondary = Color(0xFF0C0E12),
    surface = ObsidianSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = ObsidianSurfaceHigh,
    onSurfaceVariant = TextSecondaryDark,
    background = ObsidianBackground,
    onBackground = TextPrimaryDark,
    outline = ObsidianBorder,
    error = TextErrorDark,
    onError = Color.White
  )

private val LightColorScheme =
  lightColorScheme(
    primary = ChampagneGoldDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFF4DF),
    onPrimaryContainer = ChampagneGoldDark,
    secondary = ButtonActionTextLight,
    onSecondary = Color.White,
    surface = IvorySurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = IvorySurfaceHigh,
    onSurfaceVariant = TextSecondaryLight,
    background = IvoryBackground,
    onBackground = TextPrimaryLight,
    outline = IvoryBorder,
    error = Color(0xFFD32F2F),
    onError = Color.White
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

