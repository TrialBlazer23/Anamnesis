package com.anamnesis.core.data.content

import android.content.Context
import com.anamnesis.core.domain.model.Card
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Produces new [Card]s from the content pack's vocabulary (DCC Greek core). */
class ContentPackSeedSource(private val context: Context) {

    suspend fun seedCards(): List<Card> = withContext(Dispatchers.IO) {
        val path = ContentPackProvisioner.ensure(context)
        // Entries arrive ordered by frequency_rank; vocabulary positions start
        // after the letter deck's so the alphabet is introduced first, then
        // words from most to least frequent.
        ContentPackDataSource(path).loadVocabularyEntries().mapIndexed { index, entry ->
            Card(
                lemma = entry.lemma,
                gloss = entry.gloss,
                partOfSpeech = entry.partOfSpeech,
                position = VOCAB_POSITION_BASE + (entry.frequencyRank ?: index),
            )
        }
    }

    private companion object {
        const val VOCAB_POSITION_BASE = 1000
    }
}
