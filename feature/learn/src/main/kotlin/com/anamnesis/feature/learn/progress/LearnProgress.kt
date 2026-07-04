package com.anamnesis.feature.learn.progress

import android.content.Context

/**
 * Pure unit-gating rules (spec §7) — separated from storage so they are
 * unit-testable:
 * - Units 0–1 are always open.
 * - Units 2 and 3 unlock when the previous unit is complete.
 * - Units 4+ are not built yet ("soon") and stay locked regardless.
 *
 * Completion criteria (recognition accuracy, not handwriting):
 * - Unit 1 ← batch-1 practice session ≥ 90%.
 * - Unit 2 ← batch-2 practice session ≥ 90%.
 * - Unit 3 ← a MIXED full-alphabet session ("All") ≥ 90% (per the spec, the
 *   false-friend unit is passed by a mixed quiz, not a batch-3-only drill).
 */
object UnitGating {
    const val PASS_THRESHOLD = 0.9
    private val ALWAYS_OPEN = setOf(0, 1)

    /** Units above this are roadmap-only ("soon"); raised as units are built. */
    const val HIGHEST_BUILT_UNIT = 3

    /** The alphabet on-ramp; completing it unlocks vocabulary cards in Train. */
    val ALPHABET_UNITS = setOf(1, 2, 3)

    fun alphabetComplete(completed: Set<Int>): Boolean =
        ALPHABET_UNITS.all { it in completed }

    /** Which unit (if any) a finished practice session completes. */
    fun unitForSession(scopeBatch: Int?, score: Int, total: Int): Int? {
        if (total <= 0 || score.toDouble() / total < PASS_THRESHOLD) return null
        return when (scopeBatch) {
            1 -> 1
            2 -> 2
            null -> 3 // mixed full-alphabet quiz
            else -> null
        }
    }

    fun isUnlocked(unit: Int, completed: Set<Int>): Boolean = when {
        unit in ALWAYS_OPEN -> true
        unit > HIGHEST_BUILT_UNIT -> false
        else -> (unit - 1) in completed
    }
}

/** Persisted per-unit completion (SharedPreferences — no new dependencies). */
class LearnProgressStore(context: Context) {
    private val prefs = context.getSharedPreferences("learn_progress", Context.MODE_PRIVATE)

    fun completedUnits(): Set<Int> =
        prefs.getStringSet(KEY_COMPLETED, emptySet())
            .orEmpty()
            .mapNotNull { it.toIntOrNull() }
            .toSet()

    fun markCompleted(unit: Int) {
        val updated = completedUnits() + unit
        prefs.edit()
            .putStringSet(KEY_COMPLETED, updated.map(Int::toString).toSet())
            .apply()
    }

    private companion object {
        const val KEY_COMPLETED = "completed_units"
    }
}
