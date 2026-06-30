package com.anamnesis.feature.reader

import com.anamnesis.core.domain.model.Passage
import com.anamnesis.core.domain.model.VocabularyEntry
import com.anamnesis.core.domain.repository.ReaderRepository
import com.anamnesis.core.domain.repository.VocabularyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReaderViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before fun setUp() = Dispatchers.setMain(dispatcher)

    @After fun tearDown() = Dispatchers.resetMain()

    private fun passage(ref: String, greek: String = "greek") =
        Passage("urn:$ref", "Meditations", ref, greek, greek, null)

    private val noVocab = object : VocabularyRepository {
        override suspend fun lookup(token: String): VocabularyEntry? = null
    }

    private fun vm(
        passages: List<Passage>,
        vocab: VocabularyRepository = noVocab,
        searchResult: List<Passage> = emptyList(),
    ) = ReaderViewModel(
        object : ReaderRepository {
            override suspend fun loadPassages() = passages
            override suspend fun search(query: String) = searchResult
        },
        vocab,
    )

    @Test
    fun loadsContentAndStartsAtFirstPassage() = runTest {
        val reader = vm(listOf(passage("1.1"), passage("1.2")))
        advanceUntilIdle()
        val state = reader.content.value
        assertTrue(state is ReaderUiState.Content)
        assertEquals(2, (state as ReaderUiState.Content).passages.size)
        assertEquals(0, reader.index.value)
    }

    @Test
    fun navigationStaysWithinBounds() = runTest {
        val reader = vm(listOf(passage("1.1"), passage("1.2")))
        advanceUntilIdle()
        reader.previous()
        assertEquals(0, reader.index.value) // clamped
        reader.next()
        assertEquals(1, reader.index.value)
        reader.next()
        assertEquals(1, reader.index.value) // clamped at last
    }

    @Test
    fun emptyRepositoryYieldsEmptyState() = runTest {
        val reader = vm(emptyList())
        advanceUntilIdle()
        assertEquals(ReaderUiState.Empty, reader.content.value)
    }

    @Test
    fun tappingWordResolvesLookup() = runTest {
        val vocab = object : VocabularyRepository {
            override suspend fun lookup(token: String) =
                VocabularyEntry("λόγος", "noun", "word", null, 1)
        }
        val reader = vm(listOf(passage("1.1")), vocab = vocab)
        advanceUntilIdle()
        reader.onWordTap("λόγος,")
        advanceUntilIdle()
        assertEquals("λόγος,", reader.lookup.value?.word)
        assertEquals("word", reader.lookup.value?.entry?.gloss)
        reader.dismissLookup()
        assertNull(reader.lookup.value)
    }

    @Test
    fun searchPopulatesAndClears() = runTest {
        val hit = passage("7.59")
        val reader = vm(listOf(passage("1.1"), hit), searchResult = listOf(hit))
        advanceUntilIdle()
        reader.onQueryChange("good")
        advanceUntilIdle()
        assertEquals(listOf(hit), reader.results.value)
        reader.clearSearch()
        assertEquals("", reader.query.value)
        assertTrue(reader.results.value.isEmpty())
    }

    @Test
    fun openResultNavigatesAndClearsSearch() = runTest {
        val target = passage("7.59")
        val reader = vm(listOf(passage("1.1"), target), searchResult = listOf(target))
        advanceUntilIdle()
        reader.onQueryChange("x")
        advanceUntilIdle()
        reader.openResult(target)
        assertEquals(1, reader.index.value)
        assertEquals("", reader.query.value)
    }
}
