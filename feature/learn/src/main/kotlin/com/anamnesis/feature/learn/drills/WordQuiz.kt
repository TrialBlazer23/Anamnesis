package com.anamnesis.feature.learn.drills

import com.anamnesis.feature.learn.model.Breathing
import com.anamnesis.feature.learn.model.LessonPack
import com.anamnesis.feature.learn.model.WordLesson
import kotlin.random.Random

/**
 * Unit-5 word drills over the curated DCC core words (visual-first).
 *
 * The read-the-word distractors are *perturbations* of the correct
 * transliteration — the breathing toggled, an aspirate degraded, a long vowel
 * shortened — so a wrong answer is wrong precisely in the skill this unit
 * teaches, not in some unrelated letter.
 */
object WordQuiz {

    const val READ_DECK_SIZE = 10
    private const val OPTION_COUNT = 4
    private const val BREATHING_DECK_SIZE = 12

    /** Greek word → pick its transliteration. This deck gates the unit. */
    fun readWordDeck(
        pack: LessonPack,
        random: Random,
        size: Int = READ_DECK_SIZE,
    ): List<DrillQuestion> =
        pack.words.shuffled(random).take(size).map { word ->
            val perturbed = perturbations(word.translit).shuffled(random)
            val fallback = pack.words.map { it.translit }
                .filter { it != word.translit && it !in perturbed }
                .shuffled(random)
            val distractors = (perturbed + fallback).distinct().take(OPTION_COUNT - 1)
            DrillQuestion(
                promptTitle = "How do you read",
                prompt = word.greek,
                promptGreek = true,
                options = (distractors + word.translit)
                    .map { DrillOption(id = it, label = it) }
                    .shuffled(random),
                correctId = word.translit,
                explanation = explanation(word),
            )
        }

    /** Vowel/ρ-initial word → rough (leading [h]) or smooth (nothing)? */
    fun breathingDeck(pack: LessonPack, random: Random): List<DrillQuestion> =
        pack.words.filter { it.breathing != Breathing.NONE }
            .shuffled(random)
            .take(BREATHING_DECK_SIZE)
            .map { word ->
                DrillQuestion(
                    promptTitle = "Rough or smooth breathing?",
                    prompt = word.greek,
                    promptGreek = true,
                    options = listOf(
                        DrillOption(id = "rough", label = "Rough — say [h]"),
                        DrillOption(id = "smooth", label = "Smooth — silent"),
                    ).shuffled(random),
                    correctId = if (word.breathing == Breathing.ROUGH) "rough" else "smooth",
                    explanation = explanation(word),
                )
            }

    /** Transliteration → pick the Greek spelling. */
    fun toGreekDeck(
        pack: LessonPack,
        random: Random,
        size: Int = READ_DECK_SIZE,
    ): List<DrillQuestion> =
        pack.words.shuffled(random).take(size).map { word ->
            val tiers = LinkedHashSet<WordLesson>()
            pack.words.filter { it.greek.first() == word.greek.first() }
                .shuffled(random).forEach(tiers::add)
            pack.words.filter { it.breathing == word.breathing }
                .shuffled(random).forEach(tiers::add)
            pack.words.shuffled(random).forEach(tiers::add)
            val distractors = tiers.filter { it.greek != word.greek }.take(OPTION_COUNT - 1)
            DrillQuestion(
                promptTitle = "Which word is",
                prompt = word.translit,
                options = (distractors + word)
                    .map { DrillOption(id = it.greek, label = it.greek, greek = true) }
                    .shuffled(random),
                correctId = word.greek,
                explanation = explanation(word),
            )
        }

    private fun explanation(word: WordLesson): String =
        "${word.greek} = ${word.translit} ${word.ipa} — ${word.gloss}" +
            if (word.note.isNotBlank()) ". ${word.note}" else ""

    /**
     * Plausible misreadings of a transliteration: the breathing toggled, one
     * aspirate digraph degraded/added, one long-vowel macron dropped/added.
     */
    internal fun perturbations(translit: String): List<String> {
        val variants = mutableListOf<String>()
        variants += when {
            translit.startsWith("rh") -> "r" + translit.removePrefix("rh")
            translit.startsWith("h") -> translit.removePrefix("h")
            else -> "h$translit"
        }
        for ((plain, aspirate) in listOf("p" to "ph", "t" to "th", "k" to "kh")) {
            when {
                aspirate in translit -> variants += translit.replaceFirst(aspirate, plain)
                plain in translit -> variants += translit.replaceFirst(plain, aspirate)
            }
        }
        for ((short, long) in listOf("e" to "ē", "o" to "ō")) {
            when {
                long in translit -> variants += translit.replaceFirst(long, short)
                short in translit -> variants += translit.replaceFirst(short, long)
            }
        }
        return variants.filter { it != translit && it.isNotEmpty() }.distinct()
    }
}
