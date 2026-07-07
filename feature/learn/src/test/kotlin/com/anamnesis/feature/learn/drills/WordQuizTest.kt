package com.anamnesis.feature.learn.drills

import com.anamnesis.feature.learn.TEST_PACK
import com.anamnesis.feature.learn.model.Breathing
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class WordQuizTest {

    @Test
    fun readWordDeckHasTenUniqueQuestionsWithTheAnswerPresent() {
        val deck = WordQuiz.readWordDeck(TEST_PACK, Random(1))
        assertEquals(WordQuiz.READ_DECK_SIZE, deck.size)
        assertEquals(deck.size, deck.map { it.prompt }.toSet().size)
        deck.forEach { q ->
            assertEquals(4, q.options.size)
            assertEquals(4, q.options.map { it.id }.toSet().size)
            assertTrue(q.options.any { it.id == q.correctId })
            assertTrue(q.promptGreek)
        }
    }

    @Test
    fun perturbationsToggleExactlyTheTaughtContrasts() {
        // rough breathing dropped
        assertTrue("ypó" !in WordQuiz.perturbations("hupó"))
        assertTrue("upó" in WordQuiz.perturbations("hupó"))
        // breathing added to a smooth word
        assertTrue("hen" in WordQuiz.perturbations("en"))
        // aspirate degraded: theós → teós
        assertTrue("teós" in WordQuiz.perturbations("theós"))
        // aspirate added: kaí → khaí
        assertTrue("khaí" in WordQuiz.perturbations("kaí"))
        // long vowel shortened: legō → lego (macron toggles)
        assertTrue("légo" in WordQuiz.perturbations("légō"))
        // initial rh loses its h
        assertTrue("rāidios" in WordQuiz.perturbations("rhāidios"))
        // never the identity
        TEST_PACK.words.forEach { w ->
            assertTrue(w.translit !in WordQuiz.perturbations(w.translit))
        }
    }

    @Test
    fun breathingDeckOnlyAsksMarkedWords() {
        val deck = WordQuiz.breathingDeck(TEST_PACK, Random(2))
        assertTrue(deck.isNotEmpty())
        val noneWords = TEST_PACK.words.filter { it.breathing == Breathing.NONE }.map { it.greek }
        deck.forEach { q ->
            assertTrue(q.prompt !in noneWords)
            assertEquals(setOf("rough", "smooth"), q.options.map { it.id }.toSet())
        }
        val byGreek = TEST_PACK.words.associateBy { it.greek }
        deck.forEach { q ->
            val expected =
                if (byGreek.getValue(q.prompt).breathing == Breathing.ROUGH) "rough" else "smooth"
            assertEquals(expected, q.correctId)
        }
    }

    @Test
    fun toGreekDeckShowsGreekOptions() {
        val deck = WordQuiz.toGreekDeck(TEST_PACK, Random(3))
        assertEquals(WordQuiz.READ_DECK_SIZE, deck.size)
        deck.forEach { q ->
            assertEquals(4, q.options.map { it.id }.toSet().size)
            assertTrue(q.options.all { it.greek })
            assertTrue(q.options.any { it.id == q.correctId })
        }
    }
}
