package com.anamnesis.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.anamnesis.core.data.dao.VocabularyDao
import com.anamnesis.core.data.entity.PassageFts
import com.anamnesis.core.data.entity.VocabularyEntity

@Database(
    entities = [VocabularyEntity::class, PassageFts::class],
    version = 1,
    exportSchema = true,
)
abstract class AnamnesisDatabase : RoomDatabase() {
    abstract fun vocabularyDao(): VocabularyDao
}
