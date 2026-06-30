package com.anamnesis.feature.reader

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

/**
 * Gentium Plus (SIL OFL 1.1) — the bundled polytonic Greek font. Compose text
 * uses HarfBuzz, which applies the font's `ccmp`/`mark` tables, so feed it
 * NFC-normalized text (the pipeline already emits NFC).
 *
 * Bundled version is 6.101 (from the SIL/Google Fonts mirror); the design doc
 * targets 7.000, which only adds family unification — 6.x already has complete
 * polytonic coverage. Upgrade when SIL's site is reachable. See
 * `assets/licenses/gentium_ofl.txt`.
 */
val GentiumPlus = FontFamily(
    Font(R.font.gentium_plus_regular, FontWeight.Normal),
    Font(R.font.gentium_plus_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.gentium_plus_bold, FontWeight.Bold),
    Font(R.font.gentium_plus_bold_italic, FontWeight.Bold, FontStyle.Italic),
)
