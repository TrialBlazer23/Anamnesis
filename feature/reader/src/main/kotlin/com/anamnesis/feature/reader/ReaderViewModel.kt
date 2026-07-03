package com.anamnesis.feature.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anamnesis.core.domain.model.Passage
import com.anamnesis.core.domain.model.VocabularyEntry
import com.anamnesis.core.domain.repository.ReaderRepository
import com.anamnesis.core.domain.repository.RecitationRepository
import com.anamnesis.core.domain.repository.VocabularyRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Result of tapping a word: the tapped form plus its dictionary entry (if any). */
data class WordLookup(val word: String, val entry: VocabularyEntry?)

/** Loads passages and drives navigation, word lookup, and full-text search. */
class ReaderViewModel(
    private val readerRepository: ReaderRepository,
    private val vocabularyRepository: VocabularyRepository,
    private val recitationRepository: RecitationRepository? = null,
) : ViewModel() {

    private val _content = MutableStateFlow<ReaderUiState>(ReaderUiState.Loading)
    val content: StateFlow<ReaderUiState> = _content.asStateFlow()

    private val _index = MutableStateFlow(0)
    val index: StateFlow<Int> = _index.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _results = MutableStateFlow<List<Passage>>(emptyList())
    val results: StateFlow<List<Passage>> = _results.asStateFlow()

    private val _lookup = MutableStateFlow<WordLookup?>(null)
    val lookup: StateFlow<WordLookup?> = _lookup.asStateFlow()

    /** Playable audio file for the current passage, or null (no recitation). */
    private val _audioPath = MutableStateFlow<String?>(null)
    val audioPath: StateFlow<String?> = _audioPath.asStateFlow()

    private var passages: List<Passage> = emptyList()
    private var searchJob: Job? = null
    private var audioJob: Job? = null

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _content.value = ReaderUiState.Loading
            passages = runCatching { readerRepository.loadPassages() }.getOrDefault(emptyList())
            _index.value = 0
            _content.value =
                if (passages.isEmpty()) ReaderUiState.Empty else ReaderUiState.Content(passages)
            refreshAudio()
        }
    }

    fun next() {
        if (_index.value < passages.lastIndex) {
            _index.value += 1
            refreshAudio()
        }
    }

    fun previous() {
        if (_index.value > 0) {
            _index.value -= 1
            refreshAudio()
        }
    }

    private fun refreshAudio() {
        val repository = recitationRepository ?: return
        val urn = passages.getOrNull(_index.value)?.ctsUrn ?: run {
            _audioPath.value = null
            return
        }
        audioJob?.cancel()
        audioJob = viewModelScope.launch {
            _audioPath.value = runCatching { repository.audioFileFor(urn) }.getOrNull()
        }
    }

    fun onWordTap(token: String) {
        viewModelScope.launch {
            val entry = runCatching { vocabularyRepository.lookup(token) }.getOrNull()
            _lookup.value = WordLookup(token, entry)
        }
    }

    fun dismissLookup() {
        _lookup.value = null
    }

    fun onQueryChange(query: String) {
        _query.value = query
        searchJob?.cancel()
        if (query.isBlank()) {
            _results.value = emptyList()
            return
        }
        searchJob = viewModelScope.launch {
            _results.value = runCatching { readerRepository.search(query) }.getOrDefault(emptyList())
        }
    }

    fun clearSearch() {
        searchJob?.cancel()
        _query.value = ""
        _results.value = emptyList()
    }

    fun openResult(passage: Passage) {
        val target = passages.indexOfFirst { it.ctsUrn == passage.ctsUrn }
        if (target >= 0) {
            _index.value = target
            refreshAudio()
        }
        clearSearch()
    }

    class Factory(
        private val readerRepository: ReaderRepository,
        private val vocabularyRepository: VocabularyRepository,
        private val recitationRepository: RecitationRepository? = null,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ReaderViewModel(readerRepository, vocabularyRepository, recitationRepository) as T
    }
}
