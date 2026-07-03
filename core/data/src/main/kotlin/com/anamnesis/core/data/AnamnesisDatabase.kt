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
    version = 3,
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

        /**
         * v2 → v3: cards gain `position` (pedagogical introduction order; the
         * next seed pass fills real values) and `introducedEpochDay` (first-review
         * day, -1 = never; backfilled from the last review as the best estimate).
         */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE cards ADD COLUMN position INTEGER NOT NULL DEFAULT 0"
                )
                db.execSQL(
                    "ALTER TABLE cards ADD COLUMN introducedEpochDay INTEGER NOT NULL DEFAULT -1"
                )
                db.execSQL(
                    "UPDATE cards SET introducedEpochDay = lastReviewEpochDay WHERE reps > 0"
                )
            }
        }
    }
}
