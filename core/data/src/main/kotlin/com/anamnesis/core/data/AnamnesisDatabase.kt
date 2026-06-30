package com.anamnesis.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.anamnesis.core.data.dao.CardDao
import com.anamnesis.core.data.entity.CardEntity

/**
 * The encrypted (SQLCipher) user database. Holds private SRS state only —
 * public content (Greek text, translations, vocab) lives in the separate
 * read-only content pack.
 */
@Database(
    entities = [CardEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class AnamnesisDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao
}
