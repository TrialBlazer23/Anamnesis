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
 * Drives the review session: seeds cards on first run, composes the day's queue
 * (due reviews first, then a budgeted batch of new words in introduction order),
 * and persists each graded card with its next FSRS schedule. Cards graded
 * *Again* re-enter the session a few cards later, so a word is relearned before
 * the session lets go of it.
 */
class ReviewViewModel(
    private val repository: SrsRepository,
    private val seeds: suspend () -> List<Card>,
    private val scheduler: ReviewScheduler = ReviewScheduler(),
    private val today: () -> Long = { System.currentTimeMillis() / MILLIS_PER_DAY },
    private val maxNewPerDay: Int = DEFAULT_MAX_NEW_PER_DAY,
    private val vocabUnlocked: () -> Boolean = { true },
) : ViewModel() {

    private val _state = MutableStateFlow<ReviewUiState>(ReviewUiState.Loading)
    val state: StateFlow<ReviewUiState> = _state.asStateFlow()

    private val queue = ArrayDeque<Card>()
    private var sessionTotal = 0
    private var completed = 0
    private var againCount = 0
    private var vocabIsUnlocked = true

    init {
        viewModelScope.launch {
            // Seed every launch: the DAO inserts with IGNORE, so existing review
            // progress is untouched while newly added decks/cards appear.
            runCatching { repository.seed(seeds()) }
            startSession()
        }
    }

    fun loadDue() {
        viewModelScope.launch { startSession() }
    }

    /**
     * Re-check the queue when the tab is revisited between sessions — e.g. the
     * user just finished the alphabet in Learn and vocabulary unlocked. Never
     * interrupts a session in progress.
     */
    fun refreshIfDone() {
        if (_state.value is ReviewUiState.Done) loadDue()
    }

    /** Pull the next batch of unseen words even though today's budget is spent. */
    fun studyMoreNew() {
        viewModelScope.launch {
            _state.value = ReviewUiState.Loading
            vocabIsUnlocked = runCatching { vocabUnlocked() }.getOrDefault(true)
            val extra = runCatching { repository.newCards(EXTRA_NEW_BATCH, allowedDecks()) }
                .getOrDefault(emptyList())
            queue.clear()
            queue.addAll(extra)
            sessionTotal = extra.size
            completed = 0
            againCount = 0
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
            if (rating == Rating.Again) {
                againCount += 1
                // Relearning step: show the failed card again a few cards later.
                queue.add(minOf(RELEARN_STEP, queue.size), updated)
            } else {
                completed += 1
            }
            showNext()
        }
    }

    private suspend fun startSession() {
        _state.value = ReviewUiState.Loading
        val now = today()
        vocabIsUnlocked = runCatching { vocabUnlocked() }.getOrDefault(true)
        val reviews = runCatching { repository.dueReviewCards(now) }.getOrDefault(emptyList())
        val introducedToday = runCatching { repository.countIntroducedOn(now) }.getOrDefault(0)
        val newBudget = (maxNewPerDay - introducedToday).coerceAtLeast(0)
        val fresh = if (newBudget > 0) {
            runCatching { repository.newCards(newBudget, allowedDecks()) }.getOrDefault(emptyList())
        } else {
            emptyList()
        }
        queue.clear()
        queue.addAll(reviews)
        queue.addAll(fresh)
        sessionTotal = queue.size
        completed = 0
        againCount = 0
        showNext()
    }

    /** null = every deck may introduce new cards; letters-only while vocab is locked. */
    private fun allowedDecks(): Set<String>? =
        if (vocabIsUnlocked) null else setOf(Card.DECK_LETTERS)

    private suspend fun showNext() {
        val card = queue.firstOrNull()
        if (card == null) {
            val hasMoreNew = runCatching { repository.newCards(1, allowedDecks()) }
                .getOrDefault(emptyList())
                .isNotEmpty()
            _state.value = ReviewUiState.Done(
                completed = completed,
                again = againCount,
                hasMoreNew = hasMoreNew,
                vocabLocked = !vocabIsUnlocked,
            )
            return
        }
        val now = today()
        _state.value = ReviewUiState.Reviewing(
            card = card,
            revealed = false,
            completed = completed,
            sessionTotal = sessionTotal,
            newRemaining = queue.count { it.isNew },
            reviewRemaining = queue.count { !it.isNew },
            intervalHints = Rating.entries.associateWith { rating ->
                formatInterval(scheduler.previewIntervalDays(card, rating, now))
            },
        )
    }

    class Factory(
        private val repository: SrsRepository,
        private val seeds: suspend () -> List<Card>,
        private val vocabUnlocked: () -> Boolean = { true },
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ReviewViewModel(repository, seeds, vocabUnlocked = vocabUnlocked) as T
    }

    companion object {
        const val DEFAULT_MAX_NEW_PER_DAY = 10
        const val EXTRA_NEW_BATCH = 10

        /** How many cards later an Again-graded card is shown again. */
        const val RELEARN_STEP = 3

        private const val MILLIS_PER_DAY = 86_400_000L

        internal fun formatInterval(days: Long): String = when {
            days <= 0L -> "today"
            days < 30L -> "${days}d"
            days < 365L -> "${days / 30}mo"
            else -> "${days / 365}y"
        }
    }
}
