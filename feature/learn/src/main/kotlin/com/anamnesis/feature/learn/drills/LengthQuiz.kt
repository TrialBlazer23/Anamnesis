package com.anamnesis.feature.learn.drills

import com.anamnesis.feature.learn.model.LessonPack
import com.anamnesis.feature.learn.model.MinimalPair
import kotlin.random.Random

/**
 * Unit-4 vowel-quantity drills, driven by the pack's length-type minimal
 * pairs (ε/η, ο/ω, and the hidden-quantity ᾰ/ᾱ, ῐ/ῑ). Both decks feed the
 * same "length" gate. Pair rows are authored as "short X vs long Y", so side
 * `a` is always the short member.
 */
object LengthQuiz {

    private val LENGTH_TYPES = setOf("vowel-length", "hidden-quantity")

    /** One vowel with its known quantity, split out of a minimal pair. */
    data class LengthItem(
        val glyph: String,
        val isLong: Boolean,
        val description: String,
        val note: String,
    )

    fun lengthItems(pack: LessonPack): List<LengthItem> =
        lengthPairs(pack).flatMap { pair ->
            val (shortDesc, longDesc) = descriptions(pair)
            listOf(
                LengthItem(pair.a, isLong = false, description = shortDesc, note = pair.note),
                LengthItem(pair.b, isLong = true, description = longDesc, note = pair.note),
            )
        }

    /** Show a vowel, answer Long or Short. */
    fun longOrShortDeck(pack: LessonPack, random: Random): List<DrillQuestion> =
        lengthItems(pack).shuffled(random).map { item ->
            DrillQuestion(
                promptTitle = "Long or short?",
                prompt = item.glyph,
                promptGreek = true,
                options = listOf(
                    DrillOption(id = "long", label = "Long"),
                    DrillOption(id = "short", label = "Short"),
                ).shuffled(random),
                correctId = if (item.isLong) "long" else "short",
                explanation = "${item.glyph} = ${item.description}" +
                    if (item.note.isNotBlank()) " — ${item.note}" else "",
            )
        }

    /** Show one side's sound description, pick the letter of the pair it names. */
    fun minimalPairDeck(pack: LessonPack, random: Random): List<DrillQuestion> =
        lengthPairs(pack).flatMap { pair ->
            val (shortDesc, longDesc) = descriptions(pair)
            listOf(
                pairQuestion(pair, prompt = shortDesc, correct = pair.a, random = random),
                pairQuestion(pair, prompt = longDesc, correct = pair.b, random = random),
            )
        }.shuffled(random)

    private fun pairQuestion(
        pair: MinimalPair,
        prompt: String,
        correct: String,
        random: Random,
    ): DrillQuestion = DrillQuestion(
        promptTitle = "Which letter is",
        prompt = prompt,
        options = listOf(
            DrillOption(id = pair.a, label = pair.a, greek = true),
            DrillOption(id = pair.b, label = pair.b, greek = true),
        ).shuffled(random),
        correctId = correct,
        explanation = "${pair.a} vs ${pair.b}: ${pair.contrast}" +
            if (pair.note.isNotBlank()) " — ${pair.note}" else "",
    )

    private fun lengthPairs(pack: LessonPack): List<MinimalPair> =
        pack.minimalPairs.filter { it.type in LENGTH_TYPES }

    private fun descriptions(pair: MinimalPair): Pair<String, String> {
        val sides = pair.contrast.split(" vs ", limit = 2)
        return if (sides.size == 2) sides[0] to sides[1] else pair.contrast to pair.contrast
    }
}
