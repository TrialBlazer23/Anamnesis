package com.anamnesis.feature.reader

import com.anamnesis.core.domain.model.Passage

/** UI state for the reader screen. */
sealed interface ReaderUiState {
    data object Loading : ReaderUiState
    data object Empty : ReaderUiState
    data class Content(val passages: List<Passage>) : ReaderUiState
}
