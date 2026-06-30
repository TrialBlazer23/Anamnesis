package com.anamnesis.feature.learn

import com.anamnesis.feature.learn.data.ALPHABET
import com.anamnesis.feature.learn.data.ALPHABET_BATCHES
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class AlphabetQuizTest {

    @Test
    fun alphabetHas24DistinctLetters() {
        assertEquals(24, ALPHABET.size)
        assertEquals(24, ALPHABET.map { it.lower }.toSet().size)
    }

    @Test
    fun batchesPartitionTheAlphabet() {
        assertEquals(listOf(1, 2, 3, 4), ALPHABET_BATCHES.map { it.number })
        assertEquals(mapOf(1 to 7, 2 to 10, 3 to 6, 4 to 1), ALPHABET_BATCHES.associate { it.number to it.letters.size })
        assertEquals(24, ALPHABET_BATCHES.sumOf { it.letters.size })
    }

    @Test
    fun falseFriendsAreTheKnownSet() {
        val ff = ALPHABET.filter { it.falseFriend }.map { it.lower }.toSet()
        assertEquals(setOf("ζ", "η", "ν", "ρ", "υ", "χ"), ff)
    }

    @Test
    fun questionContainsAnswerAndRequestedOptionCount() {
        val random = Random(42)
        val answer = ALPHABET.first { it.lower == "λ" }
        val q = AlphabetQuiz.question(answer, ALPHABET, optionCount = 4, random = random)
        assertEquals(4, q.options.size)
        assertTrue(q.options.any { it.lower == "λ" })
        assertTrue(q.options.all { opt -> ALPHABET.any { it.lower == opt.lower } })
        assertEquals(4, q.options.map { it.lower }.toSet().size) // no duplicate options
    }

    @Test
    fun deckIsAPermutationOfThePool() {
        val deck = AlphabetQuiz.deck(ALPHABET, Random(7))
        assertEquals(ALPHABET.size, deck.size)
        assertEquals(ALPHABET.map { it.lower }.toSet(), deck.map { it.lower }.toSet())
    }
}
