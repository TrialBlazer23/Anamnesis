package com.anamnesis.feature.reader

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anamnesis.core.domain.model.Passage

/**
 * Reader surface: polytonic Greek with its facing translation and tap-to-lookup.
 *
 * Phase 2 wires this to the content-pack database and the Gentium Plus
 * FontFamily. For now it renders a passage passed in directly.
 */
@Composable
fun ReaderScreen(passage: Passage, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(text = passage.reference)
        Text(text = passage.greek)
        passage.translation?.let { Text(text = it) }
    }
}
