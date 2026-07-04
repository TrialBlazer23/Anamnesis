package com.anamnesis.feature.learn.drills

import com.anamnesis.feature.learn.TEST_PACK
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class LengthQuizTest {

    @Test
    fun lengthItemsSplitEveryLengthPairIntoShortAndLong() {
        val items = LengthQuiz.lengthItems(TEST_PACK)
        assertEquals(8, items.size) // ε/η, ο/ω + hidden-quantity ᾰ/ᾱ, ῐ/ῑ
        assertEquals(4, items.count { it.isLong })
        val eta = items.first { it.glyph == "η" }
        assertTrue(eta.isLong)
        assertTrue(eta.description.contains("long"))
        val epsilon = items.first { it.glyph == "ε" }
        assertFalse(epsilon.isLong)
    }

    @Test
    fun longOrShortDeckKeysAnswersToTheItems() {
        val deck = LengthQuiz.longOrShortDeck(TEST_PACK, Random(5))
        assertEquals(8, deck.size)
        deck.forEach { q ->
            assertEquals(setOf("long", "short"), q.options.map { it.id }.toSet())
            assertTrue(q.correctId == "long" || q.correctId == "short")
            assertTrue(q.promptGreek)
        }
        val longGlyphs = LengthQuiz.lengthItems(TEST_PACK).filter { it.isLong }.map { it.glyph }
        deck.filter { it.prompt in longGlyphs }.forEach { assertEquals("long", it.correctId) }
    }

    @Test
    fun minimalPairDeckAsksBothSidesOfEveryPair() {
        val deck = LengthQuiz.minimalPairDeck(TEST_PACK, Random(6))
        assertEquals(8, deck.size) // 4 length pairs × 2 sides
        deck.forEach { q ->
            assertEquals(2, q.options.size)
            assertTrue(q.options.any { it.id == q.correctId })
            assertTrue(q.options.all { it.greek })
        }
        // The prompt describes exactly the correct side ("short [e]" → ε).
        val shortE = deck.first { it.prompt.startsWith("short [e]") }
        assertEquals("ε", shortE.correctId)
        val longE = deck.first { it.prompt.startsWith("long [ɛː]") }
        assertEquals("η", longE.correctId)
    }

    @Test
    fun aspirationPairsStayOutOfTheLengthDrills() {
        val glyphs = LengthQuiz.lengthItems(TEST_PACK).map { it.glyph }
        assertTrue(listOf("π", "φ", "τ", "θ", "κ", "χ").none { it in glyphs })
    }
}
