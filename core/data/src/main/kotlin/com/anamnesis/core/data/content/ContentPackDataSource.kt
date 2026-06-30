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

    private fun readTitle(db: SQLiteDatabase): String =
        db.rawQuery("SELECT value FROM meta WHERE key = 'title'", null).use { c ->
            if (c.moveToFirst()) c.getString(0) else ""
        }
}
