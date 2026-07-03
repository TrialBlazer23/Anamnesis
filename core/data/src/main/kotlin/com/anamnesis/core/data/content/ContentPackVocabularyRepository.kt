package com.anamnesis.core.data.content

import android.content.Context
import com.anamnesis.core.domain.model.VocabularyEntry
import com.anamnesis.core.domain.repository.VocabularyRepository
import com.anamnesis.core.domain.text.GreekText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * [VocabularyRepository] over a content pack. Lookup chain:
 * 1. DCC core vocabulary — an accent-insensitive in-memory index keyed by each
 *    space-separated part of a headword (e.g. "ὁ ἡ τό" indexes ο/η/το);
 *    frequency-ranked, teaching-oriented glosses.
 * 2. The pack's broad `lexicon` table (Middle Liddell, ~34k headwords) queried
 *    by normalized lemma — for words that ARE headwords.
 * 3. The pack's `morphology` table (form → lemma + parse): resolves inflected
 *    forms to their lemma, then glosses the lemma via 1/2 — tap-to-parse.
 *
 * [dbPath] is resolved per lookup so the dictionary follows the active pack
 * (each pack's morphology table is filtered to its own text's forms); the
 * in-memory DCC index is rebuilt when the path changes.
 */
class ContentPackVocabularyRepository(
    private val dbPath: () -> String,
) : VocabularyRepository {

    constructor(context: Context) : this({ ContentPackProvisioner.ensure(context) })

    @Volatile private var cachedIndex: Pair<String, Map<String, VocabularyEntry>>? = null

    override suspend fun lookup(token: String): VocabularyEntry? = withContext(Dispatchers.IO) {
        val key = GreekText.wordKey(token)
        if (key.isEmpty()) return@withContext null

        val path = dbPath()
        val source = ContentPackDataSource(path)
        ensureIndex(path, source)[key]?.let { return@withContext it }
        source.lookupLexicon(key)?.let { return@withContext it }

        val analyses = source.lookupMorphology(key)
        if (analyses.isEmpty()) return@withContext null
        // Ambiguous forms (πᾶσι → πᾶς or πᾶσις): prefer the analysis whose
        // lemma is core vocabulary, then one the lexicon can gloss, then first.
        val index = ensureIndex(path, source)
        val ranked = analyses.sortedBy { analysis ->
            val lemmaKey = GreekText.wordKey(analysis.lemma)
            when {
                index.containsKey(lemmaKey) -> 0
                source.lookupLexicon(lemmaKey) != null -> 1
                else -> 2
            }
        }
        val best = ranked.first()
        val lemmaKey = GreekText.wordKey(best.lemma)
        val lemmaEntry = index[lemmaKey] ?: source.lookupLexicon(lemmaKey)
        val alternatives = ranked.drop(1)
            .filter { it.lemma != best.lemma }
            .distinctBy { it.lemma }
            .joinToString("") { "\nAlso possible: ${it.lemma} — ${it.parse}" }
        VocabularyEntry(
            lemma = best.lemma,
            partOfSpeech = best.parse,
            gloss = (lemmaEntry?.gloss?.takeIf { it.isNotBlank() } ?: best.gloss) + alternatives,
            semanticGroup = lemmaEntry?.semanticGroup,
            frequencyRank = lemmaEntry?.frequencyRank,
        )
    }

    private fun ensureIndex(
        path: String,
        source: ContentPackDataSource,
    ): Map<String, VocabularyEntry> {
        cachedIndex?.takeIf { it.first == path }?.let { return it.second }
        val entries = source.loadVocabularyEntries()
        val built = HashMap<String, VocabularyEntry>(entries.size * 2)
        for (entry in entries) {
            for (part in entry.lemma.split(Regex("\\s+"))) {
                val key = GreekText.wordKey(part)
                if (key.isNotEmpty()) built.putIfAbsent(key, entry)
            }
        }
        cachedIndex = path to built
        return built
    }
}
