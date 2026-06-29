package com.anamnesis.feature.srs

/**
 * FSRS-6 scheduler.
 *
 * PLACEHOLDER. Phase 2 vendors the FSRS-Kotlin source port
 * (open-spaced-repetition/FSRS-Kotlin, MIT, explicitly FSRS-6) into this
 * package — that repo has no Maven coordinate, so the source is copied in here
 * with its LICENSE retained and recorded in THIRD_PARTY_LICENSES.md.
 *
 * FSRS-6 is a 21-parameter model; the 21st parameter (decay) defaults to 0.1542.
 * Validate the port against py-fsrs / fsrs-rs (both FSRS-6) before relying on it.
 */
val FSRS6_DEFAULT_PARAMETERS: DoubleArray = doubleArrayOf(
    0.212, 1.2931, 2.3065, 8.2956, 6.4133, 0.8334, 3.0194, 0.001,
    1.8722, 0.1666, 0.796, 1.4835, 0.0614, 0.2629, 1.6483, 0.6014,
    1.8729, 0.5425, 0.0912, 0.0658, 0.1542,
)

/** A learner's grade for a review, per the FSRS rating scale. */
enum class Rating { AGAIN, HARD, GOOD, EASY }
