package com.anamnesis.core.domain.repository

import com.anamnesis.core.domain.model.Card

/** Persistence for spaced-repetition cards (the encrypted user DB). */
interface SrsRepository {
    suspend fun count(): Int

    /**
     * Insert cards that don't already exist (keeps existing progress) and
     * refresh their static fields (position/gloss/part of speech) so content
     * updates and ordering fixes reach already-seeded installs.
     */
    suspend fun seed(cards: List<Card>)

    /** Previously studied cards due on or before [todayEpochDay], soonest first. */
    suspend fun dueReviewCards(todayEpochDay: Long, limit: Int = 200): List<Card>

    /** Never-studied cards in introduction order (letters, then vocab by frequency). */
    suspend fun newCards(limit: Int): List<Card>

    /** How many cards were introduced (first reviewed) on [epochDay]. */
    suspend fun countIntroducedOn(epochDay: Long): Int

    suspend fun upsert(card: Card)
}
