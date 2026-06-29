package com.anamnesis.core.domain.model

/**
 * A single canonical passage of Greek text with its facing translation.
 *
 * Pure-domain model — no Android/Room/serialization dependencies. The CTS URN
 * (e.g. `urn:cts:greekLit:tlg0562.tlg001.perseus-grc2:1.1`) is the stable
 * identity carried through the pipeline and the app.
 */
data class Passage(
    val ctsUrn: String,
    val work: String,
    val reference: String,
    /** NFC-normalized polytonic Greek. */
    val greek: String,
    /** Diacritic-stripped key for accent-insensitive search. */
    val searchKey: String,
    /** Public-domain facing translation (Haines 1916 for Meditations). */
    val translation: String?,
)
