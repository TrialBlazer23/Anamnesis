package com.anamnesis.core.domain.text

import java.text.Normalizer

/** Greek text helpers shared by lookup and search. */
object GreekText {

    /**
     * Accent-insensitive key: decompose (NFD), drop combining marks, lowercase,
     * and fold final sigma to medial. Matches the pipeline's `strip_diacritics`
     * and the content pack's FTS tokenizer so on-device matching aligns.
     */
    fun stripDiacritics(text: String): String {
        val decomposed = Normalizer.normalize(text, Normalizer.Form.NFD)
        val sb = StringBuilder(decomposed.length)
        for (ch in decomposed) {
            if (!isCombining(ch)) sb.append(ch)
        }
        return sb.toString().lowercase().replace('ς', 'σ')
    }

    /** Keep only letters (drops punctuation/markers around a tapped word). */
    fun wordKey(token: String): String =
        stripDiacritics(token.filter { it.isLetter() })

    private fun isCombining(ch: Char): Boolean = when (Character.getType(ch)) {
        Character.NON_SPACING_MARK.toInt(),
        Character.COMBINING_SPACING_MARK.toInt(),
        Character.ENCLOSING_MARK.toInt(),
        -> true
        else -> false
    }
}
