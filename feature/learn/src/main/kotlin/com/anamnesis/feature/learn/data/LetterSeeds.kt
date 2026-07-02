package com.anamnesis.feature.learn.data

import com.anamnesis.core.domain.model.Card

/**
 * The alphabet as FSRS cards (deck = [Card.DECK_LETTERS]): front is the glyph,
 * back is name + restored-Attic sound. They interleave with vocabulary in the
 * Train tab's due queue; FSRS naturally schedules the harder false-friend
 * letters more often (spec §7).
 */
fun letterSeedCards(): List<Card> = ALPHABET.map { letter ->
    Card(
        lemma = "${letter.lower} ${letter.upper}",
        gloss = "${letter.nameTranslit} · ${letter.ipa}" +
            if (letter.falseFriend && letter.latinLookalike != null) {
                "  (not Latin “${letter.latinLookalike}”)"
            } else {
                ""
            },
        partOfSpeech = "letter · batch ${letter.batch}",
        deck = Card.DECK_LETTERS,
    )
}
