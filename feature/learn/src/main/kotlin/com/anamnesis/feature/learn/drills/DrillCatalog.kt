package com.anamnesis.feature.learn.drills

import com.anamnesis.feature.learn.model.LessonPack
import kotlin.random.Random

/**
 * Maps the drill ids authored in `units.json` to their screens: a display
 * label and a question-deck builder. Drills of units that are not built yet
 * are simply absent and hidden by the UI.
 */
object DrillCatalog {

    private val LABELS = mapOf(
        "long-or-short" to "Long or short?",
        "length-minimal-pair" to "Minimal pairs",
        "diphthong-to-sound" to "Diphthong sounds",
    )

    fun isBuilt(drillId: String): Boolean = drillId in LABELS

    fun label(drillId: String): String = LABELS[drillId] ?: drillId

    fun deck(pack: LessonPack, drillId: String, random: Random): List<DrillQuestion> =
        when (drillId) {
            "long-or-short" -> LengthQuiz.longOrShortDeck(pack, random)
            "length-minimal-pair" -> LengthQuiz.minimalPairDeck(pack, random)
            "diphthong-to-sound" -> DiphthongQuiz.deck(pack, random)
            else -> emptyList()
        }
}
