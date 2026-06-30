package com.anamnesis.feature.reader

import com.anamnesis.core.domain.model.Passage
import com.anamnesis.core.domain.repository.ReaderRepository
import com.anamnesis.core.domain.text.GreekText

/** Fallback [ReaderRepository] used when no content pack is bundled. */
class SampleReaderRepository : ReaderRepository {
    override suspend fun loadPassages(): List<Passage> = SAMPLE_PASSAGES

    override suspend fun search(query: String): List<Passage> {
        val key = GreekText.stripDiacritics(query).trim()
        if (key.isBlank()) return emptyList()
        val english = query.trim().lowercase()
        return SAMPLE_PASSAGES.filter { passage ->
            GreekText.stripDiacritics(passage.greek).contains(key) ||
                passage.translation?.lowercase()?.contains(english) == true
        }
    }
}
