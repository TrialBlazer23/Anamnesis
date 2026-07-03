package com.anamnesis

import android.content.Context
import com.anamnesis.core.data.content.ContentPackProvisioner
import com.anamnesis.core.data.content.ContentPackReaderRepository
import com.anamnesis.core.data.content.ContentPackSeedSource
import com.anamnesis.core.data.content.ContentPackVocabularyRepository
import com.anamnesis.core.data.packs.PackLibrary
import com.anamnesis.core.data.srs.SrsRepositoryFactory
import com.anamnesis.core.domain.model.Card
import com.anamnesis.core.domain.repository.ReaderRepository
import com.anamnesis.core.domain.repository.SrsRepository
import com.anamnesis.core.domain.repository.VocabularyRepository
import com.anamnesis.feature.learn.data.letterSeedCards
import com.anamnesis.feature.reader.EmptyVocabularyRepository
import com.anamnesis.feature.reader.SampleReaderRepository

/** Minimal manual dependency wiring (no DI framework yet). */
class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    private val hasContentPack = ContentPackProvisioner.isBundled(appContext)

    /** Downloaded-pack manager; also decides which text pack the reader opens. */
    val packLibrary by lazy { PackLibrary(appContext) }

    val readerRepository: ReaderRepository =
        if (hasContentPack) {
            ContentPackReaderRepository { packLibrary.activeTextDbPath() }
        } else {
            SampleReaderRepository()
        }

    val vocabularyRepository: VocabularyRepository =
        if (hasContentPack) {
            ContentPackVocabularyRepository { packLibrary.activeTextDbPath() }
        } else {
            EmptyVocabularyRepository
        }

    val srsRepository: SrsRepository by lazy { SrsRepositoryFactory.create(appContext) }

    private val seedSource by lazy { ContentPackSeedSource(appContext) }
    val srsSeeds: suspend () -> List<Card> = { letterSeedCards() + seedSource.seedCards() }
}
