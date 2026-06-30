package com.anamnesis.core.domain.text

import org.junit.Assert.assertEquals
import org.junit.Test

class GreekTextTest {

    @Test
    fun stripsAccentsAndBreathings() {
        assertEquals("ανθρωποσ", GreekText.stripDiacritics("ἄνθρωπος"))
        assertEquals("ουηρου", GreekText.stripDiacritics("Οὐήρου"))
    }

    @Test
    fun foldsFinalSigma() {
        assertEquals(GreekText.stripDiacritics("λογοσ"), GreekText.stripDiacritics("λόγος"))
    }

    @Test
    fun wordKeyDropsSurroundingPunctuation() {
        assertEquals("λογοσ", GreekText.wordKey("λόγος,"))
        assertEquals("ανθρωποσ", GreekText.wordKey("«ἄνθρωπος»"))
        assertEquals("", GreekText.wordKey("·"))
    }
}
