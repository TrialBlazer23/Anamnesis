package com.anamnesis.feature.learn.drills

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anamnesis.core.ui.GentiumPlus

/** One tappable answer. [greek] renders the label in the Greek font. */
data class DrillOption(
    val id: String,
    val label: String,
    val greek: Boolean = false,
)

/** A generic multiple-choice drill question (units 4–6). */
data class DrillQuestion(
    val promptTitle: String,
    val prompt: String,
    val promptGreek: Boolean = false,
    val promptCaption: String? = null,
    val options: List<DrillOption>,
    val correctId: String,
    /** Shown after answering — the teaching point behind the item. */
    val explanation: String? = null,
)

/**
 * Generic multiple-choice drill runner: progress, prompt, option grid with
 * the answered/correct/error coloring of the letters PracticeScreen, and a
 * score screen with a pass banner. [onComplete] fires once per finished deck.
 */
@Composable
fun DrillScreen(
    title: String,
    passThreshold: Double,
    deckFactory: () -> List<DrillQuestion>,
    onBack: () -> Unit,
    onComplete: (score: Int, total: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var deck by remember { mutableStateOf(deckFactory()) }
    var index by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var answeredId by remember { mutableStateOf<String?>(null) }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onBack) { Text("‹ Back") }
            Spacer(Modifier.width(4.dp))
            Text(title, style = MaterialTheme.typography.titleMedium)
        }

        val current = deck.getOrNull(index)
        if (current == null) {
            LaunchedEffect(deck) { onComplete(score, deck.size) }
            val passed = deck.isNotEmpty() && score.toDouble() / deck.size >= passThreshold
            Column(
                Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Score: $score / ${deck.size}", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(6.dp))
                if (passed) {
                    Text(
                        "Passed! (≥${(passThreshold * 100).toInt()}%)",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium,
                    )
                } else {
                    Text(
                        "Pass mark: ${(passThreshold * 100).toInt()}%",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                Spacer(Modifier.height(16.dp))
                Button(onClick = {
                    deck = deckFactory()
                    index = 0; score = 0; answeredId = null
                }) { Text("Practice again") }
            }
            return@Column
        }

        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("${index + 1} / ${deck.size}", style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(16.dp))
            Text(current.promptTitle, style = MaterialTheme.typography.titleMedium)
            if (current.promptGreek) {
                Text(current.prompt, fontFamily = GentiumPlus, fontSize = 56.sp)
            } else {
                Text(
                    current.prompt,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                )
            }
            current.promptCaption?.let { caption ->
                Text(
                    caption,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(24.dp))

            // Short labels sit two to a row; long ones get the full width.
            val perRow = if (current.options.all { it.label.length <= 10 }) 2 else 1
            current.options.chunked(perRow).forEach { rowOptions ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    rowOptions.forEach { option ->
                        val isAnswered = answeredId != null
                        val isCorrect = option.id == current.correctId
                        val container = when {
                            !isAnswered -> MaterialTheme.colorScheme.surfaceVariant
                            isCorrect -> MaterialTheme.colorScheme.primaryContainer
                            option.id == answeredId -> MaterialTheme.colorScheme.errorContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                        Button(
                            onClick = {
                                answeredId = option.id
                                if (isCorrect) score += 1
                            },
                            enabled = !isAnswered,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = container,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                            ),
                            modifier = Modifier.weight(1f).heightIn(min = 64.dp),
                        ) {
                            if (option.greek) {
                                Text(option.label, fontFamily = GentiumPlus, fontSize = 26.sp)
                            } else {
                                Text(option.label, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            if (answeredId != null) {
                current.explanation?.takeIf { it.isNotBlank() }?.let { explanation ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        ),
                    ) {
                        Text(
                            explanation,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                }
                Button(
                    onClick = { index += 1; answeredId = null },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text(if (index + 1 < deck.size) "Next" else "Finish") }
            }
        }
    }
}
