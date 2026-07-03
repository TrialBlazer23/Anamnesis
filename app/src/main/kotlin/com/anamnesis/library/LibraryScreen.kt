package com.anamnesis.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anamnesis.core.data.packs.PackLibrary
import com.anamnesis.core.domain.packs.PackCatalog
import com.anamnesis.core.domain.packs.PackKind

/**
 * Library tab: the bundled starter pack plus downloadable packs from the
 * project's rolling release — download (SHA-256-verified), delete, and choose
 * what the Read tab shows.
 */
@Composable
fun LibraryRoute(
    library: PackLibrary,
    onReadPack: (packId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: LibraryViewModel = viewModel(factory = LibraryViewModel.Factory(library))
    val rows by viewModel.rows.collectAsStateWithLifecycle()
    val activePackId by viewModel.activePackId.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text("Library", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(4.dp))
            Text(
                "Texts and audio are downloaded once, verified, and stored on-device.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item {
            BundledPackCard(
                active = activePackId == PackCatalog.BUNDLED_ID,
                onRead = {
                    viewModel.activate(PackCatalog.BUNDLED_ID)
                    onReadPack(PackCatalog.BUNDLED_ID)
                },
            )
        }
        items(rows, key = { it.descriptor.id }) { row ->
            RemotePackCard(
                row = row,
                onDownload = { viewModel.download(row.descriptor) },
                onDelete = { viewModel.delete(row.descriptor) },
                onRead = {
                    viewModel.activate(row.descriptor.id)
                    onReadPack(row.descriptor.id)
                },
            )
        }
    }
}

@Composable
private fun BundledPackCard(active: Boolean, onRead: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Meditations", style = MaterialTheme.typography.titleMedium)
            Text(
                "Marcus Aurelius · Haines translation · bundled with the app",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                AssistChip(onClick = {}, label = { Text(if (active) "Reading" else "Installed") })
                Spacer(Modifier.weight(1f))
                if (!active) OutlinedButton(onClick = onRead) { Text("Read") }
            }
        }
    }
}

@Composable
private fun RemotePackCard(
    row: PackRowState,
    onDownload: () -> Unit,
    onDelete: () -> Unit,
    onRead: () -> Unit,
) {
    val descriptor = row.descriptor
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(descriptor.title, style = MaterialTheme.typography.titleMedium)
            Text(
                descriptor.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))

            row.downloadProgress?.let { progress ->
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Downloading… ${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                )
                return@Column
            }
            row.error?.let { error ->
                Text(
                    error,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
                Spacer(Modifier.height(4.dp))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                when {
                    !row.installed -> {
                        Text(
                            "~${descriptor.approxSizeBytes / 1_000_000} MB",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.weight(1f))
                        Button(onClick = onDownload) { Text("Download") }
                    }
                    descriptor.kind == PackKind.TEXT -> {
                        AssistChip(
                            onClick = {},
                            label = { Text(if (row.active) "Reading" else "Installed") },
                        )
                        Spacer(Modifier.weight(1f))
                        OutlinedButton(onClick = onDelete) { Text("Remove") }
                        Spacer(Modifier.padding(4.dp))
                        Button(onClick = onRead, enabled = !row.active) { Text("Read") }
                    }
                    else -> {
                        AssistChip(onClick = {}, label = { Text("Downloaded") })
                        Spacer(Modifier.weight(1f))
                        Text(
                            "Playback coming soon",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.padding(4.dp))
                        OutlinedButton(onClick = onDelete) { Text("Remove") }
                    }
                }
            }
        }
    }
}
