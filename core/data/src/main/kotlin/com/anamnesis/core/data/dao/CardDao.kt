package com.anamnesis.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.anamnesis.core.data.entity.CardEntity

@Dao
interface CardDao {
    @Query("SELECT COUNT(*) FROM cards")
    suspend fun count(): Int

    /** Seed without clobbering existing review progress. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(cards: List<CardEntity>)

    /** Refresh content fields on an existing row, leaving review state alone. */
    @Query(
        "UPDATE cards SET position = :position, gloss = :gloss, partOfSpeech = :partOfSpeech " +
            "WHERE lemma = :lemma"
    )
    suspend fun refreshStatic(lemma: String, position: Int, gloss: String, partOfSpeech: String)

    @Transaction
    suspend fun seed(cards: List<CardEntity>) {
        insertAll(cards)
        cards.forEach { refreshStatic(it.lemma, it.position, it.gloss, it.partOfSpeech) }
    }

    @Upsert
    suspend fun upsert(card: CardEntity)

    /** Cards already in rotation that are due for review, soonest first. */
    @Query(
        "SELECT * FROM cards WHERE stability > 0 AND dueEpochDay <= :today " +
            "ORDER BY dueEpochDay, position LIMIT :limit"
    )
    suspend fun dueReviews(today: Long, limit: Int): List<CardEntity>

    /** Never-studied cards in pedagogical introduction order. */
    @Query("SELECT * FROM cards WHERE stability <= 0 ORDER BY position, lemma LIMIT :limit")
    suspend fun newCards(limit: Int): List<CardEntity>

    /** Like [newCards], restricted to the given decks. */
    @Query(
        "SELECT * FROM cards WHERE stability <= 0 AND deck IN (:decks) " +
            "ORDER BY position, lemma LIMIT :limit"
    )
    suspend fun newCardsInDecks(decks: Set<String>, limit: Int): List<CardEntity>

    @Query("SELECT COUNT(*) FROM cards WHERE introducedEpochDay = :day")
    suspend fun countIntroducedOn(day: Long): Int
}
