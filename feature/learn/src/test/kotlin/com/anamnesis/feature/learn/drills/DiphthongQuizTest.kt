package com.anamnesis.feature.learn.drills

import com.anamnesis.feature.learn.TEST_PACK
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class DiphthongQuizTest {

    @Test
    fun deckAsksEveryDiphthongBothWays() {
        val deck = DiphthongQuiz.deck(TEST_PACK, Random(1))
        assertEquals(22, deck.size) // 11 diphthongs × (glyph→IPA + IPA→glyph)
        val asked = deck.map { it.correctId }
        TEST_PACK.diphthongs.forEach { d ->
            assertEquals(2, asked.count { it == d.glyph })
        }
    }

    @Test
    fun everyQuestionContainsItsAnswerWithoutDuplicates() {
        val deck = DiphthongQuiz.deck(TEST_PACK, Random(2))
        deck.forEach { q ->
            assertEquals(4, q.options.size)
            assertEquals(4, q.options.map { it.id }.toSet().size)
            assertTrue(q.options.any { it.id == q.correctId })
        }
    }

    @Test
    fun improperDiphthongsCarryTheSubscriptCaption() {
        val deck = DiphthongQuiz.deck(TEST_PACK, Random(3))
        val improperGlyphs = TEST_PACK.diphthongs.filter { it.improper }.map { it.glyph }
        val glyphPrompts = deck.filter { it.promptGreek && it.prompt in improperGlyphs }
        assertTrue(glyphPrompts.isNotEmpty())
        assertTrue(glyphPrompts.all { it.promptCaption?.contains("iota subscript") == true })
    }

    @Test
    fun distractorsPreferSameFirstLetter() {
        // αι has three same-first-letter neighbours (αυ, ᾳ …) — with tiered
        // distractors at least one must appear among its options.
        val deck = DiphthongQuiz.deck(TEST_PACK, Random(4))
        val alphaQuestions = deck.filter { it.correctId == "αι" }
        alphaQuestions.forEach { q ->
            val others = q.options.map { it.id }.filter { it != "αι" }
            assertTrue(
                "expected an α-initial distractor in $others",
                others.any { it.startsWith("α") || it == "ᾳ" },
            )
        }
    }
}
