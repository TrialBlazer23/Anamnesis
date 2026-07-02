package com.anamnesis.core.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/** SRS card row in the encrypted user DB. Holds only private review state. */
@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey val lemma: String,
    val gloss: String,
    val partOfSpeech: String,
    @ColumnInfo(defaultValue = "vocab") val deck: String,
    val stability: Double,
    val difficulty: Double,
    val dueEpochDay: Long,
    val lastReviewEpochDay: Long,
    val reps: Int,
    val lapses: Int,
)
