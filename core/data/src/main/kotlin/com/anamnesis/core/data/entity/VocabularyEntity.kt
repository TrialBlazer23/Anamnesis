package com.anamnesis.core.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vocabulary")
data class VocabularyEntity(
    @PrimaryKey val lemma: String,
    val partOfSpeech: String,
    val gloss: String,
    val semanticGroup: String?,
    val frequencyRank: Int?,
)
