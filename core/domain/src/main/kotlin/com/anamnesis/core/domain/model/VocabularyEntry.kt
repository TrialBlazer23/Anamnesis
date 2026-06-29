package com.anamnesis.core.domain.model

/**
 * A lemma the learner studies, sourced from the DCC Greek Core Vocabulary
 * (CC BY-SA 3.0) plus an openly-licensed LSJ gloss.
 */
data class VocabularyEntry(
    val lemma: String,
    val partOfSpeech: String,
    val gloss: String,
    val semanticGroup: String?,
    val frequencyRank: Int?,
)
