package com.anamnesis.core.data.content

import android.database.sqlite.SQLiteDatabase
import com.anamnesis.core.domain.model.Passage

/**
 * Reads passages from a content-pack SQLite file (read-only, plain SQLite — the
 * pack is built by the Python pipeline). Only the `passages` and `meta` tables
 * are touched here; FTS search comes later.
 */
internal class ContentPackDataSource(private val dbPath: String) {

    fun loadPassages(): List<Passage> {
        SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY).use { db ->
            val title = readTitle(db)
            db.rawQuery(
                "SELECT cts_urn, ref, greek, search_key, translation FROM passages ORDER BY id",
                null,
            ).use { cursor ->
                val passages = ArrayList<Passage>(cursor.count)
                while (cursor.moveToNext()) {
                    passages += Passage(
                        ctsUrn = cursor.getString(0),
                        work = title,
                        reference = cursor.getString(1),
                        greek = cursor.getString(2),
                        searchKey = cursor.getString(3),
                        translation = cursor.getString(4),
                    )
                }
                return passages
            }
        }
    }

    fun loadVocabulary(): List<VocabularyRow> {
        SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY).use { db ->
            db.rawQuery(
                "SELECT lemma, gloss, part_of_speech FROM vocabulary ORDER BY frequency_rank",
                null,
            ).use { cursor ->
                val rows = ArrayList<VocabularyRow>(cursor.count)
                while (cursor.moveToNext()) {
                    rows += VocabularyRow(
                        lemma = cursor.getString(0),
                        gloss = cursor.getString(1) ?: "",
                        partOfSpeech = cursor.getString(2) ?: "",
                    )
                }
                return rows
            }
        }
    }

    private fun readTitle(db: SQLiteDatabase): String =
        db.rawQuery("SELECT value FROM meta WHERE key = 'title'", null).use { c ->
            if (c.moveToFirst()) c.getString(0) else ""
        }
}

internal data class VocabularyRow(
    val lemma: String,
    val gloss: String,
    val partOfSpeech: String,
)
