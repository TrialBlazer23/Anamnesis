package com.anamnesis.feature.srs

import com.anamnesis.core.domain.model.Card
import com.anamnesis.core.domain.repository.SrsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReviewViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before fun setUp() = Dispatchers.setMain(dispatcher)

    @After fun tearDown() = Dispatchers.resetMain()

    private class FakeSrsRepository(initial: List<Card> = emptyList()) : SrsRepository {
        val cards = initial.associateBy { it.lemma }.toMutableMap()
        override suspend fun count() = cards.size
        override suspend fun seed(cards: List<Card>) {
            cards.forEach { seeded ->
                val existing = this.cards[seeded.lemma]
                this.cards[seeded.lemma] = existing?.copy(
                    position = seeded.position,
                    gloss = seeded.gloss,
                    partOfSpeech = seeded.partOfSpeech,
                ) ?: seeded
            }
        }
        override suspend fun dueReviewCards(todayEpochDay: Long, limit: Int) =
            cards.values.filter { !it.isNew && it.dueEpochDay <= todayEpochDay }
                .sortedWith(compareBy({ it.dueEpochDay }, { it.position }))
                .take(limit)
        override suspend fun newCards(limit: Int, decks: Set<String>?) =
            cards.values.filter { it.isNew && (decks == null || it.deck in decks) }
                .sortedWith(compareBy({ it.position }, { it.lemma }))
                .take(limit)
        override suspend fun countIntroducedOn(epochDay: Long) =
            cards.values.count { it.introducedEpochDay == epochDay }
        override suspend fun upsert(card: Card) { cards[card.lemma] = card }
    }

    private fun vmWith(
        repo: SrsRepository,
        seeds: List<Card>,
        maxNewPerDay: Int = ReviewViewModel.DEFAULT_MAX_NEW_PER_DAY,
        vocabUnlocked: () -> Boolean = { true },
    ) = ReviewViewModel(
        repo,
        { seeds },
        ReviewScheduler(),
        today = { 1000L },
        maxNewPerDay = maxNewPerDay,
        vocabUnlocked = vocabUnlocked,
    )

    @Test
    fun seedsWhenEmptyThenServesDueQueue() = runTest {
        val repo = FakeSrsRepository()
        val vm = vmWith(repo, listOf(card("α"), card("β", position = 1)))
        advanceUntilIdle()

        assertEquals(2, repo.count())
        val state = vm.state.value as ReviewUiState.Reviewing
        assertEquals(2, state.sessionTotal)
        assertEquals(2, state.newRemaining)
        assertEquals("α", state.card.lemma)
    }

    @Test
    fun gradingAdvancesUntilDoneWithSummary() = runTest {
        val vm = vmWith(FakeSrsRepository(), listOf(card("α"), card("β", position = 1)))
        advanceUntilIdle()

        vm.grade(Rating.Good)
        advanceUntilIdle()
        assertEquals(1, (vm.state.value as ReviewUiState.Reviewing).completed)

        vm.grade(Rating.Good)
        advanceUntilIdle()
        assertEquals(ReviewUiState.Done(completed = 2, again = 0, hasMoreNew = false), vm.state.value)
    }

    @Test
    fun revealShowsAnswer() = runTest {
        val vm = vmWith(FakeSrsRepository(), listOf(card("α")))
        advanceUntilIdle()
        vm.reveal()
        assertTrue((vm.state.value as ReviewUiState.Reviewing).revealed)
    }

    @Test
    fun newCardsAreCappedByTheDailyBudget() = runTest {
        val seeds = (1..15).map { card("w$it", position = it) }
        val vm = vmWith(FakeSrsRepository(), seeds, maxNewPerDay = 10)
        advanceUntilIdle()

        assertEquals(10, (vm.state.value as ReviewUiState.Reviewing).sessionTotal)
        repeat(10) {
            vm.grade(Rating.Good)
            advanceUntilIdle()
        }
        val done = vm.state.value as ReviewUiState.Done
        assertEquals(10, done.completed)
        assertTrue("unseen words remain past the budget", done.hasMoreNew)
    }

    @Test
    fun budgetCountsCardsAlreadyIntroducedToday() = runTest {
        val introducedToday = (1..4).map {
            card("done$it").copy(stability = 2.0, dueEpochDay = 1002L, introducedEpochDay = 1000L, reps = 1)
        }
        val fresh = (1..10).map { card("new$it", position = it) }
        val vm = vmWith(FakeSrsRepository(introducedToday), fresh, maxNewPerDay = 10)
        advanceUntilIdle()

        // 4 of today's 10 already used, nothing due for review -> 6 new cards.
        assertEquals(6, (vm.state.value as ReviewUiState.Reviewing).sessionTotal)
    }

    @Test
    fun dueReviewsComeBeforeNewCards() = runTest {
        val review = card("παλαιός").copy(stability = 2.0, dueEpochDay = 999L, reps = 1)
        val vm = vmWith(FakeSrsRepository(listOf(review)), listOf(card("νέος")))
        advanceUntilIdle()

        val state = vm.state.value as ReviewUiState.Reviewing
        assertEquals("παλαιός", state.card.lemma)
        assertEquals(1, state.newRemaining)
        assertEquals(1, state.reviewRemaining)
    }

    @Test
    fun againRequeuesTheCardWithinTheSession() = runTest {
        val vm = vmWith(FakeSrsRepository(), listOf(card("α"), card("β", position = 1)))
        advanceUntilIdle()

        vm.grade(Rating.Again)
        advanceUntilIdle()
        // Session continues with β; α waits later in the queue.
        assertEquals("β", (vm.state.value as ReviewUiState.Reviewing).card.lemma)

        vm.grade(Rating.Good)
        advanceUntilIdle()
        // The failed card comes back for a same-session retry.
        assertEquals("α", (vm.state.value as ReviewUiState.Reviewing).card.lemma)

        vm.grade(Rating.Good)
        advanceUntilIdle()
        assertEquals(ReviewUiState.Done(completed = 2, again = 1, hasMoreNew = false), vm.state.value)
    }

    @Test
    fun studyMoreNewPullsTheNextBatchPastTheBudget() = runTest {
        val seeds = (1..3).map { card("w$it", position = it) }
        val vm = vmWith(FakeSrsRepository(), seeds, maxNewPerDay = 1)
        advanceUntilIdle()

        vm.grade(Rating.Good)
        advanceUntilIdle()
        assertTrue((vm.state.value as ReviewUiState.Done).hasMoreNew)

        vm.studyMoreNew()
        advanceUntilIdle()
        val state = vm.state.value as ReviewUiState.Reviewing
        assertEquals(2, state.sessionTotal)
        assertEquals("w2", state.card.lemma)
    }

    @Test
    fun lockedVocabIntroducesOnlyLetterCards() = runTest {
        val seeds = listOf(
            letter("α Α", 0),
            letter("β Β", 1),
            card("λόγος", position = 1000),
        )
        val vm = vmWith(FakeSrsRepository(), seeds, vocabUnlocked = { false })
        advanceUntilIdle()

        val state = vm.state.value as ReviewUiState.Reviewing
        assertEquals(2, state.sessionTotal) // letters only, vocab held back
        assertEquals("α Α", state.card.lemma)

        vm.grade(Rating.Good)
        advanceUntilIdle()
        vm.grade(Rating.Good)
        advanceUntilIdle()
        val done = vm.state.value as ReviewUiState.Done
        assertTrue(done.vocabLocked)
        // No more letters, and locked vocab must not be offered as "more new".
        assertTrue(!done.hasMoreNew)
    }

    @Test
    fun completingTheAlphabetUnlocksVocabularyOnReload() = runTest {
        var unlocked = false
        val seeds = listOf(letter("α Α", 0), card("λόγος", position = 1000))
        val vm = vmWith(FakeSrsRepository(), seeds, vocabUnlocked = { unlocked })
        advanceUntilIdle()

        vm.grade(Rating.Good) // the only letter
        advanceUntilIdle()
        assertTrue((vm.state.value as ReviewUiState.Done).vocabLocked)

        unlocked = true
        vm.refreshIfDone()
        advanceUntilIdle()
        val state = vm.state.value as ReviewUiState.Reviewing
        assertEquals("λόγος", state.card.lemma)
    }

    @Test
    fun refreshIfDoneNeverInterruptsASessionInProgress() = runTest {
        val vm = vmWith(FakeSrsRepository(), listOf(card("α"), card("β", position = 1)))
        advanceUntilIdle()

        vm.grade(Rating.Good)
        advanceUntilIdle()
        vm.refreshIfDone()
        advanceUntilIdle()
        // Still mid-session on the same card with progress intact.
        val state = vm.state.value as ReviewUiState.Reviewing
        assertEquals(1, state.completed)
        assertEquals("β", state.card.lemma)
    }

    @Test
    fun intervalHintsCoverEveryGrade() = runTest {
        val vm = vmWith(FakeSrsRepository(), listOf(card("α")))
        advanceUntilIdle()

        val hints = (vm.state.value as ReviewUiState.Reviewing).intervalHints
        assertEquals("today", hints[Rating.Again])
        Rating.entries.forEach { rating ->
            assertTrue("hint for $rating", !hints[rating].isNullOrBlank())
        }
    }

    private fun card(lemma: String, position: Int = 0) =
        Card(lemma = lemma, gloss = "gloss-$lemma", partOfSpeech = "noun", position = position)

    private fun letter(lemma: String, position: Int) =
        Card(
            lemma = lemma,
            gloss = "gloss-$lemma",
            partOfSpeech = "letter",
            deck = Card.DECK_LETTERS,
            position = position,
        )
}
