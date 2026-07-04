package com.anamnesis.feature.learn

/**
 * A recognition question for the sound units (4–6): show [greek] (when present),
 * ask [prompt], offer [options], reveal [explanation] after answering.
 */
data class UnitQuizQuestion(
    val greek: String?,
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
) {
    init {
        require(correctIndex in options.indices) { "correctIndex out of bounds" }
    }

    val correct: String get() = options[correctIndex]
}
