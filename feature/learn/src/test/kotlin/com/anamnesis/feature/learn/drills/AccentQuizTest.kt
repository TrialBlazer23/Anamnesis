package com.anamnesis.feature.learn.drills

import com.anamnesis.feature.learn.TEST_PACK
import com.anamnesis.feature.learn.model.Accent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class AccentQuizTest {

    @Test
    fun identifyDeckAsksEveryItemWithTheRightAnswer() {
        val deck = AccentQuiz.identifyDeck(TEST_PACK, Random(1))
        assertEquals(TEST_PACK.accentItems.size, deck.size)
        val byWord = TEST_PACK.accentItems.associateBy { it.word }
        deck.forEach { q ->
            assertEquals(setOf("acute", "grave", "circumflex"), q.options.map { it.id }.toSet())
            val expected = when (byWord.getValue(q.prompt).accent) {
                Accent.ACUTE -> "acute"
                Accent.GRAVE -> "grave"
                Accent.CIRCUMFLEX -> "circumflex"
            }
            assertEquals(expected, q.correctId)
            assertTrue(q.promptGreek)
        }
    }

    @Test
    fun identifyDeckCoversAllThreeAccents() {
        val deck = AccentQuiz.identifyDeck(TEST_PACK, Random(2))
        assertEquals(
            setOf("acute", "grave", "circumflex"),
            deck.map { it.correctId }.toSet(),
        )
    }

    @Test
    fun samePairDeckJudgesTenPairs() {
        val deck = AccentQuiz.samePairDeck(TEST_PACK, Random(3))
        assertEquals(AccentQuiz.PAIR_DECK_SIZE, deck.size)
        val byPrompt = TEST_PACK.accentPairs.associateBy { "${it.a}  ·  ${it.b}" }
        deck.forEach { q ->
            assertEquals(setOf("same", "different"), q.options.map { it.id }.toSet())
            val pair = byPrompt.getValue(q.prompt)
            assertEquals(if (pair.same) "same" else "different", q.correctId)
        }
        // Both outcomes appear, so the drill can't be passed by always answering one way.
        assertTrue(deck.map { it.correctId }.toSet().size == 2)
    }
}
