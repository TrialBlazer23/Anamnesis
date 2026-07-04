package com.anamnesis.feature.learn.data

import android.content.Context
import com.anamnesis.core.domain.model.Card
import com.anamnesis.feature.learn.model.LessonPack
import com.anamnesis.feature.learn.pack.LessonPackLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Seed cards are refreshed by lemma (CardDao.refreshStatic), so the lemma
// spaces of the decks must stay disjoint: letters use "α Α" glyph pairs,
// diphthongs the bare glyph ("αι"), vocab the DCC citation form.

/**
 * The alphabet as FSRS cards (deck = [Card.DECK_LETTERS]): front is the glyph,
 * back is name + restored-Attic sound. They interleave with vocabulary in the
 * Train tab's due queue; FSRS naturally schedules the harder false-friend
 * letters more often (spec §7).
 */
fun letterSeedCards(pack: LessonPack): List<Card> = pack.letters.mapIndexed { index, letter ->
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
        // Letters occupy the earliest introduction positions (before all vocab),
        // in alphabet-batch order.
        position = index,
    )
}

/** Seeds every deck the lessons pack feeds into the Train tab. */
class LessonSeedSource(context: Context) {
    private val appContext = context.applicationContext

    suspend fun seedCards(): List<Card> = withContext(Dispatchers.IO) {
        val pack = LessonPackLoader.load(appContext)
        letterSeedCards(pack)
    }
}
