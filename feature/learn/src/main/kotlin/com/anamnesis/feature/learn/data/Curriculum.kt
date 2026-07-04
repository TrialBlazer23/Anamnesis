package com.anamnesis.feature.learn.data

import com.anamnesis.feature.learn.model.LearnUnit

/**
 * The 9-unit on-ramp (spec §7 / `units.json`), shown as a tappable roadmap.
 * Units 0–6 are live: 0 is a read-only orientation, 1–3 drill the alphabet
 * (letter practice), and 4–6 are the sound units (see SoundUnits). 7–8 land
 * as the Learn tab grows.
 */
val CURRICULUM: List<LearnUnit> = listOf(
    LearnUnit(0, "Orientation", "What restored Attic is, and how this on-ramp works."),
    LearnUnit(1, "Familiar letters", "Read and sound the 7 Latin-like letters."),
    LearnUnit(2, "Distinctive Greek letters", "Add 10 distinctively Greek letters, incl. the aspirates."),
    LearnUnit(3, "False friends", "Defeat the Latin look-alike trap (Η Ν Ρ Χ Υ Ζ)."),
    LearnUnit(4, "Vowel quantity & diphthongs", "Hear long vs short and the 8 diphthongs."),
    LearnUnit(5, "Breathings & words", "Rough/smooth breathing; read whole short words."),
    LearnUnit(6, "Accents (recognition)", "Recognize acute/grave/circumflex — the musical marks."),
    LearnUnit(7, "How Greek words work", "Inflection and lemma vs. form, so tap-to-parse makes sense."),
    LearnUnit(8, "Variation, punctuation & Euthyphro", "The three kinds of variation; begin reading Plato."),
)
