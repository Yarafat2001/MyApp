package com.example.ui.theme

import androidx.compose.runtime.Composable

/**
 * Bilingual English / Bengali (বাংলা) string selector that automatically
 * reacts to LocalAppLanguage.current across the entire UI.
 */
@Composable
fun tr(en: String, bn: String): String {
  return if (LocalAppLanguage.current == AppLanguage.BENGALI) bn else en
}
