package com.anamnesis.core.data.content

import android.content.Context
import com.anamnesis.core.domain.model.Card
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Produces new [Card]s from the content pack's vocabulary (DCC Greek core). */
class ContentPackSeedSource(private val context: Context) {

    suspend fun seedCards(): List<Card> = withContext(Dispatchers.IO) {
        val path = ContentPackProvisioner.ensure(context)
        ContentPackDataSource(path).loadVocabulary().map { row ->
            Card(lemma = row.lemma, gloss = row.gloss, partOfSpeech = row.partOfSpeech)
        }
    }
}
