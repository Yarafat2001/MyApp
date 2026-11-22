package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

enum class AppLanguage(val code: String, val nativeLabel: String) {
  ENGLISH("EN", "English"),
  BENGALI("বাংলা", "বাংলা")
}

val LocalIsDarkTheme = compositionLocalOf { true }
val LocalAppLanguage = compositionLocalOf { AppLanguage.ENGLISH }

private val DarkColorScheme =
  darkColorScheme(
    primary = CyberCyan,
    onPrimary = Slate950,
    primaryContainer = DarkElevatedSurface,
    onPrimaryContainer = CyanBright,
    secondary = NeonViolet,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF2E1065),
    onSecondaryContainer = VioletBright,
    tertiary = TacticalEmerald,
    onTertiary = Slate950,
    tertiaryContainer = Color(0xFF064E3B),
    onTertiaryContainer = EmeraldLight,
    error = PanicRed,
    onError = Color.White,
    background = Slate950,
    onBackground = Slate100,
    surface = Slate900,
    onSurface = Slate100,
    surfaceVariant = DarkCardSurface,
    onSurfaceVariant = Slate200,
    outline = DarkCardBorder,
    outlineVariant = Slate700,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = Color(0xFF0096C7),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCAF0F8),
    onPrimaryContainer = Color(0xFF03045E),
    secondary = Color(0xFF6D28D9),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE9FE),
    onSecondaryContainer = Color(0xFF3B0764),
    tertiary = Color(0xFF059669),
    onTertiary = Color.White,
    error = Color(0xFFE11D48),
    onError = Color.White,
    background = LightBgTop,
    onBackground = LightTextPrimary,
    surface = Color.White,
    onSurface = LightTextPrimary,
    surfaceVariant = LightGlassSurfaceMedium,
    onSurfaceVariant = LightTextSecondary,
    outline = Color(0xFF93C5FD),
    outlineVariant = Color(0xFFCBD5E1),
  )

@Composable
fun GlassTextPrimary(): Color =
  if (LocalIsDarkTheme.current) Color.White else Color(0xFF071124)

@Composable
fun GlassTextSecondary(): Color =
  if (LocalIsDarkTheme.current) Color(0xFFF1F5F9) else Color(0xFF1E293B)

@Composable
fun GlassTextMuted(): Color =
  if (LocalIsDarkTheme.current) Color(0xFFCBD5E1) else Color(0xFF334155)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  appLanguage: AppLanguage = AppLanguage.ENGLISH,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  val typography = buildAppTypography(
    isBengali = appLanguage == AppLanguage.BENGALI,
    isDark = darkTheme
  )

  CompositionLocalProvider(
    LocalIsDarkTheme provides darkTheme,
    LocalAppLanguage provides appLanguage
  ) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = typography,
      content = content
    )
  }
}
