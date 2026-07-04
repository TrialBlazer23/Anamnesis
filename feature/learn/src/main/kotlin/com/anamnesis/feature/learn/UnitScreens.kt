package com.anamnesis.feature.learn

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anamnesis.core.ui.GentiumPlus
import com.anamnesis.feature.learn.drills.DrillCatalog
import com.anamnesis.feature.learn.model.LessonPack
import com.anamnesis.feature.learn.progress.UnitGating

/**
 * A unit's lesson page: what the unit teaches (rendered from the pack), then
 * its drills with per-drill pass marks, then the advance criteria. Unit 0 is
 * a read-and-acknowledge orientation page.
 */
@Composable
internal fun UnitLessonScreen(
    pack: LessonPack,
    number: Int,
    passedGates: Set<String>,
    completed: Boolean,
    modifier: Modifier,
    onDrill: (String) -> Unit,
    onAcknowledge: () -> Unit,
    onBack: () -> Unit,
) {
    val unit = pack.units.first { it.number == number } // pack validates 0..8
    Column(modifier = modifier.fillMaxSize()) {
        BackRow(onBack, "Unit $number · ${unit.title}")
        Column(Modifier.verticalScroll(rememberScrollState()).padding(16.dp)) {
            Text(
                unit.objective + if (completed) "  ·  ✓ complete" else "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
            LessonCard(unit.taught)
            Spacer(Modifier.height(16.dp))

            when (number) {
                4 -> Unit4Sections(pack)
                5 -> Unit5Sections(pack)
            }

            val drills = unit.drills.filter(DrillCatalog::isBuilt)
            if (drills.isNotEmpty()) {
                SectionTitle("Drills")
                drills.forEach { drillId ->
                    val gate = UnitGating.gateForDrill(drillId)
                    val passed = gate != null && gate in passedGates
                    val percent = (UnitGating.drillThreshold(drillId) * 100).toInt()
                    OutlinedButton(
                        onClick = { onDrill(drillId) },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    ) {
                        Text(
                            (if (passed) "✓ " else "") + DrillCatalog.label(drillId) +
                                if (gate != null) "  ·  pass at $percent%" else "",
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (completed) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                    ),
                ) {
                    Text(
                        if (completed) "✓ Unit complete." else "To complete: ${unit.advance}",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            if (number == 0 && !completed) {
                Spacer(Modifier.height(8.dp))
                Button(onClick = onAcknowledge, modifier = Modifier.fillMaxWidth()) {
                    Text("Got it — on to the alphabet")
                }
            }
        }
    }
}

@Composable
private fun Unit4Sections(pack: LessonPack) {
    SectionTitle("Length pairs")
    Text(
        "Greek vowels come in short/long pairs — roughly the same sound held about " +
            "twice as long (with a quality shift for e and o).",
        style = MaterialTheme.typography.bodyMedium,
    )
    Spacer(Modifier.height(8.dp))
    pack.minimalPairs
        .filter { it.type == "vowel-length" || it.type == "hidden-quantity" }
        .forEach { pair ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            ) {
                Row(
                    Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "${pair.a} · ${pair.b}",
                        fontFamily = GentiumPlus,
                        fontSize = 26.sp,
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(pair.contrast, style = MaterialTheme.typography.bodyMedium)
                        if (pair.note.isNotBlank()) {
                            Text(
                                pair.note,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    Spacer(Modifier.height(16.dp))

    SectionTitle("The eight diphthongs")
    val (improper, proper) = pack.diphthongs.partition { it.improper }
    proper.forEach { DiphthongRow(it.glyph, it.ipa, it.note) }
    Spacer(Modifier.height(8.dp))
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
        ),
    ) {
        Text(
            "Watch out: ει and ου only look like diphthongs — in Classical Attic they " +
                "are pure long vowels, [eː] and [uː], with no glide.",
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
    Spacer(Modifier.height(16.dp))

    SectionTitle("Improper diphthongs (iota subscript)")
    Text(
        "A tiny ι written under a long vowel. It is NOT pronounced — it is a " +
            "spelling cue (you will meet it in dative endings).",
        style = MaterialTheme.typography.bodyMedium,
    )
    Spacer(Modifier.height(8.dp))
    improper.forEach { DiphthongRow(it.glyph, it.ipa, it.note) }
    Spacer(Modifier.height(16.dp))
}

@Composable
private fun Unit5Sections(pack: LessonPack) {
    SectionTitle("The two breathings")
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(Modifier.padding(12.dp)) {
            Text("Rough  ῾", style = MaterialTheme.typography.titleSmall)
            Text(
                "Say [h] before the vowel: ὁ = ho, ὕδωρ = húdōr.",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(Modifier.padding(12.dp)) {
            Text("Smooth  ᾿", style = MaterialTheme.typography.titleSmall)
            Text(
                "Adds nothing — it just marks the absence of [h]: ἐν = en, αὐτός = autós.",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
    Spacer(Modifier.height(8.dp))
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
    ) {
        Text(
            "Rules of thumb: every vowel-initial word carries one of the two marks. " +
                "Initial υ and ρ regularly take the rough breathing (ὑπό, ῥᾴδιος). " +
                "On a diphthong the mark sits on the second letter (οὗτος = hoûtos); " +
                "beside a capital it stands in front (Ὅμηρος).",
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
    Spacer(Modifier.height(16.dp))

    SectionTitle("Sound reminders in words")
    Text(
        "γ before γ/κ/χ/ξ is the nasal [ŋ] (ἄγγελος). ρ is trilled, and voiceless " +
            "at the start of a word (ῥ). σ is [z] before voiced consonants.",
        style = MaterialTheme.typography.bodyMedium,
    )
    Spacer(Modifier.height(16.dp))

    SectionTitle("The first ${pack.words.size} core words")
    Text(
        "The most frequent words of classical Greek (DCC core list) — you will " +
            "meet them constantly in the reader and in Train.",
        style = MaterialTheme.typography.bodyMedium,
    )
    Spacer(Modifier.height(8.dp))
    pack.words.forEach { word ->
        Row(
            Modifier.fillMaxWidth().padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                word.greek,
                fontFamily = GentiumPlus,
                fontSize = 22.sp,
                modifier = Modifier.width(104.dp),
            )
            Column {
                Text(
                    "${word.translit}  ·  ${word.ipa}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    word.gloss,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
    Spacer(Modifier.height(16.dp))
}

@Composable
private fun DiphthongRow(glyph: String, ipa: String, note: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(glyph, fontFamily = GentiumPlus, fontSize = 26.sp, modifier = Modifier.width(52.dp))
        Text(
            ipa,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(72.dp),
        )
        Text(
            note,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
internal fun SectionTitle(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
    )
    Spacer(Modifier.height(8.dp))
}

@Composable
internal fun LessonCard(text: String) {
    if (text.isBlank()) return
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
    ) {
        Text(
            text,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
