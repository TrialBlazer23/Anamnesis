package com.anamnesis.feature.learn

import com.anamnesis.feature.learn.data.SoundUnits
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SoundUnitsTest {

    @Test
    fun lessonsExistExactlyForTheNonLetterUnits() {
        listOf(0, 4, 5, 6).forEach { assertNotNull("lesson for unit $it", SoundUnits.lesson(it)) }
        listOf(1, 2, 3, 7, 8).forEach { assertNull("no lesson for unit $it", SoundUnits.lesson(it)) }
    }

    @Test
    fun orientationIsReadOnly() {
        assertFalse(SoundUnits.lesson(0)!!.hasQuiz)
        assertTrue(SoundUnits.quiz(0, Random(1)).isEmpty())
    }

    @Test
    fun quizQuestionsAreWellFormed() {
        listOf(4, 5, 6).forEach { unit ->
            val deck = SoundUnits.quiz(unit, Random(7))
            assertTrue("unit $unit should have a real deck", deck.size >= 10)
            assertTrue(SoundUnits.lesson(unit)!!.hasQuiz)
            deck.forEach { q ->
                assertTrue("≥2 options", q.options.size >= 2)
                assertEquals("options distinct", q.options.distinct().size, q.options.size)
                assertTrue("valid answer index", q.correctIndex in q.options.indices)
                assertTrue("has explanation", q.explanation.isNotBlank())
            }
        }
    }

    @Test
    fun diphthongQuizCoversAllEightDiphthongs() {
        val deck = SoundUnits.quiz(4, Random(1))
        listOf("αι", "ει", "οι", "υι", "αυ", "ευ", "ηυ", "ου").forEach { d ->
            assertTrue("question for $d", deck.any { it.greek == d })
        }
        assertTrue(deck.first { it.greek == "ου" }.correct.contains("food"))
        assertTrue(deck.first { it.greek == "η" }.correct.contains("long"))
        assertTrue(deck.first { it.greek == "ο" }.correct.contains("short"))
    }

    @Test
    fun breathingAnswersFollowTheMark() {
        val deck = SoundUnits.quiz(5, Random(1))
        assertEquals("hodós", deck.first { it.greek == "ὁδός" }.correct)   // rough
        assertEquals("egṓ", deck.first { it.greek == "ἐγώ" }.correct)      // smooth
        assertEquals("húdōr", deck.first { it.greek == "ὕδωρ" }.correct)   // initial υ
        assertEquals("hoûtos", deck.first { it.greek == "οὗτος" }.correct) // diphthong
    }

    @Test
    fun accentAnswersFollowTheMark() {
        val deck = SoundUnits.quiz(6, Random(1))
        assertTrue(deck.first { it.greek == "λόγος" }.correct.startsWith("acute"))
        assertTrue(deck.first { it.greek == "καὶ …" }.correct.startsWith("grave"))
        assertTrue(deck.first { it.greek == "σῶμα" }.correct.startsWith("circumflex"))
        assertTrue(deck.first { it.greek == "νοῦς" }.correct.startsWith("circumflex"))
    }
}
