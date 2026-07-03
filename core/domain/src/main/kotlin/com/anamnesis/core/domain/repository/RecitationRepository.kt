package com.anamnesis.core.domain.repository

/** Provides per-line recitation audio from installed audio packs. */
interface RecitationRepository {
    /**
     * Absolute path of a playable audio file for this passage's CTS URN, or
     * null when no installed audio pack covers it.
     */
    suspend fun audioFileFor(ctsUrn: String): String?
}
