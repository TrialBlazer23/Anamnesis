package com.anamnesis.feature.srs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anamnesis.core.domain.model.Card
import com.anamnesis.core.domain.repository.SrsRepository

/** Stateful entry point for the review (training) screen. */
@Composable
fun ReviewRoute(
    repository: SrsRepository,
    seeds: suspend () -> List<Card>,
    modifier: Modifier = Modifier,
) {
    val viewModel: ReviewViewModel =
        viewModel(factory = ReviewViewModel.Factory(repository, seeds))
    val state by viewModel.state.collectAsStateWithLifecycle()

    ReviewScreen(
        state = state,
        onReveal = viewModel::reveal,
        onGrade = viewModel::grade,
        onRestart = viewModel::loadDue,
        modifier = modifier,
    )
}
