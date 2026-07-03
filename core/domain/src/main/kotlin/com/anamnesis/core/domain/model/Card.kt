package com.anamnesis.core.domain.model

/**
 * A study card with its FSRS memory state. A card with `stability == 0.0`
 * has never been reviewed (new). Dates are epoch-days (days since 1970-01-01),
 * which is the granularity FSRS schedules at.
 *
 * [deck] groups cards by source/kind — `"vocab"` (DCC lemmas) or `"letters"`
 * (alphabet drills); all decks interleave in one FSRS due queue.
 *
 * [position] is the pedagogical introduction order for new cards: letters come
 * first (alphabet batch order), then vocabulary by corpus frequency — so the
 * trainer introduces the most useful words first instead of alphabetically.
 *
 * [introducedEpochDay] is the day the card was first reviewed ([NEVER_INTRODUCED]
 * until then); it drives the daily new-card budget.
 */
data class Card(
    val lemma: String,
    val gloss: String,
    val partOfSpeech: String,
    val deck: String = DECK_VOCAB,
    val position: Int = 0,
    val stability: Double = 0.0,
    val difficulty: Double = 0.0,
    val dueEpochDay: Long = 0L,
    val lastReviewEpochDay: Long = 0L,
    val introducedEpochDay: Long = NEVER_INTRODUCED,
    val reps: Int = 0,
    val lapses: Int = 0,
) {
    val isNew: Boolean get() = stability <= 0.0

    companion object {
        const val DECK_VOCAB = "vocab"
        const val DECK_LETTERS = "letters"
        const val NEVER_INTRODUCED = -1L
    }
}
