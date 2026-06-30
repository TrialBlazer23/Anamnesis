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
            cards.forEach { this.cards.putIfAbsent(it.lemma, it) }
        }
        override suspend fun dueCards(todayEpochDay: Long, limit: Int) =
            cards.values.filter { it.dueEpochDay <= todayEpochDay }
                .sortedBy { it.dueEpochDay }.take(limit)
        override suspend fun upsert(card: Card) { cards[card.lemma] = card }
    }

    private fun vmWith(repo: SrsRepository, seeds: List<Card>) =
        ReviewViewModel(repo, { seeds }, ReviewScheduler(), today = { 1000L })

    @Test
    fun seedsWhenEmptyThenServesDueQueue() = runTest {
        val repo = FakeSrsRepository()
        val vm = vmWith(repo, listOf(card("α"), card("β")))
        advanceUntilIdle()

        assertEquals(2, repo.count())
        val state = vm.state.value
        assertTrue(state is ReviewUiState.Reviewing)
        assertEquals(2, (state as ReviewUiState.Reviewing).remaining)
    }

    @Test
    fun gradingAdvancesUntilDone() = runTest {
        val vm = vmWith(FakeSrsRepository(), listOf(card("α"), card("β")))
        advanceUntilIdle()

        vm.grade(Rating.Good)
        advanceUntilIdle()
        assertEquals(1, (vm.state.value as ReviewUiState.Reviewing).remaining)

        vm.grade(Rating.Good)
        advanceUntilIdle()
        assertEquals(ReviewUiState.Done, vm.state.value)
    }

    @Test
    fun revealShowsAnswer() = runTest {
        val vm = vmWith(FakeSrsRepository(), listOf(card("α")))
        advanceUntilIdle()
        vm.reveal()
        assertTrue((vm.state.value as ReviewUiState.Reviewing).revealed)
    }

    private fun card(lemma: String) = Card(lemma = lemma, gloss = "gloss-$lemma", partOfSpeech = "noun")
}
