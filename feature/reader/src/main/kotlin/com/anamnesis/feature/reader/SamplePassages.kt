package com.anamnesis.feature.reader

import com.anamnesis.core.domain.model.Passage

/**
 * A few real, NFC-normalized passages from the Meditations (Perseus
 * `perseus-grc2`, CC BY-SA 4.0) used for previews and as a placeholder source
 * until the reader is wired to the content-pack database. Translations are null
 * for now (Haines 1916 ingestion is pending).
 */
private const val URN = "urn:cts:greekLit:tlg0562.tlg001.perseus-grc2"

val SAMPLE_PASSAGES: List<Passage> = listOf(
    Passage(
        ctsUrn = "$URN:1.1.1",
        work = "Meditations",
        reference = "1.1",
        greek = "Παρὰ τοῦ πάππου Οὐήρου τὸ καλόηθες καὶ ἀόργητον.",
        searchKey = "παρα του παππου ουηρου το καλοηθες και αοργητον.",
        translation = null,
    ),
    Passage(
        ctsUrn = "$URN:2.1.1",
        work = "Meditations",
        reference = "2.1",
        greek = "Ἕωθεν προλέγειν ἑαυτῷ· συντεύξομαι περιέργῳ, ἀχαρίστῳ, " +
            "ὑβριστῇ, δολερῷ, βασκάνῳ, ἀκοινωνήτῳ.",
        searchKey = "εωθεν προλεγειν εαυτω· συντευξομαι περιεργω, αχαριστω, " +
            "υβριστη, δολερω, βασκανω, ακοινωνητω.",
        translation = null,
    ),
    Passage(
        ctsUrn = "$URN:7.59.1",
        work = "Meditations",
        reference = "7.59",
        greek = "Ἔνδον σκάπτε, ἔνδον ἡ πηγὴ τοῦ ἀγαθοῦ καὶ ἀεὶ ἀναβλύειν " +
            "δυναμένη, ἐὰν ἀεὶ σκάπτῃς.",
        searchKey = "ενδον σκαπτε, ενδον η πηγη του αγαθου και αει αναβλυειν " +
            "δυναμενη, εαν αει σκαπτης.",
        translation = null,
    ),
)
