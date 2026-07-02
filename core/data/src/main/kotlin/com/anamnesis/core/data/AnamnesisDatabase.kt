package com.anamnesis.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.anamnesis.core.data.dao.CardDao
import com.anamnesis.core.data.entity.CardEntity

/**
 * The encrypted (SQLCipher) user database. Holds private SRS state only —
 * public content (Greek text, translations, vocab) lives in the separate
 * read-only content pack.
 */
@Database(
    entities = [CardEntity::class],
    version = 2,
    exportSchema = true,
)
abstract class AnamnesisDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao

    companion object {
        /** v1 → v2: cards gain a `deck` grouping column (existing rows = vocab). */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE cards ADD COLUMN deck TEXT NOT NULL DEFAULT 'vocab'"
                )
            }
        }
    }
}
