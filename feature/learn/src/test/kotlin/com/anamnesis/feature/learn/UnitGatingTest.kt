package com.anamnesis.feature.learn

import com.anamnesis.core.domain.model.Card
import com.anamnesis.feature.learn.data.diphthongSeedCards
import com.anamnesis.feature.learn.data.letterSeedCards
import com.anamnesis.feature.learn.progress.UnitGating
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UnitGatingTest {

    @Test
    fun batchSessionsCompleteTheirUnitsAtNinetyPercent() {
        assertEquals(1, UnitGating.unitForSession(scopeBatch = 1, score = 7, total = 7))
        assertEquals(2, UnitGating.unitForSession(scopeBatch = 2, score = 9, total = 10))
        assertNull(UnitGating.unitForSession(scopeBatch = 2, score = 8, total = 10)) // 80% < 90%
        assertNull(UnitGating.unitForSession(scopeBatch = 1, score = 0, total = 0))  // empty deck
    }

    @Test
    fun mixedFullAlphabetSessionCompletesUnitThree() {
        assertEquals(3, UnitGating.unitForSession(scopeBatch = null, score = 22, total = 24))
        // Batch-3-only practice does NOT pass unit 3 (spec: mixed quiz required).
        assertNull(UnitGating.unitForSession(scopeBatch = 3, score = 6, total = 6))
        assertNull(UnitGating.unitForSession(scopeBatch = 4, score = 1, total = 1))
    }

    @Test
    fun unlockChainFollowsCompletion() {
        assertTrue(UnitGating.isUnlocked(0, emptySet()))
        assertTrue(UnitGating.isUnlocked(1, emptySet()))
        assertFalse(UnitGating.isUnlocked(2, emptySet()))
        assertTrue(UnitGating.isUnlocked(2, setOf(1)))
        assertFalse(UnitGating.isUnlocked(3, setOf(1)))
        assertTrue(UnitGating.isUnlocked(3, setOf(1, 2)))
        assertFalse(UnitGating.isUnlocked(4, setOf(1, 2)))
        assertTrue(UnitGating.isUnlocked(4, setOf(1, 2, 3)))
        // Units beyond the built range stay locked even with everything complete.
        val allBuilt = (0..UnitGating.HIGHEST_BUILT_UNIT).toSet()
        assertFalse(UnitGating.isUnlocked(UnitGating.HIGHEST_BUILT_UNIT + 1, allBuilt))
    }

    @Test
    fun unitFourNeedsBothGatesAtTheirOwnThresholds() {
        // Length passes at 80%: 8/10 is enough…
        assertEquals(UnitGating.GATE_LENGTH, UnitGating.drillPassed("long-or-short", 8, 10))
        assertEquals(UnitGating.GATE_LENGTH, UnitGating.drillPassed("length-minimal-pair", 8, 10))
        // …but diphthongs need 90%: 8/10 falls short, 9/10 passes.
        assertNull(UnitGating.drillPassed("diphthong-to-sound", 8, 10))
        assertEquals(UnitGating.GATE_DIPHTHONG, UnitGating.drillPassed("diphthong-to-sound", 9, 10))
        assertNull(UnitGating.drillPassed("long-or-short", 7, 10))
        assertNull(UnitGating.drillPassed("long-or-short", 0, 0))
        assertNull(UnitGating.drillPassed("not-a-drill", 10, 10))

        // Both gates are required; either alone is not enough.
        assertFalse(UnitGating.unitCompleteFromDrills(4, setOf(UnitGating.GATE_LENGTH)))
        assertFalse(UnitGating.unitCompleteFromDrills(4, setOf(UnitGating.GATE_DIPHTHONG)))
        assertTrue(
            UnitGating.unitCompleteFromDrills(
                4,
                setOf(UnitGating.GATE_LENGTH, UnitGating.GATE_DIPHTHONG),
            ),
        )
        // Units without drill gates never complete this way.
        assertFalse(UnitGating.unitCompleteFromDrills(3, setOf(UnitGating.GATE_LENGTH)))
    }

    @Test
    fun unitFiveIsGatedByTheReadingDrillAlone() {
        assertNull(UnitGating.drillPassed("read-the-word", 8, 10))
        assertEquals(UnitGating.GATE_READ_WORD, UnitGating.drillPassed("read-the-word", 9, 10))
        // The other unit-5 drills are practice, not gates.
        assertNull(UnitGating.drillPassed("breathing-identification", 12, 12))
        assertNull(UnitGating.drillPassed("transliteration", 10, 10))
        assertTrue(UnitGating.unitCompleteFromDrills(5, setOf(UnitGating.GATE_READ_WORD)))
        assertFalse(UnitGating.unitCompleteFromDrills(5, setOf(UnitGating.GATE_LENGTH)))
    }

    @Test
    fun unitSixNeedsBothAccentGatesAtEightyPercent() {
        assertEquals(
            UnitGating.GATE_IDENTIFY_ACCENT,
            UnitGating.drillPassed("identify-the-accent", 8, 10),
        )
        assertEquals(
            UnitGating.GATE_SAME_DIFFERENT,
            UnitGating.drillPassed("same-word-or-different", 8, 10),
        )
        assertNull(UnitGating.drillPassed("same-word-or-different", 7, 10))
        assertFalse(UnitGating.unitCompleteFromDrills(6, setOf(UnitGating.GATE_IDENTIFY_ACCENT)))
        assertTrue(
            UnitGating.unitCompleteFromDrills(
                6,
                setOf(UnitGating.GATE_IDENTIFY_ACCENT, UnitGating.GATE_SAME_DIFFERENT),
            ),
        )
    }

    @Test
    fun bothLengthDrillsFeedTheSameGate() {
        assertEquals(
            UnitGating.gateForDrill("long-or-short"),
            UnitGating.gateForDrill("length-minimal-pair"),
        )
        assertEquals(0.80, UnitGating.drillThreshold("long-or-short"), 1e-9)
        assertEquals(0.90, UnitGating.drillThreshold("diphthong-to-sound"), 1e-9)
    }

    @Test
    fun alphabetCompleteRequiresAllThreeUnits() {
        assertFalse(UnitGating.alphabetComplete(emptySet()))
        assertFalse(UnitGating.alphabetComplete(setOf(1, 2)))
        assertTrue(UnitGating.alphabetComplete(setOf(1, 2, 3)))
        assertTrue(UnitGating.alphabetComplete(setOf(0, 1, 2, 3)))
    }

    @Test
    fun letterSeedsCoverTheAlphabetInTheLettersDeck() {
        val cards = letterSeedCards(TEST_PACK)
        assertEquals(24, cards.size)
        assertTrue(cards.all { it.deck == Card.DECK_LETTERS })
        assertEquals(24, cards.map { it.lemma }.toSet().size)
        val eta = cards.first { it.lemma.startsWith("η") }
        assertTrue("eta card names its false friend", "not Latin" in eta.gloss)
        assertTrue(eta.partOfSpeech.contains("batch 3"))
    }

    @Test
    fun diphthongSeedsSitBetweenLettersAndVocabulary() {
        val letters = letterSeedCards(TEST_PACK)
        val diphthongs = diphthongSeedCards(TEST_PACK)
        assertEquals(11, diphthongs.size)
        assertTrue(diphthongs.all { it.deck == Card.DECK_DIPHTHONGS })
        assertEquals(11, diphthongs.map { it.lemma }.toSet().size)
        // No lemma collision with the letters deck (CardDao refreshes by lemma).
        assertTrue(diphthongs.map { it.lemma }.none { it in letters.map(Card::lemma) })
        val maxLetter = letters.maxOf { it.position }
        assertTrue(diphthongs.all { it.position in (maxLetter + 1)..999 })
    }
}
