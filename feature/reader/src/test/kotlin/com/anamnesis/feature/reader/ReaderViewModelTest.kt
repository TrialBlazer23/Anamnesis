package com.anamnesis.feature.reader

import com.anamnesis.core.domain.model.Passage
import com.anamnesis.core.domain.repository.ReaderRepository
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
class ReaderViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before fun setUp() = Dispatchers.setMain(dispatcher)

    @After fun tearDown() = Dispatchers.resetMain()

    private fun passage(ref: String) =
        Passage("urn:$ref", "Meditations", ref, "greek", "greek", null)

    @Test
    fun emitsContentWhenRepositoryHasPassages() = runTest {
        val vm = ReaderViewModel(repository { listOf(passage("1.1"), passage("1.2")) })
        advanceUntilIdle()
        val state = vm.state.value
        assertTrue(state is ReaderUiState.Content)
        assertEquals(2, (state as ReaderUiState.Content).passages.size)
    }

    @Test
    fun emitsEmptyWhenRepositoryReturnsNothing() = runTest {
        val vm = ReaderViewModel(repository { emptyList() })
        advanceUntilIdle()
        assertEquals(ReaderUiState.Empty, vm.state.value)
    }

    @Test
    fun emitsEmptyWhenRepositoryThrows() = runTest {
        val vm = ReaderViewModel(repository { error("boom") })
        advanceUntilIdle()
        assertEquals(ReaderUiState.Empty, vm.state.value)
    }

    private fun repository(block: suspend () -> List<Passage>): ReaderRepository =
        object : ReaderRepository {
            override suspend fun loadPassages(): List<Passage> = block()
        }
}
