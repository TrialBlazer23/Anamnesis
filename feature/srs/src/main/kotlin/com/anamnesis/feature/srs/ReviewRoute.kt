package com.anamnesis.feature.srs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anamnesis.core.domain.model.Card
import com.anamnesis.core.domain.repository.SrsRepository

/**
 * Stateful entry point for the review (training) screen.
 *
 * [vocabUnlocked] gates the introduction of new vocabulary cards (letters are
 * always available) — wired to the Learn tab's alphabet completion.
 * [onOpenLearn] jumps to the Learn tab when training is waiting on it.
 */
@Composable
fun ReviewRoute(
    repository: SrsRepository,
    seeds: suspend () -> List<Card>,
    modifier: Modifier = Modifier,
    vocabUnlocked: () -> Boolean = { true },
    onOpenLearn: () -> Unit = {},
) {
    val viewModel: ReviewViewModel =
        viewModel(factory = ReviewViewModel.Factory(repository, seeds, vocabUnlocked))
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Re-entering the tab between sessions picks up newly unlocked decks
    // (e.g. the alphabet was just completed in Learn).
    LaunchedEffect(Unit) { viewModel.refreshIfDone() }

    ReviewScreen(
        state = state,
        onReveal = viewModel::reveal,
        onGrade = viewModel::grade,
        onRestart = viewModel::loadDue,
        onStudyMoreNew = viewModel::studyMoreNew,
        onOpenLearn = onOpenLearn,
        modifier = modifier,
    )
}
