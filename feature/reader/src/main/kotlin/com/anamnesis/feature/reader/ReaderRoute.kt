package com.anamnesis.feature.reader

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anamnesis.core.domain.repository.ReaderRepository

/**
 * Stateful entry point: builds a [ReaderViewModel] from [repository], observes
 * its state, and renders loading / empty / content.
 */
@Composable
fun ReaderRoute(repository: ReaderRepository, modifier: Modifier = Modifier) {
    val viewModel: ReaderViewModel = viewModel(factory = ReaderViewModel.Factory(repository))
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val current = state) {
        is ReaderUiState.Loading ->
            Box(modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
        is ReaderUiState.Empty ->
            Box(modifier.fillMaxSize(), Alignment.Center) { Text("No passages available.") }
        is ReaderUiState.Content ->
            ReaderScreen(passages = current.passages, modifier = modifier)
    }
}
