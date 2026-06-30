package com.anamnesis.feature.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anamnesis.core.domain.repository.ReaderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Loads passages from a [ReaderRepository] and exposes [ReaderUiState]. */
class ReaderViewModel(
    private val repository: ReaderRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<ReaderUiState>(ReaderUiState.Loading)
    val state: StateFlow<ReaderUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = ReaderUiState.Loading
            val passages = runCatching { repository.loadPassages() }.getOrDefault(emptyList())
            _state.value =
                if (passages.isEmpty()) ReaderUiState.Empty else ReaderUiState.Content(passages)
        }
    }

    /** Factory so the screen can build the VM with its repository (no DI framework yet). */
    class Factory(private val repository: ReaderRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ReaderViewModel(repository) as T
    }
}
