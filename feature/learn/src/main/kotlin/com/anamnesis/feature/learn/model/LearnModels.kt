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
)

/** One reference row in a unit lesson: a symbol, what it is, how it sounds. */
data class LessonRow(
    val symbol: String,
    val label: String,
    val detail: String,
)

/** Teaching content for a curriculum unit (orientation + the sound units 4–6). */
data class UnitLesson(
    val unit: Int,
    val title: String,
    val intro: List<String>,
    val rows: List<LessonRow>,
    /** False for read-only units (orientation), true when a ≥90% quiz gates completion. */
    val hasQuiz: Boolean,
)
