package com.anamnesis.core.domain.repository

import com.anamnesis.core.domain.model.VocabularyEntry

/** Dictionary lookup over the content pack's vocabulary. */
interface VocabularyRepository {
    /**
     * Best-effort accent-insensitive lookup of a (possibly inflected) word
     * against DCC headwords. Returns null when nothing matches — full
     * morphological lemmatization is a later (Phase 3) enhancement.
     */
    suspend fun lookup(token: String): VocabularyEntry?
}
