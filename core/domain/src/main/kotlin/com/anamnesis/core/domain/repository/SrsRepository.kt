package com.anamnesis.core.domain.repository

import com.anamnesis.core.domain.model.Card

/** Persistence for spaced-repetition cards (the encrypted user DB). */
interface SrsRepository {
    suspend fun count(): Int

    /** Insert cards that don't already exist (keeps existing progress). */
    suspend fun seed(cards: List<Card>)

    /** Cards due on or before [todayEpochDay], soonest first. */
    suspend fun dueCards(todayEpochDay: Long, limit: Int = 30): List<Card>

    suspend fun upsert(card: Card)
}
