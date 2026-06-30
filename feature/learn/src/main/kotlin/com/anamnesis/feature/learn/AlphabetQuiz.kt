package com.anamnesis.feature.learn

import com.anamnesis.feature.learn.model.LetterLesson
import kotlin.random.Random

/** A single recognition question: pick [answer] from [options] (name/sound → letter). */
data class QuizQuestion(
    val answer: LetterLesson,
    val options: List<LetterLesson>,
)

/** Pure question-generation for the alphabet recognition drill (unit-testable). */
object AlphabetQuiz {

    /** A shuffled study deck over [pool]. */
    fun deck(pool: List<LetterLesson>, random: Random): List<LetterLesson> =
        pool.shuffled(random)

    /** Build a question for [answer], with up to [optionCount] options drawn from [pool]. */
    fun question(
        answer: LetterLesson,
        pool: List<LetterLesson>,
        optionCount: Int = 4,
        random: Random,
    ): QuizQuestion {
        val distractors = pool
            .filter { it.lower != answer.lower }
            .shuffled(random)
            .take((optionCount - 1).coerceAtLeast(0))
        val options = (distractors + answer).shuffled(random)
        return QuizQuestion(answer = answer, options = options)
    }
}
