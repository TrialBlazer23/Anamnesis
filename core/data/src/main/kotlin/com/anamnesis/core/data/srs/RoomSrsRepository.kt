package com.anamnesis.core.data.srs

import com.anamnesis.core.data.dao.CardDao
import com.anamnesis.core.data.entity.CardEntity
import com.anamnesis.core.domain.model.Card
import com.anamnesis.core.domain.repository.SrsRepository

/** [SrsRepository] backed by the encrypted Room database. */
class RoomSrsRepository(private val dao: CardDao) : SrsRepository {

    override suspend fun count(): Int = dao.count()

    override suspend fun seed(cards: List<Card>) = dao.seed(cards.map { it.toEntity() })

    override suspend fun dueReviewCards(todayEpochDay: Long, limit: Int): List<Card> =
        dao.dueReviews(todayEpochDay, limit).map { it.toDomain() }

    override suspend fun newCards(limit: Int): List<Card> =
        dao.newCards(limit).map { it.toDomain() }

    override suspend fun countIntroducedOn(epochDay: Long): Int =
        dao.countIntroducedOn(epochDay)

    override suspend fun upsert(card: Card) = dao.upsert(card.toEntity())
}

private fun Card.toEntity() = CardEntity(
    lemma = lemma,
    gloss = gloss,
    partOfSpeech = partOfSpeech,
    deck = deck,
    position = position,
    stability = stability,
    difficulty = difficulty,
    dueEpochDay = dueEpochDay,
    lastReviewEpochDay = lastReviewEpochDay,
    introducedEpochDay = introducedEpochDay,
    reps = reps,
    lapses = lapses,
)

private fun CardEntity.toDomain() = Card(
    lemma = lemma,
    gloss = gloss,
    partOfSpeech = partOfSpeech,
    deck = deck,
    position = position,
    stability = stability,
    difficulty = difficulty,
    dueEpochDay = dueEpochDay,
    lastReviewEpochDay = lastReviewEpochDay,
    introducedEpochDay = introducedEpochDay,
    reps = reps,
    lapses = lapses,
)
