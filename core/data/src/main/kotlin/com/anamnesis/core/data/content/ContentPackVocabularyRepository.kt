package com.anamnesis.core.data.content

import android.content.Context
import com.anamnesis.core.domain.model.VocabularyEntry
import com.anamnesis.core.domain.repository.VocabularyRepository
import com.anamnesis.core.domain.text.GreekText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * [VocabularyRepository] over the content pack. Lookup chain:
 * 1. DCC core vocabulary — an accent-insensitive in-memory index keyed by each
 *    space-separated part of a headword (e.g. "ὁ ἡ τό" indexes ο/η/το);
 *    frequency-ranked, teaching-oriented glosses.
 * 2. The pack's broad `lexicon` table (Middle Liddell, ~34k headwords) queried
 *    by normalized lemma — the fallback for everything the DCC lacks.
 */
class ContentPackVocabularyRepository(
    private val context: Context,
) : VocabularyRepository {

    @Volatile private var index: Map<String, VocabularyEntry>? = null

    override suspend fun lookup(token: String): VocabularyEntry? = withContext(Dispatchers.IO) {
        val key = GreekText.wordKey(token)
        if (key.isEmpty()) {
            null
        } else {
            ensureIndex()[key]
                ?: ContentPackDataSource(ContentPackProvisioner.ensure(context)).lookupLexicon(key)
        }
    }

    private fun ensureIndex(): Map<String, VocabularyEntry> {
        index?.let { return it }
        val entries = ContentPackDataSource(ContentPackProvisioner.ensure(context)).loadVocabularyEntries()
        val built = HashMap<String, VocabularyEntry>(entries.size * 2)
        for (entry in entries) {
            for (part in entry.lemma.split(Regex("\\s+"))) {
                val key = GreekText.wordKey(part)
                if (key.isNotEmpty()) built.putIfAbsent(key, entry)
            }
        }
        index = built
        return built
    }
}
