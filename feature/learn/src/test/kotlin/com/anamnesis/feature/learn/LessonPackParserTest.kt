package com.anamnesis.feature.learn

import com.anamnesis.feature.learn.model.Accent
import com.anamnesis.feature.learn.model.Breathing
import com.anamnesis.feature.learn.model.letterBatches
import com.anamnesis.feature.learn.pack.LessonPackParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Parses the committed `lessons.json` asset — the real thing the app ships. */
class LessonPackParserTest {

    @Test
    fun parsesTheBundledPack() {
        val pack = TEST_PACK
        assertEquals(LessonPackParser.SUPPORTED_SCHEMA_VERSION, pack.schemaVersion)
        assertEquals(9, pack.units.size)
        assertEquals((0..8).toList(), pack.units.map { it.number })
        assertEquals(24, pack.letters.size)
        assertEquals(11, pack.diphthongs.size)
        assertTrue(pack.minimalPairs.size >= 7)
        assertTrue(pack.words.size >= 40)
        assertTrue(pack.accentItems.size >= 18)
        assertTrue(pack.accentPairs.size >= 12)
    }

    @Test
    fun letterFieldsSurviveTheRoundTrip() {
        val alpha = TEST_PACK.letters.first()
        assertEquals("α", alpha.lower)
        assertEquals("Α", alpha.upper)
        assertEquals("alpha", alpha.nameTranslit)
        assertEquals(1, alpha.batch)
        assertEquals("snd_alpha", alpha.audioId)

        val eta = TEST_PACK.letters.first { it.lower == "η" }
        assertTrue(eta.falseFriend)
        assertEquals("H", eta.latinLookalike)
    }

    @Test
    fun batchesPartitionTheAlphabetWithUnitTitles() {
        val batches = TEST_PACK.letterBatches()
        assertEquals(listOf(1, 2, 3, 4), batches.map { it.number })
        assertEquals(24, batches.sumOf { it.letters.size })
        assertEquals("Familiar letters", batches[0].title)
        assertEquals("False friends", batches[2].title)
        assertEquals("Special cases", batches[3].title)
    }

    @Test
    fun diphthongsSplitProperAndImproper() {
        val (improper, proper) = TEST_PACK.diphthongs.partition { it.improper }
        assertEquals(8, proper.size)
        assertEquals(3, improper.size)
        assertTrue(proper.any { it.glyph == "αι" })
        assertTrue(improper.all { it.audioId != null })
    }

    @Test
    fun wordsCarryBreathingsAndAudioIds() {
        val words = TEST_PACK.words
        assertTrue(words.any { it.breathing == Breathing.ROUGH })
        assertTrue(words.any { it.breathing == Breathing.SMOOTH })
        assertTrue(words.any { it.breathing == Breathing.NONE })
        val ho = words.first { it.greek == "ὁ" }
        assertEquals(Breathing.ROUGH, ho.breathing)
        assertEquals("voc_1", ho.audioId)
    }

    @Test
    fun accentDataCoversAllThreeAccents() {
        val byAccent = TEST_PACK.accentItems.groupBy { it.accent }
        assertTrue(byAccent.getValue(Accent.ACUTE).size >= 6)
        assertTrue(byAccent.getValue(Accent.GRAVE).size >= 6)
        assertTrue(byAccent.getValue(Accent.CIRCUMFLEX).size >= 6)
        assertTrue(TEST_PACK.accentPairs.any { it.same })
        assertTrue(TEST_PACK.accentPairs.any { !it.same })
    }

    @Test
    fun unitsCarryCurriculumMetadata() {
        val unit4 = TEST_PACK.units[4]
        assertEquals("Vowel quantity & diphthongs", unit4.title)
        assertEquals(
            listOf("length-minimal-pair", "diphthong-to-sound", "long-or-short"),
            unit4.drills,
        )
        assertTrue(unit4.taught.isNotBlank())
        assertEquals(1, TEST_PACK.units[1].batch)
    }
}
