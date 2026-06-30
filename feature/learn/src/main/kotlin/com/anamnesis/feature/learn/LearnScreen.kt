package com.anamnesis.feature.learn

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anamnesis.core.ui.GentiumPlus
import com.anamnesis.feature.learn.data.ALPHABET
import com.anamnesis.feature.learn.data.ALPHABET_BATCHES
import com.anamnesis.feature.learn.data.CURRICULUM
import com.anamnesis.feature.learn.model.LetterLesson
import kotlin.random.Random

private sealed interface LearnNav {
    data object Home : LearnNav
    data object Alphabet : LearnNav
    data class Detail(val letter: LetterLesson) : LearnNav
    data object Practice : LearnNav
}

/** The Learn tab: a visual on-ramp (browse the alphabet, practice recognition). */
@Composable
fun LearnRoute(modifier: Modifier = Modifier) {
    var nav by remember { mutableStateOf<LearnNav>(LearnNav.Home) }
    when (val current = nav) {
        LearnNav.Home -> LearnHome(
            modifier = modifier,
            onBrowse = { nav = LearnNav.Alphabet },
            onPractice = { nav = LearnNav.Practice },
        )
        LearnNav.Alphabet -> AlphabetScreen(
            modifier = modifier,
            onBack = { nav = LearnNav.Home },
            onLetter = { nav = LearnNav.Detail(it) },
        )
        is LearnNav.Detail -> LetterDetailScreen(
            letter = current.letter,
            modifier = modifier,
            onBack = { nav = LearnNav.Alphabet },
        )
        LearnNav.Practice -> PracticeScreen(
            modifier = modifier,
            onBack = { nav = LearnNav.Home },
        )
    }
}

@Composable
private fun LearnHome(modifier: Modifier, onBrowse: () -> Unit, onPractice: () -> Unit) {
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

        Spacer(Modifier.height(24.dp))
        Text("Roadmap", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        CURRICULUM.forEach { unit ->
            val live = unit.number in 1..3
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (live) {
                        MaterialTheme.colorScheme.secondaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                ),
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text(
                        "${unit.number}. ${unit.title}${if (live) "" else "  ·  soon"}",
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Text(
                        unit.objective,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun AlphabetScreen(modifier: Modifier, onBack: () -> Unit, onLetter: (LetterLesson) -> Unit) {
    Column(modifier = modifier.fillMaxSize()) {
        BackRow(onBack, "The Alphabet")
        Column(Modifier.verticalScroll(rememberScrollState()).padding(16.dp)) {
            ALPHABET_BATCHES.forEach { batch ->
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
private fun PracticeScreen(modifier: Modifier, onBack: () -> Unit) {
    val random = remember { Random(System.currentTimeMillis()) }
    var deck by remember { mutableStateOf(AlphabetQuiz.deck(ALPHABET, random)) }
    var index by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var answered by remember { mutableStateOf<LetterLesson?>(null) }

    Column(modifier = modifier.fillMaxSize()) {
        BackRow(onBack, "Practice")
        val current = deck.getOrNull(index)
        if (current == null) {
            Column(
                Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Score: $score / ${deck.size}", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(16.dp))
                Button(onClick = {
                    deck = AlphabetQuiz.deck(ALPHABET, random)
                    index = 0; score = 0; answered = null
                }) { Text("Practice again") }
            }
            return@Column
        }

        val question = remember(index, deck) {
            AlphabetQuiz.question(current, ALPHABET, optionCount = 4, random = random)
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
private fun BackRow(onBack: () -> Unit, title: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(onClick = onBack) { Text("‹ Back") }
        Spacer(Modifier.width(4.dp))
        Text(title, style = MaterialTheme.typography.titleMedium)
    }
}
