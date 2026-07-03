package com.anamnesis.feature.srs

import com.anamnesis.core.domain.model.Card

/**
 * Applies a review grade to a [Card] using the FSRS-6 scheduler — pure logic,
 * unit-testable without Android.
 */
class ReviewScheduler(private val fsrs: Fsrs = Fsrs()) {

    fun schedule(card: Card, rating: Rating, todayEpochDay: Long): Card {
        val next = nextMemoryState(card, rating, todayEpochDay)
        return card.copy(
            stability = next.stability,
            difficulty = next.difficulty,
            dueEpochDay = todayEpochDay + intervalFor(next, rating),
            lastReviewEpochDay = todayEpochDay,
            introducedEpochDay = if (card.isNew) todayEpochDay else card.introducedEpochDay,
            reps = card.reps + 1,
            lapses = card.lapses + if (rating == Rating.Again) 1 else 0,
        )
    }

    /** Days until [card] would next be due if graded [rating] now (0 = later today). */
    fun previewIntervalDays(card: Card, rating: Rating, todayEpochDay: Long): Long =
        intervalFor(nextMemoryState(card, rating, todayEpochDay), rating)

    private fun nextMemoryState(card: Card, rating: Rating, todayEpochDay: Long): MemoryState =
        if (card.isNew) {
            fsrs.initialState(rating)
        } else {
            val elapsed = (todayEpochDay - card.lastReviewEpochDay).coerceAtLeast(0L).toDouble()
            fsrs.nextState(MemoryState(card.stability, card.difficulty), rating, elapsed)
        }

    /**
     * A failed card stays due today so it re-enters the session queue
     * (relearning) instead of vanishing until tomorrow; the same-day re-grade
     * then takes FSRS's short-term stability path.
     */
    private fun intervalFor(state: MemoryState, rating: Rating): Long =
        if (rating == Rating.Again) 0L else fsrs.intervalDays(state.stability).toLong()
}
