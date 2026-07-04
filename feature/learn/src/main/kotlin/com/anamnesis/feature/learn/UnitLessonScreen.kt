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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.anamnesis.feature.learn.data.SoundUnits
import com.anamnesis.feature.learn.model.UnitLesson
import com.anamnesis.feature.learn.progress.UnitGating
import kotlin.random.Random

/**
 * A curriculum unit opened from the roadmap: teaching content first, then (for
 * units with a quiz) a ≥90% recognition quiz that completes the unit.
 */
@Composable
fun UnitLessonRoute(
    unit: Int,
    completed: Boolean,
    onComplete: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val lesson = SoundUnits.lesson(unit)
    if (lesson == null) {
        // Only units with authored content are routed here; recover gracefully.
        LaunchedEffect(unit) { onBack() }
        return
    }
    var inQuiz by remember { mutableStateOf(false) }

    if (inQuiz) {
        UnitQuizScreen(
            lesson = lesson,
            onPassed = { onComplete(unit) },
            onBack = { inQuiz = false },
            modifier = modifier,
        )
    } else {
        UnitLessonContent(
            lesson = lesson,
            completed = completed,
            onStartQuiz = { inQuiz = true },
            onMarkRead = { onComplete(unit); onBack() },
            onBack = onBack,
            modifier = modifier,
        )
    }
}

@Composable
private fun UnitLessonContent(
    lesson: UnitLesson,
    completed: Boolean,
    onStartQuiz: () -> Unit,
    onMarkRead: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        BackRow(onBack, "${lesson.unit}. ${lesson.title}")
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        ) {
            lesson.intro.forEach { paragraph ->
                Text(paragraph, style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(12.dp))
            }

            if (lesson.rows.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                lesson.rows.forEach { row ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        ),
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                row.symbol,
                                fontFamily = GentiumPlus,
                                fontSize = 28.sp,
                                modifier = Modifier.width(64.dp),
                            )
                            Column {
                                Text(
                                    row.label,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                                Text(row.detail, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            if (lesson.hasQuiz) {
                if (completed) {
                    Text(
                        "✓ Unit complete — quiz again any time.",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.height(8.dp))
                }
                Button(onClick = onStartQuiz, modifier = Modifier.fillMaxWidth()) {
                    Text("Start the quiz  ·  pass with 90%")
                }
            } else {
                Button(onClick = onMarkRead, modifier = Modifier.fillMaxWidth()) {
                    Text(if (completed) "✓ Read — back to the roadmap" else "Got it — mark as read")
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun UnitQuizScreen(
    lesson: UnitLesson,
    onPassed: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier,
) {
    val random = remember { Random(System.currentTimeMillis()) }
    var deck by remember { mutableStateOf(SoundUnits.quiz(lesson.unit, random)) }
    var index by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var answered by remember { mutableStateOf<Int?>(null) }

    Column(modifier = modifier.fillMaxSize()) {
        BackRow(onBack, "${lesson.title} · quiz")

        val question = deck.getOrNull(index)
        if (question == null) {
            val passed = deck.isNotEmpty() &&
                score.toDouble() / deck.size >= UnitGating.PASS_THRESHOLD
            LaunchedEffect(deck, passed) { if (passed) onPassed() }
            Column(
                Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Score: $score / ${deck.size}", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(6.dp))
                if (passed) {
                    Text(
                        "Passed! Unit ${lesson.unit} complete.",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium,
                    )
                } else {
                    Text(
                        "You need 90%. Review the lesson and try again — it sticks fast.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )
                }
                Spacer(Modifier.height(16.dp))
                Button(onClick = {
                    deck = SoundUnits.quiz(lesson.unit, random)
                    index = 0
                    score = 0
                    answered = null
                }) { Text("Quiz again") }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = onBack) { Text("Back to the lesson") }
            }
            return@Column
        }

        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("${index + 1} / ${deck.size}", style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(16.dp))
            question.greek?.let { greek ->
                Text(greek, fontFamily = GentiumPlus, fontSize = 44.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(8.dp))
            }
            Text(
                question.prompt,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(20.dp))

            question.options.forEachIndexed { optionIndex, option ->
                val isAnswered = answered != null
                val container = when {
                    !isAnswered -> MaterialTheme.colorScheme.surfaceVariant
                    optionIndex == question.correctIndex -> MaterialTheme.colorScheme.primaryContainer
                    optionIndex == answered -> MaterialTheme.colorScheme.errorContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
                Button(
                    onClick = {
                        answered = optionIndex
                        if (optionIndex == question.correctIndex) score += 1
                    },
                    enabled = answered == null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = container,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        disabledContainerColor = container,
                        disabledContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                ) {
                    Text(option, textAlign = TextAlign.Center)
                }
            }

            if (answered != null) {
                Spacer(Modifier.height(12.dp))
                Text(
                    question.explanation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        index += 1
                        answered = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text(if (index + 1 < deck.size) "Next" else "Finish") }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
