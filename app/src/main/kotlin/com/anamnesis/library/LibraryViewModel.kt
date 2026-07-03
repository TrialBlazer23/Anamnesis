package com.anamnesis.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anamnesis.core.data.packs.PackLibrary
import com.anamnesis.core.domain.packs.PackCatalog
import com.anamnesis.core.domain.packs.PackDescriptor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** One library row: a pack and its current on-device state. */
data class PackRowState(
    val descriptor: PackDescriptor,
    val installed: Boolean,
    val active: Boolean,
    val downloadProgress: Float? = null, // 0..1 while downloading, else null
    val error: String? = null,
)

/** Drives the Library tab: download/verify, delete, and pack activation. */
class LibraryViewModel(private val library: PackLibrary) : ViewModel() {

    private val _rows = MutableStateFlow(currentRows())
    val rows: StateFlow<List<PackRowState>> = _rows.asStateFlow()

    private val _activePackId = MutableStateFlow(library.activePackId())
    val activePackId: StateFlow<String> = _activePackId.asStateFlow()

    private val downloading = mutableSetOf<String>()

    fun download(descriptor: PackDescriptor) {
        if (!downloading.add(descriptor.id)) return
        updateRow(descriptor.id) { it.copy(downloadProgress = 0f, error = null) }
        viewModelScope.launch {
            runCatching {
                library.download(descriptor) { copied, total ->
                    val progress = if (total > 0) copied.toFloat() / total else 0f
                    updateRow(descriptor.id) { it.copy(downloadProgress = progress) }
                }
            }.onFailure { failure ->
                updateRow(descriptor.id) {
                    it.copy(
                        downloadProgress = null,
                        error = failure.message ?: "Download failed",
                    )
                }
            }.onSuccess { refresh() }
            downloading.remove(descriptor.id)
        }
    }

    fun delete(descriptor: PackDescriptor) {
        library.delete(descriptor)
        refresh()
    }

    /** Make [descriptor] the pack the Read tab shows. */
    fun activate(id: String) {
        library.setActivePack(id)
        refresh()
    }

    fun refresh() {
        _activePackId.value = library.activePackId()
        _rows.value = currentRows()
    }

    private fun currentRows(): List<PackRowState> {
        val active = library.activePackId()
        return PackCatalog.remote.map { descriptor ->
            PackRowState(
                descriptor = descriptor,
                installed = library.isInstalled(descriptor),
                active = descriptor.id == active,
            )
        }
    }

    private fun updateRow(id: String, transform: (PackRowState) -> PackRowState) {
        _rows.value = _rows.value.map { if (it.descriptor.id == id) transform(it) else it }
    }

    class Factory(private val library: PackLibrary) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            LibraryViewModel(library) as T
    }
}
