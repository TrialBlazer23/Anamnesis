package com.anamnesis.feature.learn.progress

import android.content.Context

/**
 * Pure unit-gating rules (spec §7) — separated from storage so they are
 * unit-testable:
 * - Units 0–1 are always open.
 * - Each later unit unlocks when the previous unit is complete.
 * - Units above [UnitGating.HIGHEST_BUILT_UNIT] are not built yet ("soon")
 *   and stay locked regardless.
 *
 * Completion criteria (recognition accuracy, not handwriting):
 * - Unit 1 ← batch-1 practice session ≥ 90%.
 * - Unit 2 ← batch-2 practice session ≥ 90%.
 * - Unit 3 ← a MIXED full-alphabet session ("All") ≥ 90% (per the spec, the
 *   false-friend unit is passed by a mixed quiz, not a batch-3-only drill).
 * - Unit 4+ ← per-drill gates: every gate in [UnitGating.requiredGates] passed
 *   at its own threshold (e.g. unit 4 = length ≥ 80% AND diphthongs ≥ 90%).
 */
object UnitGating {
    const val PASS_THRESHOLD = 0.9
    private val ALWAYS_OPEN = setOf(0, 1)

    /** Units above this are roadmap-only ("soon"); raised as units are built. */
    const val HIGHEST_BUILT_UNIT = 4

    /** The alphabet on-ramp; completing it unlocks vocabulary cards in Train. */
    val ALPHABET_UNITS = setOf(1, 2, 3)

    // Gate keys persisted per passed drill ("<unit>:<skill>"). Unit 4 needs two
    // independent passes; the two length drills feed a single shared gate.
    const val GATE_LENGTH = "4:length"
    const val GATE_DIPHTHONG = "4:diphthong"

    private data class DrillGate(val key: String, val threshold: Double)

    private val DRILL_GATES = mapOf(
        // Length discrimination is genuinely hard — the spec passes it at 80%.
        "long-or-short" to DrillGate(GATE_LENGTH, 0.80),
        "length-minimal-pair" to DrillGate(GATE_LENGTH, 0.80),
        "diphthong-to-sound" to DrillGate(GATE_DIPHTHONG, 0.90),
    )

    private val REQUIRED_GATES = mapOf(
        4 to setOf(GATE_LENGTH, GATE_DIPHTHONG),
    )

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

    /** The pass threshold shown/used for a unit-4+ drill. */
    fun drillThreshold(drillId: String): Double =
        DRILL_GATES[drillId]?.threshold ?: PASS_THRESHOLD

    /** The gate a drill feeds, if it is one of the gated drills. */
    fun gateForDrill(drillId: String): String? = DRILL_GATES[drillId]?.key

    /** The gate key a finished drill session passes, or null if it fell short. */
    fun drillPassed(drillId: String, score: Int, total: Int): String? {
        val gate = DRILL_GATES[drillId] ?: return null
        if (total <= 0 || score.toDouble() / total < gate.threshold) return null
        return gate.key
    }

    /** Gates a unit requires before it is complete (empty = not drill-gated). */
    fun requiredGates(unit: Int): Set<String> = REQUIRED_GATES[unit].orEmpty()

    fun unitCompleteFromDrills(unit: Int, passedGates: Set<String>): Boolean =
        requiredGates(unit).let { it.isNotEmpty() && passedGates.containsAll(it) }
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

    fun passedDrillGates(): Set<String> =
        prefs.getStringSet(KEY_DRILLS, emptySet()).orEmpty().toSet()

    fun markDrillGatePassed(gate: String) {
        prefs.edit()
            .putStringSet(KEY_DRILLS, passedDrillGates() + gate)
            .apply()
    }

    private companion object {
        const val KEY_COMPLETED = "completed_units"
        const val KEY_DRILLS = "passed_drills"
    }
}
