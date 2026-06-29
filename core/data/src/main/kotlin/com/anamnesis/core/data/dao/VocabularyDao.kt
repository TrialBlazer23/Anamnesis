package com.anamnesis.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anamnesis.core.data.entity.VocabularyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabularyDao {
    @Query("SELECT * FROM vocabulary ORDER BY frequencyRank")
    fun observeAll(): Flow<List<VocabularyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entries: List<VocabularyEntity>)

    @Query("SELECT * FROM vocabulary WHERE lemma = :lemma")
    suspend fun findByLemma(lemma: String): VocabularyEntity?
}
