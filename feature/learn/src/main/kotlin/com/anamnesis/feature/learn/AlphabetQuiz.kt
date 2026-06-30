package com.anamnesis.feature.learn

import com.anamnesis.feature.learn.model.LetterLesson
import kotlin.random.Random

/** A single recognition question: pick [answer] from [options] (name/sound → letter). */
data class QuizQuestion(
    val answer: LetterLesson,
    val options: List<LetterLesson>,
)

/**
 * Pure question-generation for the alphabet recognition drill (unit-testable).
 *
 * Distractors are chosen to be *plausible* — letters genuinely confusable with the
 * answer — so the drill targets real discrimination, not random elimination. The
 * priority tiers are: curated visual look-alikes → same teaching batch → anything
 * else, each tier shuffled for variety.
 */
object AlphabetQuiz {

    /** Curated Greek-internal visual look-alike pairs (symmetric, expanded below). */
    private val CONFUSABLE_PAIRS = listOf(
        "ν" to "υ", // nu vs upsilon
        "ζ" to "ξ", // zeta vs xi
        "θ" to "ο", // theta vs omicron
        "ο" to "σ", // omicron vs sigma
        "ε" to "σ", // epsilon vs (lunate) sigma
        "η" to "ν", // eta vs nu (descenders)
        "γ" to "τ", // gamma vs tau
    )

    private val CONFUSABLES: Map<String, Set<String>> = buildMap<String, MutableSet<String>> {
        CONFUSABLE_PAIRS.forEach { (a, b) ->
            getOrPut(a) { mutableSetOf() }.add(b)
            getOrPut(b) { mutableSetOf() }.add(a)
        }
    }

    /** A shuffled study deck over [pool]. */
    fun deck(pool: List<LetterLesson>, random: Random): List<LetterLesson> =
        pool.shuffled(random)

    /** Build a question for [answer], preferring confusable distractors from [pool]. */
    fun question(
        answer: LetterLesson,
        pool: List<LetterLesson>,
        optionCount: Int = 4,
        random: Random,
    ): QuizQuestion {
        val byGlyph = pool.associateBy { it.lower }
        val tiers = LinkedHashSet<LetterLesson>()

        // Tier 1: curated visual look-alikes.
        (CONFUSABLES[answer.lower] ?: emptySet()).shuffled(random)
            .mapNotNull { byGlyph[it] }
            .forEach(tiers::add)
        // Tier 2: same teaching batch.
        pool.filter { it.batch == answer.batch }.shuffled(random).forEach(tiers::add)
        // Tier 3: everything else.
        pool.shuffled(random).forEach(tiers::add)

        val distractors = tiers
            .filter { it.lower != answer.lower }
            .take((optionCount - 1).coerceAtLeast(0))
        val options = (distractors + answer).shuffled(random)
        return QuizQuestion(answer = answer, options = options)
    }
}
