package com.anamnesis.feature.srs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anamnesis.core.ui.GentiumPlus

@Composable
fun ReviewScreen(
    state: ReviewUiState,
    onReveal: () -> Unit,
    onGrade: (Rating) -> Unit,
    onRestart: () -> Unit,
    onStudyMoreNew: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        is ReviewUiState.Loading ->
            Box(modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }

        is ReviewUiState.Done ->
            DoneContent(state, onRestart, onStudyMoreNew, modifier)

        is ReviewUiState.Reviewing ->
            ReviewingContent(state, onReveal, onGrade, modifier)
    }
}

@Composable
private fun DoneContent(
    state: ReviewUiState.Done,
    onRestart: () -> Unit,
    onStudyMoreNew: () -> Unit,
    modifier: Modifier,
) {
    Box(modifier.fillMaxSize().padding(24.dp), Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (state.completed > 0) {
                Text("Session complete!", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text(
                    "${state.completed} card${if (state.completed == 1) "" else "s"} studied" +
                        if (state.again > 0) " · ${state.again} needed a retry" else "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            } else {
                Text("All caught up.", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Nothing is due right now. Come back tomorrow —\nspacing out reviews is what makes them stick.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(Modifier.height(24.dp))
            if (state.hasMoreNew) {
                Button(onClick = onStudyMoreNew) { Text("Learn more new words") }
                Spacer(Modifier.height(8.dp))
            }
            OutlinedButton(onClick = onRestart) { Text("Check again") }
        }
    }
}

@Composable
private fun ReviewingContent(
    state: ReviewUiState.Reviewing,
    onReveal: () -> Unit,
    onGrade: (Rating) -> Unit,
    modifier: Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SessionHeader(state)

        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (state.card.isNew) {
                    NewWordBadge()
                    Spacer(Modifier.height(12.dp))
                }
                Text(
                    text = state.card.lemma,
                    fontFamily = GentiumPlus,
                    fontSize = 34.sp,
                    textAlign = TextAlign.Center,
                )
                if (state.revealed) {
                    Spacer(Modifier.height(20.dp))
                    if (state.card.partOfSpeech.isNotBlank()) {
                        Text(
                            text = state.card.partOfSpeech,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(Modifier.height(4.dp))
                    }
                    Text(
                        text = state.card.gloss,
                        fontFamily = GentiumPlus,
                        fontSize = 18.sp,
                        lineHeight = 26.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        if (state.revealed) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Rating.entries.forEach { rating ->
                    GradeButton(
                        label = rating.name,
                        hint = state.intervalHints[rating].orEmpty(),
                        modifier = Modifier.weight(1f),
                    ) { onGrade(rating) }
                }
            }
        } else {
            Button(onClick = onReveal, modifier = Modifier.fillMaxWidth()) { Text("Show answer") }
        }
    }
}

@Composable
private fun SessionHeader(state: ReviewUiState.Reviewing) {
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = buildString {
                    if (state.newRemaining > 0) append("${state.newRemaining} new")
                    if (state.newRemaining > 0 && state.reviewRemaining > 0) append(" · ")
                    if (state.reviewRemaining > 0) append("${state.reviewRemaining} review")
                },
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "${state.completed} / ${state.sessionTotal}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = {
                if (state.sessionTotal == 0) 0f
                else state.completed.toFloat() / state.sessionTotal
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun NewWordBadge() {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.tertiaryContainer,
    ) {
        Text(
            text = "NEW",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}

@Composable
private fun GradeButton(
    label: String,
    hint: String,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, maxLines = 1)
            Text(
                text = hint,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
        }
    }
}
