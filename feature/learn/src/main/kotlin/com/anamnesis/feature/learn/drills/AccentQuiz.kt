package com.anamnesis.feature.learn.drills

import com.anamnesis.feature.learn.model.Accent
import com.anamnesis.feature.learn.model.LessonPack
import kotlin.random.Random

/**
 * Unit-6 accent recognition (recognition only — the marks were musical pitch;
 * no production pressure). Two decks: name the accent a word carries, and the
 * spec's "same word or different?" judgement over curated accent pairs.
 */
object AccentQuiz {

    const val PAIR_DECK_SIZE = 10

    /** Word → acute / grave / circumflex. Options keep a fixed rubric order. */
    fun identifyDeck(pack: LessonPack, random: Random): List<DrillQuestion> =
        pack.accentItems.shuffled(random).map { item ->
            DrillQuestion(
                promptTitle = "Which accent does this word carry?",
                prompt = item.word,
                promptGreek = true,
                promptCaption = item.gloss,
                options = listOf(
                    DrillOption(id = "acute", label = "Acute ´ — pitch rises"),
                    DrillOption(id = "grave", label = "Grave ` — lowered/level"),
                    DrillOption(id = "circumflex", label = "Circumflex ῀ — rise and fall"),
                ),
                correctId = when (item.accent) {
                    Accent.ACUTE -> "acute"
                    Accent.GRAVE -> "grave"
                    Accent.CIRCUMFLEX -> "circumflex"
                },
                explanation = "${item.word} — ${item.gloss}" +
                    if (item.note.isNotBlank()) ". ${item.note}" else "",
            )
        }

    /** Two spellings side by side → same word, or two different words? */
    fun samePairDeck(
        pack: LessonPack,
        random: Random,
        size: Int = PAIR_DECK_SIZE,
    ): List<DrillQuestion> =
        pack.accentPairs.shuffled(random).take(size).map { pair ->
            DrillQuestion(
                promptTitle = "Same word, or two different words?",
                prompt = "${pair.a}  ·  ${pair.b}",
                promptGreek = true,
                options = listOf(
                    DrillOption(id = "same", label = "Same word"),
                    DrillOption(id = "different", label = "Different words"),
                ).shuffled(random),
                correctId = if (pair.same) "same" else "different",
                explanation = pair.note,
            )
        }
}
