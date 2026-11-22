package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

// Modern Geometric Display Font with Anek Bangla fallback for mixed-script rendering
val OutfitFontFamily = FontFamily(
  Font(R.font.outfit, FontWeight.Normal),
  Font(R.font.outfit, FontWeight.Medium),
  Font(R.font.outfit, FontWeight.SemiBold),
  Font(R.font.outfit, FontWeight.Bold),
  Font(R.font.outfit, FontWeight.ExtraBold),
  Font(R.font.outfit, FontWeight.Black),
  Font(R.font.anek_bangla, FontWeight.Normal),
  Font(R.font.anek_bangla, FontWeight.Bold),
  Font(R.font.anek_bangla, FontWeight.ExtraBold)
)

// Technical Tactical Header Font with Anek Bangla fallback
val SpaceGroteskFontFamily = FontFamily(
  Font(R.font.space_grotesk, FontWeight.Normal),
  Font(R.font.space_grotesk, FontWeight.Medium),
  Font(R.font.space_grotesk, FontWeight.SemiBold),
  Font(R.font.space_grotesk, FontWeight.Bold),
  Font(R.font.anek_bangla, FontWeight.Normal),
  Font(R.font.anek_bangla, FontWeight.Bold)
)

// Keep Orbitron reference aliased to Outfit/SpaceGrotesk so any existing direct calls look sleek and modern
val OrbitronFontFamily = OutfitFontFamily

val PlusJakartaFontFamily = FontFamily(
  Font(R.font.plus_jakarta_sans, FontWeight.Normal),
  Font(R.font.plus_jakarta_sans, FontWeight.Medium),
  Font(R.font.plus_jakarta_sans, FontWeight.SemiBold),
  Font(R.font.plus_jakarta_sans, FontWeight.Bold),
  Font(R.font.plus_jakarta_sans, FontWeight.ExtraBold),
  Font(R.font.hind_siliguri, FontWeight.Normal),
  Font(R.font.hind_siliguri, FontWeight.Medium),
  Font(R.font.hind_siliguri, FontWeight.SemiBold),
  Font(R.font.hind_siliguri, FontWeight.Bold)
)

val JetBrainsMonoFontFamily = FontFamily(
  Font(R.font.jetbrains_mono, FontWeight.Normal),
  Font(R.font.jetbrains_mono, FontWeight.Medium),
  Font(R.font.jetbrains_mono, FontWeight.Bold),
  Font(R.font.jetbrains_mono, FontWeight.ExtraBold),
  Font(R.font.anek_bangla, FontWeight.Bold)
)

// Attractive Modern Bengali Font Pairing (Anek Bangla + Outfit Latin fallback)
val AnekBanglaFontFamily = FontFamily(
  Font(R.font.anek_bangla, FontWeight.Normal),
  Font(R.font.anek_bangla, FontWeight.Medium),
  Font(R.font.anek_bangla, FontWeight.SemiBold),
  Font(R.font.anek_bangla, FontWeight.Bold),
  Font(R.font.anek_bangla, FontWeight.ExtraBold),
  Font(R.font.outfit, FontWeight.Normal),
  Font(R.font.outfit, FontWeight.Bold),
  Font(R.font.outfit, FontWeight.ExtraBold)
)

val HindSiliguriFontFamily = FontFamily(
  Font(R.font.hind_siliguri, FontWeight.Normal),
  Font(R.font.hind_siliguri, FontWeight.Medium),
  Font(R.font.hind_siliguri, FontWeight.SemiBold),
  Font(R.font.hind_siliguri, FontWeight.Bold),
  Font(R.font.plus_jakarta_sans, FontWeight.Normal),
  Font(R.font.plus_jakarta_sans, FontWeight.SemiBold),
  Font(R.font.plus_jakarta_sans, FontWeight.Bold)
)

