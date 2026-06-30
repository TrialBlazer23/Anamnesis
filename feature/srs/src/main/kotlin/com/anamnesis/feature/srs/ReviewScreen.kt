package com.anamnesis.feature.srs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
    modifier: Modifier = Modifier,
) {
    when (state) {
        is ReviewUiState.Loading ->
            Box(modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }

        is ReviewUiState.Done ->
            Box(modifier.fillMaxSize(), Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("All caught up.", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(16.dp))
                    OutlinedButton(onClick = onRestart) { Text("Check again") }
                }
            }

        is ReviewUiState.Reviewing ->
            ReviewingContent(state, onReveal, onGrade, modifier)
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
        Text(
            text = "${state.remaining} due",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End,
        )

        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                GradeButton("Again", Modifier.weight(1f)) { onGrade(Rating.Again) }
                GradeButton("Hard", Modifier.weight(1f)) { onGrade(Rating.Hard) }
                GradeButton("Good", Modifier.weight(1f)) { onGrade(Rating.Good) }
                GradeButton("Easy", Modifier.weight(1f)) { onGrade(Rating.Easy) }
            }
        } else {
            Button(onClick = onReveal, modifier = Modifier.fillMaxWidth()) { Text("Show answer") }
        }
    }
}

@Composable
private fun GradeButton(label: String, modifier: Modifier, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = modifier) { Text(label) }
}
