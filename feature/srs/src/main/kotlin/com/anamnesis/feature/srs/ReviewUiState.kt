package com.anamnesis.feature.srs

import com.anamnesis.core.domain.model.Card

/** UI state for the review (training) screen. */
sealed interface ReviewUiState {
    data object Loading : ReviewUiState

    /** Session finished (or nothing was due to begin with). */
    data class Done(
        /** Cards graded Hard/Good/Easy this session. */
        val completed: Int = 0,
        /** Grades of Again this session (retries within the session). */
        val again: Int = 0,
        /** Whether unseen words remain beyond today's new-card budget. */
        val hasMoreNew: Boolean = false,
    ) : ReviewUiState

    data class Reviewing(
        val card: Card,
        val revealed: Boolean,
        /** Cards finished so far this session (excludes pending retries). */
        val completed: Int,
        val sessionTotal: Int,
        /** Unseen cards still waiting in this session's queue. */
        val newRemaining: Int,
        /** Review (and retry) cards still waiting in this session's queue. */
        val reviewRemaining: Int,
        /** Human-readable next interval per grade, e.g. "today", "3d", "2mo". */
        val intervalHints: Map<Rating, String>,
    ) : ReviewUiState
}
