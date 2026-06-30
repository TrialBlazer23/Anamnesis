package com.anamnesis.feature.srs

import com.anamnesis.core.domain.model.Card

/**
 * Applies a review grade to a [Card] using the FSRS-6 scheduler — pure logic,
 * unit-testable without Android.
 */
class ReviewScheduler(private val fsrs: Fsrs = Fsrs()) {

    fun schedule(card: Card, rating: Rating, todayEpochDay: Long): Card {
        val next = if (card.isNew) {
            fsrs.initialState(rating)
        } else {
            val elapsed = (todayEpochDay - card.lastReviewEpochDay).coerceAtLeast(0L).toDouble()
            fsrs.nextState(MemoryState(card.stability, card.difficulty), rating, elapsed)
        }
        return card.copy(
            stability = next.stability,
            difficulty = next.difficulty,
            dueEpochDay = todayEpochDay + fsrs.intervalDays(next.stability),
            lastReviewEpochDay = todayEpochDay,
            reps = card.reps + 1,
            lapses = card.lapses + if (rating == Rating.Again) 1 else 0,
        )
    }
}
