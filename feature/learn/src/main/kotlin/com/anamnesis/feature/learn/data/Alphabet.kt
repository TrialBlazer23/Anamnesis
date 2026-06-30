package com.anamnesis.feature.learn.data

import com.anamnesis.feature.learn.model.LetterBatch
import com.anamnesis.feature.learn.model.LetterLesson

/**
 * The 24 letters, restored Classical Attic. Canonical source is
 * `pipeline/data/lessons/letters.csv`; kept here as a small static dataset until
 * the lessons-pack pipeline lands.
 */
val ALPHABET: List<LetterLesson> = listOf(
    LetterLesson("α", "Α", "ἄλφα", "alpha", "[a] / [aː]", 1, "A", false, false, "Hidden quantity — length is not shown in spelling.", "snd_alpha"),
    LetterLesson("β", "Β", "βῆτα", "beta", "[b]", 1, "B", false, false, "True voiced stop, NOT [v] of Modern Greek.", "snd_beta"),
    LetterLesson("γ", "Γ", "γάμμα", "gamma", "[g]; [ŋ] before γ κ χ ξ", 2, null, false, false, "Velar nasal [ŋ] in γγ/γκ/γχ (ἄγγελος).", "snd_gamma"),
    LetterLesson("δ", "Δ", "δέλτα", "delta", "[d]", 2, null, false, false, "True voiced stop, NOT [ð].", "snd_delta"),
    LetterLesson("ε", "Ε", "ἒ ψιλόν", "epsilon", "[e] short", 1, "E", false, false, "Short e; the length-pair of η.", "snd_epsilon"),
    LetterLesson("ζ", "Ζ", "ζῆτα", "zeta", "[zd]", 3, null, true, false, "Double consonant; [zd] is the standard teaching value (minority: [dz]).", "snd_zeta"),
    LetterLesson("η", "Η", "ἦτα", "eta", "[ɛː] long", 3, "H", true, false, "Looks like Latin H but = long open-e; the length-pair of ε.", "snd_eta"),
    LetterLesson("θ", "Θ", "θῆτα", "theta", "[tʰ] aspirated", 2, null, false, true, "True aspirate [tʰ], NOT [θ] (Erasmian/Modern).", "snd_theta"),
    LetterLesson("ι", "Ι", "ἰῶτα", "iota", "[i] / [iː]", 1, "I", false, false, "Hidden quantity.", "snd_iota"),
    LetterLesson("κ", "Κ", "κάππα", "kappa", "[k]", 1, "K", false, false, "Unaspirated.", "snd_kappa"),
    LetterLesson("λ", "Λ", "λάμβδα", "lambda", "[l]", 2, null, false, false, "", "snd_lambda"),
    LetterLesson("μ", "Μ", "μῦ", "mu", "[m]", 2, "M", false, false, "", "snd_mu"),
    LetterLesson("ν", "Ν", "νῦ", "nu", "[n]", 3, "v", true, false, "Looks like Latin v but = [n].", "snd_nu"),
    LetterLesson("ξ", "Ξ", "ξεῖ", "xi", "[ks]", 4, null, false, false, "Double consonant.", "snd_xi"),
    LetterLesson("ο", "Ο", "ὂ μικρόν", "omicron", "[o] short", 1, "O", false, false, "Short o; the length-pair of ω.", "snd_omicron"),
    LetterLesson("π", "Π", "πεῖ", "pi", "[p]", 2, null, false, false, "Unaspirated.", "snd_pi"),
    LetterLesson("ρ", "Ρ", "ῥῶ", "rho", "[r], initial [r̥]", 3, "p", true, false, "Looks like Latin p but = trilled r; initial ῥ is voiceless.", "snd_rho"),
    LetterLesson("σ", "Σ", "σῖγμα", "sigma", "[s], [z] before voiced", 2, null, false, false, "ς word-finally, σ elsewhere.", "snd_sigma"),
    LetterLesson("τ", "Τ", "ταῦ", "tau", "[t]", 1, "T", false, true, "Unaspirated.", "snd_tau"),
    LetterLesson("υ", "Υ", "ὖ ψιλόν", "upsilon", "[y] / [yː]", 3, "y", true, false, "French u / German ü, NOT [u]; usually rough breathing initially.", "snd_upsilon"),
    LetterLesson("φ", "Φ", "φεῖ", "phi", "[pʰ] aspirated", 2, null, false, false, "True aspirate [pʰ], NOT [f].", "snd_phi"),
    LetterLesson("χ", "Χ", "χεῖ", "chi", "[kʰ] aspirated", 3, "x", true, true, "Looks like x but = aspirate [kʰ], NOT [x]/[ç].", "snd_chi"),
    LetterLesson("ψ", "Ψ", "ψεῖ", "psi", "[ps]", 2, null, false, true, "Double consonant.", "snd_psi"),
    LetterLesson("ω", "Ω", "ὦ μέγα", "omega", "[ɔː] long", 2, null, false, false, "Long open-o; the length-pair of ο.", "snd_omega"),
)

private val BATCH_TITLES = mapOf(
    1 to "Familiar letters",
    2 to "Distinctive Greek letters",
    3 to "False friends",
    4 to "Special cases",
)

/** The alphabet grouped into the four difficulty batches (spec §2). */
val ALPHABET_BATCHES: List<LetterBatch> =
    ALPHABET.groupBy { it.batch }
        .toSortedMap()
        .map { (number, letters) -> LetterBatch(number, BATCH_TITLES[number] ?: "Batch $number", letters) }
