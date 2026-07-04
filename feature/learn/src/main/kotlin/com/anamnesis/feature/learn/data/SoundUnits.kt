package com.anamnesis.feature.learn.data

import com.anamnesis.feature.learn.UnitQuizQuestion
import com.anamnesis.feature.learn.model.LessonRow
import com.anamnesis.feature.learn.model.UnitLesson
import kotlin.random.Random

/**
 * Content and quizzes for the non-letter units: 0 (orientation, read-only) and
 * the sound units 4–6 (vowel quantity & diphthongs, breathings, accents).
 * Pronunciations are restored Classical Attic (spec §2), matching the letter
 * lessons; everything is text/IPA so the units work fully without audio.
 */
object SoundUnits {

    fun lesson(unit: Int): UnitLesson? = when (unit) {
        0 -> ORIENTATION
        4 -> DIPHTHONGS_LESSON
        5 -> BREATHINGS_LESSON
        6 -> ACCENTS_LESSON
        else -> null
    }

    /** A fresh shuffled quiz deck for [unit] (empty for read-only units). */
    fun quiz(unit: Int, random: Random): List<UnitQuizQuestion> = when (unit) {
        4 -> diphthongQuiz(random)
        5 -> breathingQuiz(random)
        6 -> accentQuiz(random)
        else -> emptyList()
    }.shuffled(random)

    // ---------------------------------------------------------------- unit 0

    private val ORIENTATION = UnitLesson(
        unit = 0,
        title = "Orientation",
        intro = listOf(
            "Anamnesis teaches restored Classical Attic — our best reconstruction " +
                "of how Greek sounded in Athens around 400 BC, the Greek of Socrates " +
                "and Plato. Vowels keep their length, and accents were musical pitch, " +
                "not stress.",
            "Why bother? Poetry scans, puns work, and spelling becomes nearly " +
                "phonetic: every mark you see is a sound you say.",
            "The path: master the letters (units 1–3), then vowel length and " +
                "diphthongs, breathings, and the accent marks (units 4–6). After " +
                "that you learn how Greek words change shape — and start reading " +
                "real Plato.",
            "The Train tab reinforces everything with spaced repetition. It drills " +
                "letters from day one; vocabulary unlocks when you finish the " +
                "alphabet units here.",
        ),
        rows = emptyList(),
        hasQuiz = false,
    )

    // ---------------------------------------------------------------- unit 4

    private data class Quantity(val symbol: String, val label: String, val answer: String, val detail: String)

    private val QUANTITIES = listOf(
        Quantity("η", "êta", ALWAYS_LONG, "the vowel of “air”, held long (/ɛː/)"),
        Quantity("ω", "ômega", ALWAYS_LONG, "“aw” of “saw”, held long (/ɔː/)"),
        Quantity("ε", "epsilon", ALWAYS_SHORT, "“e” of “pet” (/e/)"),
        Quantity("ο", "omicron", ALWAYS_SHORT, "“o” of “pot” (/o/)"),
        Quantity("α", "alpha", VARIES, "“a” of “father”, short or long"),
        Quantity("υ", "upsilon", VARIES, "French “u”, short or long"),
    )

    private data class Diphthong(val glyphs: String, val sound: String)

    private val DIPHTHONGS = listOf(
        Diphthong("αι", "“eye” — as in “high”"),
        Diphthong("ει", "“ay” — as in “they”, steady with no glide"),
        Diphthong("οι", "“oy” — as in “boy”"),
        Diphthong("υι", "“üi” — French “huit” (rare)"),
        Diphthong("αυ", "“ow” — as in “how”"),
        Diphthong("ευ", "“eh-oo” — one gliding syllable"),
        Diphthong("ηυ", "“air-oo” — the long partner of ευ"),
        Diphthong("ου", "“oo” — as in “food”"),
    )

