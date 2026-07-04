package com.anamnesis.feature.learn.model

/**
 * One letter of the alphabet as taught (restored Classical Attic). Mirrors the
 * canonical source `pipeline/data/lessons/letters.csv`; [audioId] is optional so
 * the Learn tab works fully without audio (sound is shown as IPA + name).
 */
data class LetterLesson(
    val lower: String,
    val upper: String,
    val nameGreek: String,
    val nameTranslit: String,
    val ipa: String,
    val batch: Int,
    val latinLookalike: String?,
    val falseFriend: Boolean,
    val multistroke: Boolean,
    val teachingNote: String,
    val audioId: String? = null,
)

/** A teaching batch with a short title (see spec §2). */
data class LetterBatch(val number: Int, val title: String, val letters: List<LetterLesson>)

/** One unit of the 9-unit on-ramp curriculum (`units.json`). */
data class LearnUnit(
    val number: Int,
    val title: String,
    val objective: String,
    val taught: String = "",
    val drills: List<String> = emptyList(),
    val srsFeed: String = "",
    val advance: String = "",
    val batch: Int? = null,
)

/** A proper or improper (iota-subscript) diphthong (`diphthongs.csv`). */
data class DiphthongLesson(
    val glyph: String,
    val ipa: String,
    val improper: Boolean,
    val note: String,
    val audioId: String? = null,
)

/** A length/aspiration/hidden-quantity contrast pair (`minimal_pairs.csv`). */
data class MinimalPair(
    val id: String,
    val a: String,
    val b: String,
    val contrast: String,
    val type: String,
    val note: String,
    val audioAId: String? = null,
    val audioBId: String? = null,
)

/** Word-initial breathing (unit 5). */
enum class Breathing {
    ROUGH, SMOOTH, NONE;

    companion object {
        fun from(raw: String): Breathing = when (raw) {
            "rough" -> ROUGH
            "smooth" -> SMOOTH
            "none" -> NONE
            else -> throw IllegalArgumentException("Unknown breathing '$raw'")
        }
    }
}

/** A curated DCC core word for the unit-5 reading drills (`words.csv`). */
data class WordLesson(
    val dccRank: Int,
    val greek: String,
    val translit: String,
    val ipa: String,
    val breathing: Breathing,
    val gloss: String,
    val pos: String,
    val note: String,
    val audioId: String? = null,
)

/** The three pitch-accent marks (unit 6, recognition only). */
enum class Accent {
    ACUTE, GRAVE, CIRCUMFLEX;

    companion object {
        fun from(raw: String): Accent = when (raw) {
            "acute" -> ACUTE
            "grave" -> GRAVE
            "circumflex" -> CIRCUMFLEX
            else -> throw IllegalArgumentException("Unknown accent '$raw'")
        }
    }
}

/** An identify-the-accent item (`accent_items.csv`). */
data class AccentItem(
    val id: String,
    val word: String,
    val accent: Accent,
    val gloss: String,
    val note: String,
    val audioId: String? = null,
)

/** A same-word-or-different accent pair (`accent_pairs.csv`). */
data class AccentPair(
    val id: String,
    val a: String,
    val b: String,
    val same: Boolean,
    val note: String,
    val audioAId: String? = null,
    val audioBId: String? = null,
)

/**
 * The bundled lessons pack — everything the Learn tab teaches, built by
 * `pipeline/build_lessons.py` from the authored data in `pipeline/data/lessons/`.
 */
data class LessonPack(
    val schemaVersion: Int,
    val scheme: String,
    val units: List<LearnUnit>,
    val letters: List<LetterLesson>,
    val diphthongs: List<DiphthongLesson>,
    val minimalPairs: List<MinimalPair>,
    val words: List<WordLesson>,
    val accentItems: List<AccentItem>,
    val accentPairs: List<AccentPair>,
)

/** The alphabet grouped into the four teaching batches, titled from units 1–3. */
fun LessonPack.letterBatches(): List<LetterBatch> {
    val titles = units.mapNotNull { u -> u.batch?.let { it to u.title } }.toMap()
    return letters.groupBy { it.batch }
        .toSortedMap()
        .map { (number, batchLetters) ->
            LetterBatch(number, titles[number] ?: "Special cases", batchLetters)
        }
}
