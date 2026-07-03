package com.anamnesis.core.data.content

import android.database.sqlite.SQLiteDatabase
import com.anamnesis.core.domain.model.Passage
import com.anamnesis.core.domain.model.VocabularyEntry
import com.anamnesis.core.domain.text.GreekText

/**
 * Reads from a content-pack SQLite file (read-only, plain SQLite — built by the
 * Python pipeline): passages, FTS5 search, vocabulary, and `meta`.
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

    fun loadVocabularyEntries(): List<VocabularyEntry> {
        SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY).use { db ->
            db.rawQuery(
                "SELECT lemma, part_of_speech, gloss, semantic_group, frequency_rank " +
                    "FROM vocabulary ORDER BY frequency_rank",
                null,
            ).use { cursor ->
                val entries = ArrayList<VocabularyEntry>(cursor.count)
                while (cursor.moveToNext()) {
                    entries += VocabularyEntry(
                        lemma = cursor.getString(0),
                        partOfSpeech = cursor.getString(1) ?: "",
                        gloss = cursor.getString(2) ?: "",
                        semanticGroup = cursor.getString(3),
                        frequencyRank = if (cursor.isNull(4)) null else cursor.getInt(4),
                    )
                }
                return entries
            }
        }
    }

    fun search(query: String, limit: Int = 50): List<Passage> {
        val match = buildMatchExpression(query)
        if (match.isBlank()) return emptyList()
        SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY).use { db ->
            val title = readTitle(db)
            db.rawQuery(
                "SELECT p.cts_urn, p.ref, p.greek, p.search_key, p.translation " +
                    "FROM passages p JOIN passage_fts f ON p.id = f.rowid " +
                    "WHERE passage_fts MATCH ? ORDER BY p.id LIMIT ?",
                arrayOf(match, limit.toString()),
            ).use { cursor ->
                val results = ArrayList<Passage>(cursor.count)
                while (cursor.moveToNext()) {
                    results += Passage(
                        ctsUrn = cursor.getString(0),
                        work = title,
                        reference = cursor.getString(1),
                        greek = cursor.getString(2),
                        searchKey = cursor.getString(3),
                        translation = cursor.getString(4),
                    )
                }
                return results
            }
        }
    }

    /**
     * Broad short-gloss dictionary lookup (Middle Liddell) by accent-insensitive
     * key. Returns null on packs predating the `lexicon` table (schema v1).
     */
    fun lookupLexicon(normalizedKey: String): VocabularyEntry? = runCatching {
        SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY).use { db ->
            db.rawQuery(
                "SELECT lemma, gloss FROM lexicon WHERE normalized_lemma = ? LIMIT 1",
                arrayOf(normalizedKey),
            ).use { cursor ->
                if (cursor.moveToFirst()) {
                    VocabularyEntry(
                        lemma = cursor.getString(0),
                        partOfSpeech = "",
                        gloss = cursor.getString(1),
                        semanticGroup = null,
                        frequencyRank = null,
                    )
                } else {
                    null
                }
            }
        }
    }.getOrNull()

    /**
     * Morphological analyses of a surface form by accent-insensitive key
     * (form → lemma + parse, filtered to this pack's tokens at build time; the
     * pipeline stores at most 3 analyses per form). Ambiguous forms return all
     * candidates — the caller ranks them. Returns empty on packs predating the
     * `morphology` table (schema < 3).
     */
    fun lookupMorphology(normalizedKey: String): List<MorphAnalysis> = runCatching {
        SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY).use { db ->
            db.rawQuery(
                "SELECT form, lemma, parse, gloss FROM morphology WHERE form_key = ?",
                arrayOf(normalizedKey),
            ).use { cursor ->
                val analyses = ArrayList<MorphAnalysis>(cursor.count)
                while (cursor.moveToNext()) {
                    analyses += MorphAnalysis(
                        form = cursor.getString(0),
                        lemma = cursor.getString(1),
                        parse = cursor.getString(2) ?: "",
                        gloss = cursor.getString(3) ?: "",
                    )
                }
                analyses
            }
        }
    }.getOrDefault(emptyList())

    /** Diacritic-strip the query and turn each word into an FTS5 prefix token. */
    private fun buildMatchExpression(query: String): String =
        GreekText.stripDiacritics(query)
            .split(Regex("\\s+"))
            .map { token -> token.filter { it.isLetterOrDigit() } }
            .filter { it.isNotEmpty() }
            .joinToString(" ") { "$it*" }

    private fun readTitle(db: SQLiteDatabase): String =
        db.rawQuery("SELECT value FROM meta WHERE key = 'title'", null).use { c ->
            if (c.moveToFirst()) c.getString(0) else ""
        }
}

/** One morphological analysis: the surface form, its lemma, and the parse. */
internal data class MorphAnalysis(
    val form: String,
    val lemma: String,
    val parse: String,
    val gloss: String,
)