    private val DIPHTHONGS_LESSON = UnitLesson(
        unit = 4,
        title = "Vowel quantity & diphthongs",
        intro = listOf(
            "Greek vowels have length: long vowels are held roughly twice as long " +
                "as short ones, and the difference changes meaning and poetry. " +
                "η and ω are always long; ε and ο are always short; α, ι and υ " +
                "can be either — the word decides.",
            "A diphthong is two vowels gliding together inside one syllable. " +
                "Classical Attic has eight. Learn each as a single sound — they " +
                "are everywhere.",
        ),
        rows = QUANTITIES.map { LessonRow(it.symbol, "${it.label} — ${it.answer}", it.detail) } +
            DIPHTHONGS.map { LessonRow(it.glyphs, "diphthong", it.sound) },
        hasQuiz = true,
    )

    private fun diphthongQuiz(random: Random): List<UnitQuizQuestion> {
        val quantityOptions = listOf(ALWAYS_LONG, ALWAYS_SHORT, VARIES)
        val quantityQuestions = QUANTITIES.map { q ->
            UnitQuizQuestion(
                greek = q.symbol,
                prompt = "Is ${q.label} long or short?",
                options = quantityOptions,
                correctIndex = quantityOptions.indexOf(q.answer),
                explanation = "${q.symbol} (${q.label}) is ${q.answer}: ${q.detail}.",
            )
        }
        val diphthongQuestions = DIPHTHONGS.map { d ->
            mcq(
                greek = d.glyphs,
                prompt = "How does this diphthong sound?",
                correct = d.sound,
                distractors = DIPHTHONGS.filter { it.glyphs != d.glyphs }
                    .shuffled(random).take(3).map { it.sound },
                explanation = "${d.glyphs} sounds like ${d.sound}.",
                random = random,
            )
        }
        return quantityQuestions + diphthongQuestions
    }

    // ---------------------------------------------------------------- unit 5

    private data class BreathingWord(
        val greek: String,
        val rough: Boolean,
        val withH: String,
        val withoutH: String,
        val gloss: String,
    )

    private val BREATHING_WORDS = listOf(
        BreathingWord("ὁδός", true, "hodós", "odós", "road"),
        BreathingWord("ἐγώ", false, "hegṓ", "egṓ", "I"),
        BreathingWord("ἵππος", true, "híppos", "íppos", "horse"),
        BreathingWord("οὗτος", true, "hoûtos", "oûtos", "this"),
        BreathingWord("εἰμί", false, "heimí", "eimí", "I am"),
        BreathingWord("ὕδωρ", true, "húdōr", "údōr", "water"),
        BreathingWord("ἄνθρωπος", false, "hánthrōpos", "ánthrōpos", "human being"),
        BreathingWord("ἡμέρα", true, "hēméra", "ēméra", "day"),
        BreathingWord("οἶκος", false, "hoîkos", "oîkos", "house"),
        BreathingWord("αἷμα", true, "haîma", "aîma", "blood"),
        BreathingWord("ἔργον", false, "hérgon", "érgon", "work"),
        BreathingWord("ὥρα", true, "hṓra", "ṓra", "season, hour"),
    )

    private val BREATHINGS_LESSON = UnitLesson(
        unit = 5,
        title = "Breathings & words",
        intro = listOf(
            "Every Greek word that begins with a vowel or diphthong wears a " +
                "breathing mark. The rough breathing (῾, the hook opening right) " +
                "means: say h first. The smooth breathing (᾿, opening left) means: " +
                "no h. Greek has no letter h — this mark is it.",
            "Two rules of thumb: a word-initial υ or ρ always takes the rough " +
                "breathing (ὕδωρ, ῥήτωρ), and on a diphthong the mark sits on the " +
                "second vowel (οὗτος) — but the h is still said first.",
        ),
        rows = listOf(
            LessonRow("ἁ", "rough breathing ῾", "say h first: “ha”"),
            LessonRow("ἀ", "smooth breathing ᾿", "no h: plain “a”"),
            LessonRow("ὕ", "initial upsilon", "always rough: húdōr"),
            LessonRow("ῥ", "initial rho", "always rough: rhḗtōr"),
            LessonRow("οὗ", "on a diphthong", "mark on the 2nd vowel, h still first"),
        ),
        hasQuiz = true,
    )

