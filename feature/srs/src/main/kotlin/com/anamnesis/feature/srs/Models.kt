package com.anamnesis.feature.srs

/**
 * FSRS-6 default model parameters (21 weights). The 21st (decay) is 0.1542.
 * Matches py-fsrs / fsrs-rs defaults; override with a deck's optimized weights.
 */
val FSRS6_DEFAULT_PARAMETERS: DoubleArray = doubleArrayOf(
    0.212, 1.2931, 2.3065, 8.2956, 6.4133, 0.8334, 3.0194, 0.001,
    1.8722, 0.1666, 0.796, 1.4835, 0.0614, 0.2629, 1.6483, 0.6014,
    1.8729, 0.5425, 0.0912, 0.0658, 0.1542,
)

/** A learner's grade for a review, on the FSRS 1–4 scale. */
enum class Rating(val value: Int) {
    Again(1),
    Hard(2),
    Good(3),
    Easy(4),
}

/**
 * The memory state of a card: FSRS stability (days until retrievability decays
 * to the request retention) and difficulty (1–10).
 */
data class MemoryState(
    val stability: Double,
    val difficulty: Double,
)
