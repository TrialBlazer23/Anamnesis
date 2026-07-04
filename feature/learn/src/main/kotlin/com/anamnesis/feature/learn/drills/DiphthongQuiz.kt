package com.anamnesis.feature.learn.drills

import com.anamnesis.feature.learn.model.DiphthongLesson
import com.anamnesis.feature.learn.model.LessonPack
import kotlin.random.Random

/**
 * Unit-4 diphthong recognition (visual-first: sounds are shown as IPA).
 * Each diphthong is asked both ways — glyph → IPA and IPA → glyph — with
 * distractors preferring plausible confusions: same first letter (αι/αυ),
 * then same kind (proper/improper), then anything else.
 */
object DiphthongQuiz {

    fun deck(pack: LessonPack, random: Random): List<DrillQuestion> {
        val pool = pack.diphthongs
        val glyphToSound = pool.map { answer ->
            val options = withAnswer(answer, pool, random) {
                DrillOption(id = it.glyph, label = it.ipa)
            }
            DrillQuestion(
                promptTitle = "How does this sound?",
                prompt = answer.glyph,
                promptGreek = true,
                promptCaption = if (answer.improper) "improper (iota subscript)" else null,
                options = options.shuffled(random),
                correctId = answer.glyph,
                explanation = explanation(answer),
            )
        }
        val soundToGlyph = pool.map { answer ->
            val options = withAnswer(answer, pool, random) {
                DrillOption(id = it.glyph, label = it.glyph, greek = true)
            }
            DrillQuestion(
                promptTitle = "Which spelling sounds like",
                prompt = answer.ipa,
                options = options.shuffled(random),
                correctId = answer.glyph,
                explanation = explanation(answer),
            )
        }
        return (glyphToSound + soundToGlyph).shuffled(random)
    }

    private fun explanation(d: DiphthongLesson): String =
        "${d.glyph} = ${d.ipa}" + if (d.note.isNotBlank()) " — ${d.note}" else ""

    private fun withAnswer(
        answer: DiphthongLesson,
        pool: List<DiphthongLesson>,
        random: Random,
        toOption: (DiphthongLesson) -> DrillOption,
    ): List<DrillOption> {
        val tiers = LinkedHashSet<DiphthongLesson>()
        pool.filter { it.glyph.first() == answer.glyph.first() }.shuffled(random).forEach(tiers::add)
        pool.filter { it.improper == answer.improper }.shuffled(random).forEach(tiers::add)
        pool.shuffled(random).forEach(tiers::add)
        val distractors = tiers.filter { it.glyph != answer.glyph }.take(OPTION_COUNT - 1)
        return (distractors + answer).map(toOption)
    }

    private const val OPTION_COUNT = 4
}
