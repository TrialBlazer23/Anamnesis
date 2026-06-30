package com.anamnesis.feature.srs

import com.anamnesis.core.domain.model.Card

/** UI state for the review (training) screen. */
sealed interface ReviewUiState {
    data object Loading : ReviewUiState

    /** Nothing due right now. */
    data object Done : ReviewUiState

    data class Reviewing(
        val card: Card,
        val revealed: Boolean,
        val remaining: Int,
    ) : ReviewUiState
}
