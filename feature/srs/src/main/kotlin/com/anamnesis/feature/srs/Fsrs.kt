package com.anamnesis.feature.srs

import kotlin.math.exp
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * FSRS-6 spaced-repetition scheduler.
 *
 * Clean-room Kotlin port of the FSRS-6 algorithm, informed by
 * `open-spaced-repetition/FSRS-Kotlin` (MIT) and validated against the canonical
 * `py-fsrs` / `fsrs-rs` references. The upstream FSRS-Kotlin sample mixes in
 * Android/Compose/UI concerns and uses an exponential forgetting curve; this
 * port keeps only the algorithm, has no Android dependencies (so it is unit-
 * testable on the JVM), and uses the correct FSRS-6 power forgetting curve
 *
 *     R(t, S) = (1 + FACTOR · t / S) ^ DECAY,  DECAY = −w20,  FACTOR = 0.9^(1/DECAY) − 1
 *
 * which is calibrated so a card reviewed exactly at its stability has retrievability 0.9.
 */
class Fsrs(
    val parameters: DoubleArray = FSRS6_DEFAULT_PARAMETERS,
    val requestRetention: Double = 0.9,
) {
    init {
        require(parameters.size == 21) {
            "FSRS-6 requires 21 parameters, got ${parameters.size}"
        }
        require(requestRetention > 0.0 && requestRetention <= 1.0) {
            "requestRetention must be in (0, 1], got $requestRetention"
        }
    }

    private val decay = -parameters[20]
    private val factor = 0.9.pow(1.0 / decay) - 1.0

    /** Probability of recall after [elapsedDays] for a card of the given [stability]. */
    fun retrievability(elapsedDays: Double, stability: Double): Double {
        require(stability > 0.0) { "stability must be positive" }
        val t = elapsedDays.coerceAtLeast(0.0)
        return (1.0 + factor * t / stability).pow(decay)
    }

    /** Days until the card next decays to [requestRetention]. */
    fun intervalDays(stability: Double): Int {
        val raw = stability / factor * (requestRetention.pow(1.0 / decay) - 1.0)
        return raw.roundToInt().coerceIn(1, MAX_INTERVAL)
    }

    /** Memory state for the first review of a brand-new card. */
    fun initialState(rating: Rating): MemoryState =
        MemoryState(initialStability(rating), initialDifficulty(rating))

    /**
     * Memory state after reviewing a card whose previous state is [current],
     * [elapsedDays] after it was last seen. Same-day reviews (`elapsedDays < 1`)
     * use the short-term stability path.
     */
    fun nextState(current: MemoryState, rating: Rating, elapsedDays: Double): MemoryState {
        val stability = when {
            elapsedDays < 1.0 -> shortTermStability(current.stability, rating)
            rating == Rating.Again ->
                forgetStability(current.difficulty, current.stability, retrievability(elapsedDays, current.stability))
            else ->
                recallStability(current.difficulty, current.stability, retrievability(elapsedDays, current.stability), rating)
        }
        return MemoryState(
            stability = stability.coerceIn(S_MIN, MAX_INTERVAL.toDouble()),
            difficulty = nextDifficulty(current.difficulty, rating),
        )
    }

    private fun initialStability(rating: Rating): Double =
        parameters[rating.value - 1].coerceAtLeast(S_MIN)

    private fun initialDifficulty(rating: Rating): Double =
        (parameters[4] - exp(parameters[5] * (rating.value - 1)) + 1.0).coerceIn(1.0, 10.0)

    private fun nextDifficulty(difficulty: Double, rating: Rating): Double {
        val delta = -parameters[6] * (rating.value - 3)
        val damped = delta * (10.0 - difficulty) / 9.0          // linear damping
        val next = difficulty + damped
        // Mean reversion toward the "Easy" initial difficulty.
        val reverted = parameters[7] * initialDifficulty(Rating.Easy) + (1.0 - parameters[7]) * next
        return reverted.coerceIn(1.0, 10.0)
    }

    private fun shortTermStability(stability: Double, rating: Rating): Double {
        var sinc = exp(parameters[17] * (rating.value - 3 + parameters[18])) *
            stability.pow(-parameters[19])
        if (rating.value >= Rating.Good.value) sinc = maxOf(sinc, 1.0)
        return stability * sinc
    }

    private fun recallStability(
        difficulty: Double,
        stability: Double,
        retrievability: Double,
        rating: Rating,
    ): Double {
        val hardPenalty = if (rating == Rating.Hard) parameters[15] else 1.0
        val easyBonus = if (rating == Rating.Easy) parameters[16] else 1.0
        val growth = exp(parameters[8]) *
            (11.0 - difficulty) *
            stability.pow(-parameters[9]) *
            (exp((1.0 - retrievability) * parameters[10]) - 1.0) *
            hardPenalty *
            easyBonus
        return stability * (1.0 + growth)
    }

    private fun forgetStability(
        difficulty: Double,
        stability: Double,
        retrievability: Double,
    ): Double {
        val sMin = stability / exp(parameters[17] * parameters[18])
        val postLapse = parameters[11] *
            difficulty.pow(-parameters[12]) *
            ((stability + 1.0).pow(parameters[13]) - 1.0) *
            exp((1.0 - retrievability) * parameters[14])
        return min(postLapse, sMin)
    }

    companion object {
        /** FSRS clamps stability to a small positive floor. */
        const val S_MIN: Double = 0.01

        /** ~100 years; the scheduler never proposes a longer interval. */
        const val MAX_INTERVAL: Int = 36500
    }
}
