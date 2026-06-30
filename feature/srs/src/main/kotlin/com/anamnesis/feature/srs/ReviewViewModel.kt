package com.anamnesis.feature.srs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anamnesis.core.domain.model.Card
import com.anamnesis.core.domain.repository.SrsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Drives the review session: seeds cards on first run, serves the due queue, and
 * persists each graded card with its next FSRS schedule.
 */
class ReviewViewModel(
    private val repository: SrsRepository,
    private val seeds: suspend () -> List<Card>,
    private val scheduler: ReviewScheduler = ReviewScheduler(),
    private val today: () -> Long = { System.currentTimeMillis() / MILLIS_PER_DAY },
) : ViewModel() {

    private val _state = MutableStateFlow<ReviewUiState>(ReviewUiState.Loading)
    val state: StateFlow<ReviewUiState> = _state.asStateFlow()

    private val queue = ArrayDeque<Card>()

    init {
        viewModelScope.launch {
            if (runCatching { repository.count() }.getOrDefault(0) == 0) {
                runCatching { repository.seed(seeds()) }
            }
            loadDue()
        }
    }

    fun loadDue() {
        viewModelScope.launch {
            _state.value = ReviewUiState.Loading
            val due = runCatching { repository.dueCards(today()) }.getOrDefault(emptyList())
            queue.clear()
            queue.addAll(due)
            showNext()
        }
    }

    fun reveal() {
        (_state.value as? ReviewUiState.Reviewing)?.let { _state.value = it.copy(revealed = true) }
    }

    fun grade(rating: Rating) {
        viewModelScope.launch {
            val card = queue.removeFirstOrNull() ?: return@launch
            val updated = scheduler.schedule(card, rating, today())
            runCatching { repository.upsert(updated) }
            showNext()
        }
    }

    private fun showNext() {
        val card = queue.firstOrNull()
        _state.value = if (card == null) {
            ReviewUiState.Done
        } else {
            ReviewUiState.Reviewing(card, revealed = false, remaining = queue.size)
        }
    }

    class Factory(
        private val repository: SrsRepository,
        private val seeds: suspend () -> List<Card>,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ReviewViewModel(repository, seeds) as T
    }

    private companion object {
        const val MILLIS_PER_DAY = 86_400_000L
    }
}
