package com.anamnesis

import android.content.Context
import com.anamnesis.core.data.content.ContentPackProvisioner
import com.anamnesis.core.data.content.ContentPackReaderRepository
import com.anamnesis.core.data.content.ContentPackSeedSource
import com.anamnesis.core.data.srs.SrsRepositoryFactory
import com.anamnesis.core.domain.model.Card
import com.anamnesis.core.domain.repository.ReaderRepository
import com.anamnesis.core.domain.repository.SrsRepository
import com.anamnesis.feature.reader.SampleReaderRepository

/** Minimal manual dependency wiring (no DI framework yet). */
class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    val readerRepository: ReaderRepository =
        if (ContentPackProvisioner.isBundled(appContext)) {
            ContentPackReaderRepository(appContext)
        } else {
            SampleReaderRepository()
        }

    val srsRepository: SrsRepository by lazy { SrsRepositoryFactory.create(appContext) }

    private val seedSource by lazy { ContentPackSeedSource(appContext) }
    val srsSeeds: suspend () -> List<Card> = { seedSource.seedCards() }
}