    private fun breathingQuiz(random: Random): List<UnitQuizQuestion> =
        BREATHING_WORDS.map { w ->
            mcq(
                greek = w.greek,
                prompt = "Pick the correct reading (“${w.gloss}”)",
                correct = if (w.rough) w.withH else w.withoutH,
                distractors = listOf(if (w.rough) w.withoutH else w.withH),
                explanation = if (w.rough) {
                    "${w.greek} carries the rough breathing (῾) — it begins with h."
                } else {
                    "${w.greek} carries the smooth breathing (᾿) — no h."
                },
                random = random,
            )
        }

    // ---------------------------------------------------------------- unit 6

    private data class AccentWord(val greek: String, val accent: String, val gloss: String)

    private val ACCENT_WORDS = listOf(
        AccentWord("λόγος", ACUTE, "word"),
        AccentWord("καλός", ACUTE, "beautiful"),
        AccentWord("ἄνθρωπος", ACUTE, "human being"),
        AccentWord("ποταμός", ACUTE, "river"),
        AccentWord("θάλαττα", ACUTE, "sea"),
        AccentWord("καὶ …", GRAVE, "and"),
        AccentWord("τὸν …", GRAVE, "the"),
        AccentWord("ἀγαθὸς …", GRAVE, "good"),
        AccentWord("σῶμα", CIRCUMFLEX, "body"),
        AccentWord("δῶρον", CIRCUMFLEX, "gift"),
        AccentWord("νοῦς", CIRCUMFLEX, "mind"),
        AccentWord("γῆ", CIRCUMFLEX, "earth"),
        AccentWord("τιμῶ", CIRCUMFLEX, "I honor"),
    )

    private val ACCENTS_LESSON = UnitLesson(
        unit = 6,
        title = "Accents (recognition)",
        intro = listOf(
            "Attic accents were musical — a rise or fall in pitch, not a louder " +
                "syllable. For now you only need to recognize the three marks; the " +
                "rules for placing them come later, with grammar.",
            "Acute (´): the pitch rises. Grave (`): an acute on a word's final " +
                "syllable flattens when another word follows — you'll only ever see " +
                "it mid-sentence. Circumflex (῀): the pitch rises and falls inside " +
                "one syllable, so it needs a long vowel or a diphthong.",
        ),
        rows = listOf(
            LessonRow("ά", "acute · oxeîa", "rising pitch; any of the last 3 syllables"),
            LessonRow("ὰ", "grave · bareîa", "flattened acute; final syllable, mid-sentence"),
            LessonRow("ᾶ", "circumflex · perispōménē", "rise-and-fall; long vowels & diphthongs only"),
        ),
        hasQuiz = true,
    )

    private fun accentQuiz(random: Random): List<UnitQuizQuestion> {
        val options = listOf(ACUTE, GRAVE, CIRCUMFLEX)
        return ACCENT_WORDS.map { w ->
            UnitQuizQuestion(
                greek = w.greek,
                prompt = "Which accent does this word carry? (“${w.gloss}”)",
                options = options,
                correctIndex = options.indexOf(w.accent),
                explanation = when (w.accent) {
                    ACUTE -> "${w.greek}: the acute (´) — rising pitch."
                    GRAVE -> "${w.greek}: the grave (`) — a flattened acute; " +
                        "it appears only when another word follows."
                    else -> "${w.greek}: the circumflex (῀) — rise-then-fall, " +
                        "which needs a long vowel or diphthong."
                },
            )
        }
    }

    // ------------------------------------------------------------- helpers

    private fun mcq(
        greek: String?,
        prompt: String,
        correct: String,
        distractors: List<String>,
        explanation: String,
        random: Random,
    ): UnitQuizQuestion {
        val options = (distractors + correct).distinct().shuffled(random)
        return UnitQuizQuestion(
            greek = greek,
            prompt = prompt,
            options = options,
            correctIndex = options.indexOf(correct),
            explanation = explanation,
        )
    }

    private const val ALWAYS_LONG = "always long"
    private const val ALWAYS_SHORT = "always short"
    private const val VARIES = "long or short — the word decides"
    private const val ACUTE = "acute ´"
    private const val GRAVE = "grave `"
    private const val CIRCUMFLEX = "circumflex ῀"
}
