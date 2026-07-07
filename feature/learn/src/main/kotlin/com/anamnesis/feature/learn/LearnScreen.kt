package com.anamnesis.feature.learn

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anamnesis.core.ui.GentiumPlus
import com.anamnesis.feature.learn.drills.DrillCatalog
import com.anamnesis.feature.learn.drills.DrillScreen
import com.anamnesis.feature.learn.model.LessonPack
import com.anamnesis.feature.learn.model.LetterLesson
import com.anamnesis.feature.learn.model.letterBatches
import com.anamnesis.feature.learn.pack.rememberLessonPack
import com.anamnesis.feature.learn.progress.LearnProgressStore
import com.anamnesis.feature.learn.progress.UnitGating
import kotlin.random.Random

private sealed interface LearnNav {
    data object Home : LearnNav
    data object Alphabet : LearnNav
    data class Detail(val letter: LetterLesson) : LearnNav
    data class Practice(val initialBatch: Int? = null) : LearnNav
    data class UnitLesson(val number: Int) : LearnNav
    data class UnitDrill(val number: Int, val drillId: String) : LearnNav
}

/** The Learn tab: a visual on-ramp (browse the alphabet, practice recognition). */
@Composable
fun LearnRoute(modifier: Modifier = Modifier) {
    val pack = rememberLessonPack()
    if (pack == null) {
        // Asset parse is near-instant; a blank surface avoids a spinner flash.
        Column(modifier = modifier.fillMaxSize()) {}
        return
    }
    var nav by remember { mutableStateOf<LearnNav>(LearnNav.Home) }
    val context = LocalContext.current
    val store = remember { LearnProgressStore(context) }
    var completed by remember { mutableStateOf(store.completedUnits()) }
    var passedGates by remember { mutableStateOf(store.passedDrillGates()) }

    when (val current = nav) {
        LearnNav.Home -> LearnHome(
            pack = pack,
            completed = completed,
            modifier = modifier,
            onBrowse = { nav = LearnNav.Alphabet },
            onPractice = { nav = LearnNav.Practice() },
            onUnit = { unit ->
                nav = when (unit) {
                    // The alphabet units are drilled through letter practice,
                    // pre-scoped to the unit's quiz (batch 1, batch 2, mixed).
                    1 -> LearnNav.Practice(initialBatch = 1)
                    2 -> LearnNav.Practice(initialBatch = 2)
                    3 -> LearnNav.Practice(initialBatch = null)
                    else -> LearnNav.UnitLesson(unit)
                }
            },
        )
        LearnNav.Alphabet -> AlphabetScreen(
            pack = pack,
            modifier = modifier,
            onBack = { nav = LearnNav.Home },
            onLetter = { nav = LearnNav.Detail(it) },
        )
        is LearnNav.Detail -> LetterDetailScreen(
            letter = current.letter,
            modifier = modifier,
            onBack = { nav = LearnNav.Alphabet },
        )
        is LearnNav.Practice -> PracticeScreen(
            letters = pack.letters,
            modifier = modifier,
            initialBatch = current.initialBatch,
            onBack = { nav = LearnNav.Home },
            onSessionComplete = { batch, score, total ->
                UnitGating.unitForSession(batch, score, total)?.let { unit ->
                    store.markCompleted(unit)
                    completed = store.completedUnits()
                }
            },
        )
        is LearnNav.UnitLesson -> UnitLessonScreen(
            pack = pack,
            number = current.number,
            passedGates = passedGates,
            completed = current.number in completed,
            modifier = modifier,
            onDrill = { drillId -> nav = LearnNav.UnitDrill(current.number, drillId) },
            onAcknowledge = {
                store.markCompleted(current.number)
                completed = store.completedUnits()
            },
            onBack = { nav = LearnNav.Home },
        )
        is LearnNav.UnitDrill -> {
            val random = remember(current) { Random(System.currentTimeMillis()) }
            DrillScreen(
                title = DrillCatalog.label(current.drillId),
                passThreshold = UnitGating.drillThreshold(current.drillId),
                deckFactory = { DrillCatalog.deck(pack, current.drillId, random) },
                onBack = { nav = LearnNav.UnitLesson(current.number) },
                onComplete = { score, total ->
                    UnitGating.drillPassed(current.drillId, score, total)?.let { gate ->
                        store.markDrillGatePassed(gate)
                        passedGates = store.passedDrillGates()
                        if (UnitGating.unitCompleteFromDrills(current.number, passedGates)) {
                            store.markCompleted(current.number)
                            completed = store.completedUnits()
                        }
                    }
                },
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun LearnHome(
    pack: LessonPack,
    completed: Set<Int>,
    modifier: Modifier,
    onBrowse: () -> Unit,
    onPractice: () -> Unit,
    onUnit: (Int) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
    ) {
        Text("Learn Ancient Greek", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Start with the alphabet (restored Classical Attic), then build toward reading.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(20.dp))
        Button(onClick = onBrowse, modifier = Modifier.fillMaxWidth()) { Text("Browse the alphabet") }
        Spacer(Modifier.height(10.dp))
        OutlinedButton(onClick = onPractice, modifier = Modifier.fillMaxWidth()) { Text("Practice letters") }

        Spacer(Modifier.height(16.dp))
        val alphabetDone = UnitGating.alphabetComplete(completed)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (alphabetDone) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
            ),
        ) {
            Text(
                if (alphabetDone) {
                    "✓ Alphabet complete — vocabulary training is unlocked in the Train tab."
                } else {
                    "Finish units 1–3 to unlock vocabulary training in the Train tab. " +
                        "Until then, Train drills the letters."
                },
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Spacer(Modifier.height(24.dp))
        Text("Roadmap", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        pack.units.forEach { unit ->
            val built = UnitGating.isBuilt(unit.number)
            val done = unit.number in completed
            val unlocked = UnitGating.isUnlocked(unit.number, completed)
            // Units 1–3 deep-link into pre-scoped letter practice; unit 0 and
            // units 4+ open their own lesson page.
            val openable = built && unlocked
            val status = when {
                done -> "  ·  ✓ complete"
                !built -> "  ·  soon"
                !unlocked -> "  ·  🔒 finish unit ${unit.number - 1} first"
                else -> "  ·  tap to start"
            }
            val colors = CardDefaults.cardColors(
                containerColor = when {
                    done -> MaterialTheme.colorScheme.primaryContainer
                    openable -> MaterialTheme.colorScheme.secondaryContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                },
            )
            val cardModifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            val content: @Composable () -> Unit = {
                Column(Modifier.padding(12.dp)) {
                    Text(
                        "${unit.number}. ${unit.title}$status",
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Text(
                        unit.objective,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (openable) {
                Card(onClick = { onUnit(unit.number) }, modifier = cardModifier, colors = colors) {
                    content()
                }
            } else {
                Card(modifier = cardModifier, colors = colors) { content() }
            }
        }
    }
}

@Composable
private fun AlphabetScreen(
    pack: LessonPack,
    modifier: Modifier,
    onBack: () -> Unit,
    onLetter: (LetterLesson) -> Unit,
) {
    val batches = remember(pack) { pack.letterBatches() }
    Column(modifier = modifier.fillMaxSize()) {
        BackRow(onBack, "The Alphabet")
        Column(Modifier.verticalScroll(rememberScrollState()).padding(16.dp)) {
            batches.forEach { batch ->
                Text(
                    "${batch.number}. ${batch.title}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.height(8.dp))
                batch.letters.chunked(5).forEach { rowLetters ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowLetters.forEach { letter ->
                            OutlinedButton(
                                onClick = { onLetter(letter) },
                                modifier = Modifier.width(58.dp),
                            ) {
                                Text(letter.lower, fontFamily = GentiumPlus, fontSize = 24.sp)
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun LetterDetailScreen(letter: LetterLesson, modifier: Modifier, onBack: () -> Unit) {
    Column(modifier = modifier.fillMaxSize()) {
        BackRow(onBack, "${letter.nameTranslit} · ${letter.lower}")
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("${letter.lower}  ${letter.upper}", fontFamily = GentiumPlus, fontSize = 72.sp)
            Spacer(Modifier.height(8.dp))
            Text(letter.nameGreek, fontFamily = GentiumPlus, fontSize = 24.sp)
            Text(
                "${letter.nameTranslit}  ·  ${letter.ipa}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(20.dp))
            if (letter.falseFriend && letter.latinLookalike != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        "False friend: looks like Latin “${letter.latinLookalike}”, but sounds different.",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Spacer(Modifier.height(12.dp))
            }
            if (letter.teachingNote.isNotBlank()) {
                Text(
                    letter.teachingNote,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                if (letter.multistroke) "Handwriting: multi-stroke." else "Handwriting: single stroke.",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PracticeScreen(
    letters: List<LetterLesson>,
    modifier: Modifier,
    onBack: () -> Unit,
    initialBatch: Int? = null,
    onSessionComplete: (scopeBatch: Int?, score: Int, total: Int) -> Unit = { _, _, _ -> },
) {
    val random = remember { Random(System.currentTimeMillis()) }
    var scopeBatch by remember { mutableStateOf(initialBatch) } // null = all letters
    val pool = remember(scopeBatch, letters) {
        scopeBatch?.let { b -> letters.filter { it.batch == b } } ?: letters
    }
    var deck by remember(scopeBatch) { mutableStateOf(AlphabetQuiz.deck(pool, random)) }
    var index by remember(scopeBatch) { mutableIntStateOf(0) }
    var score by remember(scopeBatch) { mutableIntStateOf(0) }
    var answered by remember(scopeBatch) { mutableStateOf<LetterLesson?>(null) }

    Column(modifier = modifier.fillMaxSize()) {
        BackRow(onBack, "Practice")
        ScopeSelector(scopeBatch) { scopeBatch = it }

        val current = deck.getOrNull(index)
        if (current == null) {
            LaunchedEffect(scopeBatch, deck) {
                onSessionComplete(scopeBatch, score, deck.size)
            }
            val passed = deck.isNotEmpty() &&
                score.toDouble() / deck.size >= UnitGating.PASS_THRESHOLD
            Column(
                Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Score: $score / ${deck.size}", style = MaterialTheme.typography.headlineSmall)
                if (passed) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Passed! (≥90%)",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                Spacer(Modifier.height(16.dp))
                Button(onClick = {
                    deck = AlphabetQuiz.deck(pool, random)
                    index = 0; score = 0; answered = null
                }) { Text("Practice again") }
            }
            return@Column
        }

        val question = remember(index, deck) {
            AlphabetQuiz.question(current, letters, optionCount = 4, random = random)
        }
        Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${index + 1} / ${deck.size}", style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(16.dp))
            Text("Which letter is", style = MaterialTheme.typography.titleMedium)
            Text(
                "${current.nameTranslit}  ·  ${current.ipa}",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Text("(${current.nameGreek})", fontFamily = GentiumPlus, fontSize = 18.sp)
            Spacer(Modifier.height(24.dp))

            question.options.chunked(2).forEach { rowOptions ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    rowOptions.forEach { option ->
                        val isAnswered = answered != null
                        val isCorrect = option.lower == current.lower
                        val container = when {
                            !isAnswered -> MaterialTheme.colorScheme.surfaceVariant
                            isCorrect -> MaterialTheme.colorScheme.primaryContainer
                            option.lower == answered?.lower -> MaterialTheme.colorScheme.errorContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                        Button(
                            onClick = {
                                answered = option
                                if (isCorrect) score += 1
                            },
                            enabled = !isAnswered,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = container,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                            ),
                            modifier = Modifier.weight(1f).height(72.dp),
                        ) {
                            Text(option.lower, fontFamily = GentiumPlus, fontSize = 30.sp)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(8.dp))
            if (answered != null) {
                Button(
                    onClick = { index += 1; answered = null },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text(if (index + 1 < deck.size) "Next" else "Finish") }
            }
        }
    }
}

@Composable
private fun ScopeSelector(selected: Int?, onSelect: (Int?) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        ScopeChip("All", selected == null) { onSelect(null) }
        (1..4).forEach { batch ->
            ScopeChip(batch.toString(), selected == batch) { onSelect(batch) }
        }
    }
}

@Composable
private fun ScopeChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val padding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
    if (selected) {
        Button(onClick = onClick, contentPadding = padding) { Text(label) }
    } else {
        OutlinedButton(onClick = onClick, contentPadding = padding) { Text(label) }
    }
}

@Composable
internal fun BackRow(onBack: () -> Unit, title: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(onClick = onBack) { Text("‹ Back") }
        Spacer(Modifier.width(4.dp))
        Text(title, style = MaterialTheme.typography.titleMedium)
    }
}
