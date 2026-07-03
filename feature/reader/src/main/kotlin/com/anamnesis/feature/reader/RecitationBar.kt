package com.anamnesis.feature.reader

import android.media.MediaPlayer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Per-line recitation controls. "Play" recites the current line once;
 * "Recite" keeps going — when a line's audio finishes it advances the reader
 * (via [onAdvance]) and plays the next line, until audio runs out or the user
 * stops. Uses the platform [MediaPlayer]: the pack ships small per-line
 * MP4/AAC files, which need none of Media3's streaming machinery.
 */
@Composable
fun RecitationBar(
    audioPath: String,
    hasNext: Boolean,
    onAdvance: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val player = remember { MediaPlayer() }
    var isPlaying by remember { mutableStateOf(false) }
    var reciting by remember { mutableStateOf(false) }
    val currentHasNext by rememberUpdatedState(hasNext)
    val currentOnAdvance by rememberUpdatedState(onAdvance)

    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    fun stop() {
        runCatching { if (player.isPlaying) player.stop() }
        isPlaying = false
    }

    fun play(path: String) {
        runCatching {
            player.reset()
            player.setDataSource(path)
            player.setOnCompletionListener {
                isPlaying = false
                if (reciting) {
                    if (currentHasNext) currentOnAdvance() else reciting = false
                }
            }
            player.prepare()
            player.start()
            isPlaying = true
        }.onFailure {
            isPlaying = false
            reciting = false
        }
    }

    // Continuous mode: (re)start whenever the mode turns on or the reader
    // advances to a new line's audio. Otherwise a line change stops playback.
    LaunchedEffect(audioPath, reciting) {
        if (reciting) play(audioPath) else stop()
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isPlaying) {
            TextButton(onClick = { reciting = false; stop() }) { Text("⏹ Stop") }
            if (reciting) {
                Text("Reciting…", style = MaterialTheme.typography.labelMedium)
            }
        } else {
            TextButton(onClick = { reciting = false; play(audioPath) }) { Text("▶ Play line") }
            TextButton(onClick = { reciting = true }) { Text("▶▶ Recite") }
        }
        Spacer(Modifier.width(8.dp).weight(1f))
        Text(
            "♪ D. Chamberlain · CC BY",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
