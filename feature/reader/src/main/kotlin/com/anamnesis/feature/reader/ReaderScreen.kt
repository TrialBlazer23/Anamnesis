package com.anamnesis.feature.reader

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anamnesis.core.domain.model.Passage
import com.anamnesis.core.ui.GentiumPlus

@Composable
fun ReaderContent(
    passages: List<Passage>,
    index: Int,
    query: String,
    results: List<Passage>,
    lookup: WordLookup?,
    audioPath: String?,
    onQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onWordTap: (String) -> Unit,
    onDismissLookup: () -> Unit,
    onOpenResult: (Passage) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            singleLine = true,
            placeholder = { Text("Search Greek or English") },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    TextButton(onClick = onClearSearch) { Text("✕") }
                }
            },
        )

        if (query.isNotBlank()) {
            SearchResults(results, onOpenResult, Modifier.weight(1f))
        } else if (passages.isNotEmpty()) {
            PassageView(
                passage = passages[index.coerceIn(0, passages.lastIndex)],
                position = index.coerceIn(0, passages.lastIndex) + 1,
                total = passages.size,
                audioPath = audioPath,
                onWordTap = onWordTap,
                onPrevious = onPrevious,
                onNext = onNext,
                modifier = Modifier.weight(1f),
            )
        }
    }

    if (lookup != null) {
        LookupSheet(lookup, onDismissLookup)
    }
}

@Composable
private fun PassageView(
    passage: Passage,
    position: Int,
    total: Int,
    audioPath: String?,
    onWordTap: (String) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "${passage.work} ${passage.reference}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        if (audioPath != null) {
            RecitationBar(
                audioPath = audioPath,
                hasNext = position < total,
                onAdvance = onNext,
            )
        }
        Spacer(Modifier.height(12.dp))

        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            TappableGreek(text = passage.greek, onWordTap = onWordTap)
            Spacer(Modifier.height(20.dp))
            HorizontalDivider()
            Spacer(Modifier.height(20.dp))
            Text(
                text = passage.translation ?: "Facing translation coming soon.",
                fontFamily = GentiumPlus,
                fontStyle = if (passage.translation == null) FontStyle.Italic else FontStyle.Normal,
                fontSize = 18.sp,
                lineHeight = 28.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(onClick = onPrevious, enabled = position > 1) { Text("Previous") }
            Text("$position / $total", style = MaterialTheme.typography.labelLarge)
            Button(onClick = onNext, enabled = position < total) { Text("Next") }
        }
    }
}

/** Greek text where tapping a word reports it via [onWordTap]. */
@Composable
private fun TappableGreek(text: String, onWordTap: (String) -> Unit) {
    var layout by remember(text) { mutableStateOf<TextLayoutResult?>(null) }
    Text(
        text = text,
        fontFamily = GentiumPlus,
        fontSize = 26.sp,
        lineHeight = 40.sp,
        color = MaterialTheme.colorScheme.onSurface,
        onTextLayout = { layout = it },
        modifier = Modifier.pointerInput(text) {
            detectTapGestures { position ->
                val result = layout ?: return@detectTapGestures
                val word = wordAt(text, result.getOffsetForPosition(position))
                if (word.isNotBlank()) onWordTap(word)
            }
        },
    )
}

@Composable
private fun SearchResults(
    results: List<Passage>,
    onOpenResult: (Passage) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (results.isEmpty()) {
        Text(
            text = "No matches.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier.padding(top = 16.dp),
        )
        return
    }
    LazyColumn(modifier = modifier) {
        items(results, key = { it.ctsUrn }) { passage ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenResult(passage) }
                    .padding(vertical = 10.dp),
            ) {
                Text(
                    text = "${passage.work} ${passage.reference}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = passage.greek,
                    fontFamily = GentiumPlus,
                    fontSize = 18.sp,
                    maxLines = 2,
                )
                HorizontalDivider(modifier = Modifier.padding(top = 10.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LookupSheet(lookup: WordLookup, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, bottom = 32.dp)) {
            Text(lookup.word, fontFamily = GentiumPlus, fontSize = 28.sp)
            Spacer(Modifier.height(8.dp))
            val entry = lookup.entry
            if (entry == null) {
                Text(
                    "No dictionary entry found for this form.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Text(
                    text = entry.lemma,
                    fontFamily = GentiumPlus,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary,
                )
                if (entry.partOfSpeech.isNotBlank()) {
                    Text(entry.partOfSpeech, style = MaterialTheme.typography.labelMedium)
                }
                Spacer(Modifier.height(6.dp))
                Text(entry.gloss, fontFamily = GentiumPlus, fontSize = 16.sp, lineHeight = 24.sp)
            }
        }
    }
}

/** Expand around [offset] to the surrounding run of letters. */
private fun wordAt(text: String, offset: Int): String {
    if (text.isEmpty()) return ""
    val pivot = offset.coerceIn(0, text.length - 1)
    if (!text[pivot].isLetter()) return ""
    var start = pivot
    while (start > 0 && text[start - 1].isLetter()) start--
    var end = pivot
    while (end < text.length && text[end].isLetter()) end++
    return text.substring(start, end)
}
