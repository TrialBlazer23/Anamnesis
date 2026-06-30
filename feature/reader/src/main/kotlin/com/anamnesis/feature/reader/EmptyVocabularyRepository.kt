package com.anamnesis.feature.reader

import com.anamnesis.core.domain.model.VocabularyEntry
import com.anamnesis.core.domain.repository.VocabularyRepository

/** No-op vocabulary lookup for when no content pack is bundled. */
object EmptyVocabularyRepository : VocabularyRepository {
    override suspend fun lookup(token: String): VocabularyEntry? = null
}
