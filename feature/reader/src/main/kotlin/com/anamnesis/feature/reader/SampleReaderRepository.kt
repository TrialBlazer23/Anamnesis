package com.anamnesis.feature.reader

import com.anamnesis.core.domain.model.Passage
import com.anamnesis.core.domain.repository.ReaderRepository

/** Fallback [ReaderRepository] used when no content pack is bundled. */
class SampleReaderRepository : ReaderRepository {
    override suspend fun loadPassages(): List<Passage> = SAMPLE_PASSAGES
}