fun buildAppTypography(isBengali: Boolean, isDark: Boolean = true): Typography {
  val displayFamily = if (isBengali) AnekBanglaFontFamily else OutfitFontFamily
  val headlineFamily = if (isBengali) AnekBanglaFontFamily else SpaceGroteskFontFamily
  val bodyFamily = if (isBengali) HindSiliguriFontFamily else PlusJakartaFontFamily

  val crispHeaderShadow = if (isDark) {
    Shadow(
      color = Color(0x88000000),
      offset = Offset(0f, 2f),
      blurRadius = 6f
    )
  } else {
    Shadow(
      color = Color(0x22000000),
      offset = Offset(0f, 1f),
      blurRadius = 3f
    )
  }

  return Typography(
    displayLarge = TextStyle(
      fontFamily = displayFamily,
      fontWeight = FontWeight.Black,
      fontSize = 40.sp,
      lineHeight = 46.sp,
      letterSpacing = (-0.5).sp,
      shadow = crispHeaderShadow
    ),
    headlineLarge = TextStyle(
      fontFamily = headlineFamily,
      fontWeight = FontWeight.ExtraBold,
      fontSize = 32.sp,
      lineHeight = 38.sp,
      letterSpacing = (-0.4).sp,
      shadow = crispHeaderShadow
    ),
    headlineMedium = TextStyle(
      fontFamily = headlineFamily,
      fontWeight = FontWeight.ExtraBold,
      fontSize = 26.sp,
      lineHeight = 32.sp,
      letterSpacing = (-0.3).sp,
      shadow = crispHeaderShadow
    ),
    headlineSmall = TextStyle(
      fontFamily = headlineFamily,
      fontWeight = FontWeight.Bold,
      fontSize = 22.sp,
      lineHeight = 28.sp,
      letterSpacing = (-0.2).sp,
      shadow = crispHeaderShadow
    ),
    titleLarge = TextStyle(
      fontFamily = displayFamily,
      fontWeight = FontWeight.ExtraBold,
      fontSize = if (isBengali) 21.sp else 20.sp,
      lineHeight = 26.sp,
      letterSpacing = 0.sp,
      shadow = crispHeaderShadow
    ),
    titleMedium = TextStyle(
      fontFamily = displayFamily,
      fontWeight = FontWeight.Bold,
      fontSize = if (isBengali) 18.sp else 17.sp,
      lineHeight = 23.sp,
      letterSpacing = 0.1.sp,
      shadow = crispHeaderShadow
    ),
    titleSmall = TextStyle(
      fontFamily = displayFamily,
      fontWeight = FontWeight.Bold,
      fontSize = if (isBengali) 15.sp else 14.sp,
      lineHeight = 20.sp,
      letterSpacing = 0.15.sp
    ),
    bodyLarge = TextStyle(
      fontFamily = bodyFamily,
      fontWeight = FontWeight.SemiBold,
      fontSize = 16.sp,
      lineHeight = 23.sp,
      letterSpacing = 0.1.sp
    ),
    bodyMedium = TextStyle(
      fontFamily = bodyFamily,
      fontWeight = FontWeight.SemiBold,
      fontSize = 15.sp,
      lineHeight = 21.sp,
      letterSpacing = 0.1.sp
    ),
    bodySmall = TextStyle(
      fontFamily = bodyFamily,
      fontWeight = FontWeight.Medium,
      fontSize = 13.sp,
      lineHeight = 18.sp,
      letterSpacing = 0.15.sp
    ),
    labelLarge = TextStyle(
      fontFamily = displayFamily,
      fontWeight = FontWeight.Bold,
      fontSize = 14.sp,
      lineHeight = 19.sp,
      letterSpacing = 0.2.sp
    ),
    labelMedium = TextStyle(
      fontFamily = displayFamily,
      fontWeight = FontWeight.Bold,
      fontSize = 12.sp,
      lineHeight = 16.sp,
      letterSpacing = 0.2.sp
    ),
    labelSmall = TextStyle(
      fontFamily = bodyFamily,
      fontWeight = FontWeight.Bold,
      fontSize = 11.sp,
      lineHeight = 15.sp,
      letterSpacing = 0.2.sp
    )
  )
}

val Typography = buildAppTypography(isBengali = false, isDark = true)
