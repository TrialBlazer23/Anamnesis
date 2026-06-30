package com.anamnesis.feature.reader

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anamnesis.core.domain.model.Passage

/**
 * The reading surface: a single passage of polytonic Greek rendered in Gentium
 * Plus with its facing translation, plus previous/next navigation. Wiring this
 * to the content-pack database (in place of [SAMPLE_PASSAGES]) is the next step.
 */
@Composable
fun ReaderScreen(
    passages: List<Passage>,
    modifier: Modifier = Modifier,
    initialIndex: Int = 0,
) {
    if (passages.isEmpty()) {
        Text(text = "No passages to show.", modifier = modifier.padding(16.dp))
        return
    }

    var index by rememberSaveable {
        mutableIntStateOf(initialIndex.coerceIn(0, passages.lastIndex))
    }
    val passage = passages[index]

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "${passage.work} ${passage.reference}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(12.dp))

        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = passage.greek,
                fontFamily = GentiumPlus,
                fontSize = 26.sp,
                lineHeight = 40.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
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
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                onClick = { index = ReaderNavigation.previous(index) },
                enabled = ReaderNavigation.canGoPrevious(index),
            ) { Text("Previous") }

            Text(
                text = "${index + 1} / ${passages.size}",
                style = MaterialTheme.typography.labelLarge,
            )

            Button(
                onClick = { index = ReaderNavigation.next(index, passages.size) },
                enabled = ReaderNavigation.canGoNext(index, passages.size),
            ) { Text("Next") }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReaderScreenPreview() {
    MaterialTheme {
        ReaderScreen(passages = SAMPLE_PASSAGES)
    }
}
