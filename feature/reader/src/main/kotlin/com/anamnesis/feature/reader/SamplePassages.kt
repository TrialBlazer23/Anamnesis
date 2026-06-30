package com.anamnesis.feature.reader

import com.anamnesis.core.domain.model.Passage

/**
 * A few real, NFC-normalized passages from the Meditations (Perseus
 * `perseus-grc2`, CC BY-SA 4.0) with their facing Haines 1916 translation
 * (public domain). Used for previews and as a placeholder source until the
 * reader is wired to the content-pack database.
 */
private const val URN = "urn:cts:greekLit:tlg0562.tlg001.perseus-grc2"

val SAMPLE_PASSAGES: List<Passage> = listOf(
    Passage(
        ctsUrn = "$URN:1.1.1",
        work = "Meditations",
        reference = "1.1",
        greek = "Παρὰ τοῦ πάππου Οὐήρου τὸ καλόηθες καὶ ἀόργητον.",
        searchKey = "παρα του παππου ουηρου το καλοηθες και αοργητον.",
        translation = "From my Grandfather Verus, a kindly disposition and sweetness of temper.",
    ),
    Passage(
        ctsUrn = "$URN:2.1.1",
        work = "Meditations",
        reference = "2.1",
        greek = "Ἕωθεν προλέγειν ἑαυτῷ· συντεύξομαι περιέργῳ, ἀχαρίστῳ, " +
            "ὑβριστῇ, δολερῷ, βασκάνῳ, ἀκοινωνήτῳ.",
        searchKey = "εωθεν προλεγειν εαυτω· συντευξομαι περιεργω, αχαριστω, " +
            "υβριστη, δολερω, βασκανω, ακοινωνητω.",
        translation = "Say to thyself at daybreak: I shall come across the busy-body, " +
            "the thankless, the bully, the treacherous, the envious, the unneighbourly.",
    ),
    Passage(
        ctsUrn = "$URN:7.59.1",
        work = "Meditations",
        reference = "7.59",
        greek = "Ἔνδον σκάπτε, ἔνδον ἡ πηγὴ τοῦ ἀγαθοῦ καὶ ἀεὶ ἀναβλύειν " +
            "δυναμένη, ἐὰν ἀεὶ σκάπτῃς.",
        searchKey = "ενδον σκαπτε, ενδον η πηγη του αγαθου και αει αναβλυειν " +
            "δυναμενη, εαν αει σκαπτης.",
        translation = "Look within. Within is the fountain of Good, ready always to " +
            "well forth if thou wilt alway delve.",
    ),
)
