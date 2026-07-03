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
import com.anamnesis.core.domain.repository.RecitationRepository
import com.anamnesis.core.domain.repository.VocabularyRepository

/**
 * Stateful entry point: builds a [ReaderViewModel] and renders the reader.
 * Pass a distinct [contentKey] per content pack — the view-model (and its
 * loaded passages) is scoped to it, so switching packs reloads the text.
 */
@Composable
fun ReaderRoute(
    readerRepository: ReaderRepository,
    vocabularyRepository: VocabularyRepository,
    modifier: Modifier = Modifier,
    contentKey: String? = null,
    recitationRepository: RecitationRepository? = null,
) {
    val viewModel: ReaderViewModel = viewModel(
        key = contentKey?.let { "reader-$it" },
        factory = ReaderViewModel.Factory(
            readerRepository,
            vocabularyRepository,
            recitationRepository,
        ),
    )
    val content by viewModel.content.collectAsStateWithLifecycle()
    val index by viewModel.index.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val results by viewModel.results.collectAsStateWithLifecycle()
    val lookup by viewModel.lookup.collectAsStateWithLifecycle()
    val audioPath by viewModel.audioPath.collectAsStateWithLifecycle()

    when (val state = content) {
        is ReaderUiState.Loading ->
            Box(modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
        is ReaderUiState.Empty ->
            Box(modifier.fillMaxSize(), Alignment.Center) { Text("No passages available.") }
        is ReaderUiState.Content ->
            ReaderContent(
                passages = state.passages,
                index = index,
                query = query,
                results = results,
                lookup = lookup,
                audioPath = audioPath,
                onQueryChange = viewModel::onQueryChange,
                onClearSearch = viewModel::clearSearch,
                onWordTap = viewModel::onWordTap,
                onDismissLookup = viewModel::dismissLookup,
                onOpenResult = viewModel::openResult,
                onPrevious = viewModel::previous,
                onNext = viewModel::next,
                modifier = modifier,
            )
    }
}
