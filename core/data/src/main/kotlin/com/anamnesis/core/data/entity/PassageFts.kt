package com.anamnesis.core.data.entity

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

/**
 * Full-text-searchable passage row.
 *
 * Room's annotation surface is `@Fts4` (FTS5 exists in the SQLCipher engine but
 * Room generates FTS3/FTS4). `searchKey` holds diacritic-stripped Greek so
 * searches are accent-insensitive; `greek` keeps the NFC-normalized original.
 *
 * For an FTS entity, `rowid` is SQLite's implicit INTEGER PRIMARY KEY — Room
 * manages it, so no `autoGenerate`.
 */
@Fts4
@Entity(tableName = "passage_fts")
data class PassageFts(
    @PrimaryKey val rowid: Int = 0,
    val ctsUrn: String,
    val reference: String,
    val greek: String,
    val searchKey: String,
    val translation: String?,
)
