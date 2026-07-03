package com.anamnesis.feature.learn

import com.anamnesis.core.domain.model.Card
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
        // Units beyond the built range stay locked even with everything complete.
        assertFalse(UnitGating.isUnlocked(4, setOf(0, 1, 2, 3)))
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
        val cards = letterSeedCards()
        assertEquals(24, cards.size)
        assertTrue(cards.all { it.deck == Card.DECK_LETTERS })
        assertEquals(24, cards.map { it.lemma }.toSet().size)
        val eta = cards.first { it.lemma.startsWith("η") }
        assertTrue("eta card names its false friend", "not Latin" in eta.gloss)
        assertTrue(eta.partOfSpeech.contains("batch 3"))
    }
}
