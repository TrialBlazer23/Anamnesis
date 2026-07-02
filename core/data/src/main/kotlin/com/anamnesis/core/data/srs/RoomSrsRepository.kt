package com.anamnesis.core.data.srs

import com.anamnesis.core.data.dao.CardDao
import com.anamnesis.core.data.entity.CardEntity
import com.anamnesis.core.domain.model.Card
import com.anamnesis.core.domain.repository.SrsRepository

/** [SrsRepository] backed by the encrypted Room database. */
class RoomSrsRepository(private val dao: CardDao) : SrsRepository {

    override suspend fun count(): Int = dao.count()

    override suspend fun seed(cards: List<Card>) = dao.insertAll(cards.map { it.toEntity() })

    override suspend fun dueCards(todayEpochDay: Long, limit: Int): List<Card> =
        dao.due(todayEpochDay, limit).map { it.toDomain() }

    override suspend fun upsert(card: Card) = dao.upsert(card.toEntity())
}

private fun Card.toEntity() = CardEntity(
    lemma = lemma,
    gloss = gloss,
    partOfSpeech = partOfSpeech,
    deck = deck,
    stability = stability,
    difficulty = difficulty,
    dueEpochDay = dueEpochDay,
    lastReviewEpochDay = lastReviewEpochDay,
    reps = reps,
    lapses = lapses,
)

private fun CardEntity.toDomain() = Card(
    lemma = lemma,
    gloss = gloss,
    partOfSpeech = partOfSpeech,
    deck = deck,
    stability = stability,
    difficulty = difficulty,
    dueEpochDay = dueEpochDay,
    lastReviewEpochDay = lastReviewEpochDay,
    reps = reps,
    lapses = lapses,
)
