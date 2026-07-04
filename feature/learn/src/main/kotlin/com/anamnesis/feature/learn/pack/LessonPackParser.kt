package com.anamnesis.feature.learn.pack

import com.anamnesis.feature.learn.model.Accent
import com.anamnesis.feature.learn.model.AccentItem
import com.anamnesis.feature.learn.model.AccentPair
import com.anamnesis.feature.learn.model.Breathing
import com.anamnesis.feature.learn.model.DiphthongLesson
import com.anamnesis.feature.learn.model.LearnUnit
import com.anamnesis.feature.learn.model.LessonPack
import com.anamnesis.feature.learn.model.LetterLesson
import com.anamnesis.feature.learn.model.MinimalPair
import com.anamnesis.feature.learn.model.WordLesson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Pure-Kotlin parser for the bundled `lessons.json` (no Android deps, so it is
 * JVM-unit-testable against the committed asset). The wire DTOs mirror the
 * snake_case pack schema emitted by `pipeline/build_lessons.py`.
 */
object LessonPackParser {
    const val SUPPORTED_SCHEMA_VERSION = 1

    private val json = Json { ignoreUnknownKeys = true }

    fun parse(text: String): LessonPack {
        val dto = json.decodeFromString<PackDto>(text)
        require(dto.schemaVersion == SUPPORTED_SCHEMA_VERSION) {
            "lessons pack schema_version ${dto.schemaVersion} != " +
                "supported $SUPPORTED_SCHEMA_VERSION — regenerate with build_lessons.py"
        }
        return LessonPack(
            schemaVersion = dto.schemaVersion,
            scheme = dto.scheme,
            units = dto.units.map { u ->
                LearnUnit(
                    number = u.number,
                    title = u.title,
                    objective = u.objective,
                    taught = u.taught,
                    drills = u.drills,
                    srsFeed = u.srsFeed,
                    advance = u.advance,
                    batch = u.batch,
                )
            },
            letters = dto.letters.map { l ->
                LetterLesson(
                    lower = l.lower,
                    upper = l.upper,
                    nameGreek = l.nameGreek,
                    nameTranslit = l.nameTranslit,
                    ipa = l.ipa,
                    batch = l.batch,
                    latinLookalike = l.latinLookalike,
                    falseFriend = l.falseFriend,
                    multistroke = l.multistroke,
                    teachingNote = l.teachingNote,
                    audioId = l.audioSoundId,
                )
            },
            diphthongs = dto.diphthongs.map { d ->
                DiphthongLesson(
                    glyph = d.glyph,
                    ipa = d.ipa,
                    improper = d.kind == "improper",
                    note = d.note,
                    audioId = d.audioId,
                )
            },
            minimalPairs = dto.minimalPairs.map { p ->
                MinimalPair(
                    id = p.id,
                    a = p.a,
                    b = p.b,
                    contrast = p.contrast,
                    type = p.type,
                    note = p.note,
                    audioAId = p.audioAId,
                    audioBId = p.audioBId,
                )
            },
            words = dto.words.map { w ->
                WordLesson(
                    dccRank = w.dccRank,
                    greek = w.greek,
                    translit = w.translit,
                    ipa = w.ipa,
                    breathing = Breathing.from(w.breathing),
                    gloss = w.gloss,
                    pos = w.pos,
                    note = w.note,
                    audioId = w.audioId,
                )
            },
            accentItems = dto.accentItems.map { a ->
                AccentItem(
                    id = a.id,
                    word = a.word,
                    accent = Accent.from(a.accent),
                    gloss = a.gloss,
                    note = a.note,
                    audioId = a.audioId,
                )
            },
            accentPairs = dto.accentPairs.map { p ->
                AccentPair(
                    id = p.id,
                    a = p.a,
                    b = p.b,
                    same = p.same,
                    note = p.note,
                    audioAId = p.audioAId,
                    audioBId = p.audioBId,
                )
            },
        )
    }
}

@Serializable
private data class PackDto(
    @SerialName("schema_version") val schemaVersion: Int,
    val scheme: String,
    val units: List<UnitDto>,
    val letters: List<LetterDto>,
    val diphthongs: List<DiphthongDto>,
    @SerialName("minimal_pairs") val minimalPairs: List<MinimalPairDto>,
    val words: List<WordDto>,
    @SerialName("accent_items") val accentItems: List<AccentItemDto>,
    @SerialName("accent_pairs") val accentPairs: List<AccentPairDto>,
)

@Serializable
private data class UnitDto(
    val number: Int,
    val title: String,
    val objective: String,
    val taught: String = "",
    val drills: List<String> = emptyList(),
    @SerialName("srs_feed") val srsFeed: String = "",
    val advance: String = "",
    val batch: Int? = null,
)

@Serializable
private data class LetterDto(
    val lower: String,
    val upper: String,
    @SerialName("name_greek") val nameGreek: String,
    @SerialName("name_translit") val nameTranslit: String,
    val ipa: String,
    val batch: Int,
    @SerialName("latin_lookalike") val latinLookalike: String? = null,
    @SerialName("false_friend") val falseFriend: Boolean = false,
    val multistroke: Boolean = false,
    @SerialName("audio_sound_id") val audioSoundId: String? = null,
    @SerialName("teaching_note") val teachingNote: String = "",
)

@Serializable
private data class DiphthongDto(
    val glyph: String,
    val ipa: String,
    val kind: String,
    @SerialName("audio_id") val audioId: String? = null,
    val note: String = "",
)

@Serializable
private data class MinimalPairDto(
    val id: String,
    val a: String,
    val b: String,
    val contrast: String,
    val type: String,
    @SerialName("audio_a_id") val audioAId: String? = null,
    @SerialName("audio_b_id") val audioBId: String? = null,
    val note: String = "",
)

@Serializable
private data class WordDto(
    @SerialName("dcc_rank") val dccRank: Int,
    val greek: String,
    val translit: String,
    val ipa: String,
    val breathing: String,
    val gloss: String,
    val pos: String = "",
    @SerialName("audio_id") val audioId: String? = null,
    val note: String = "",
)

@Serializable
private data class AccentItemDto(
    val id: String,
    val word: String,
    val accent: String,
    val gloss: String,
    @SerialName("audio_id") val audioId: String? = null,
    val note: String = "",
)

@Serializable
private data class AccentPairDto(
    val id: String,
    val a: String,
    val b: String,
    val same: Boolean,
    @SerialName("audio_a_id") val audioAId: String? = null,
    @SerialName("audio_b_id") val audioBId: String? = null,
    val note: String = "",
)
