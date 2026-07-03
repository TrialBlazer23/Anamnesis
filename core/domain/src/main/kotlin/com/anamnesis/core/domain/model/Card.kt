package com.anamnesis.core.domain.model

/**
 * A study card with its FSRS memory state. A card with `stability == 0.0`
 * has never been reviewed (new). Dates are epoch-days (days since 1970-01-01),
 * which is the granularity FSRS schedules at.
 *
 * [deck] groups cards by source/kind — `"vocab"` (DCC lemmas) or `"letters"`
 * (alphabet drills); all decks interleave in one FSRS due queue.
 */
data class Card(
    val lemma: String,
    val gloss: String,
    val partOfSpeech: String,
    val deck: String = DECK_VOCAB,
    val stability: Double = 0.0,
    val difficulty: Double = 0.0,
    val dueEpochDay: Long = 0L,
    val lastReviewEpochDay: Long = 0L,
    val reps: Int = 0,
    val lapses: Int = 0,
) {
    val isNew: Boolean get() = stability <= 0.0

    companion object {
        const val DECK_VOCAB = "vocab"
        const val DECK_LETTERS = "letters"
    }
}
