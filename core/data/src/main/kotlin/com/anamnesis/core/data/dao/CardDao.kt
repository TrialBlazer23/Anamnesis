package com.anamnesis.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.anamnesis.core.data.entity.CardEntity

@Dao
interface CardDao {
    @Query("SELECT COUNT(*) FROM cards")
    suspend fun count(): Int

    /** Seed without clobbering existing review progress. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(cards: List<CardEntity>)

    @Upsert
    suspend fun upsert(card: CardEntity)

    @Query("SELECT * FROM cards WHERE dueEpochDay <= :today ORDER BY dueEpochDay, lemma LIMIT :limit")
    suspend fun due(today: Long, limit: Int): List<CardEntity>
}
