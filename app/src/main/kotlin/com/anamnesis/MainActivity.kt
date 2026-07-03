package com.anamnesis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.anamnesis.feature.learn.LearnRoute
import com.anamnesis.feature.reader.ReaderRoute
import com.anamnesis.feature.srs.ReviewRoute
import com.anamnesis.library.LibraryRoute

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val container = AppContainer(this)

        setContent {
            MaterialTheme {
                var tab by rememberSaveable { mutableIntStateOf(0) }
                // Recreates the reader when the Library switches the active
                // pack, so the new text is loaded.
                var activePackId by rememberSaveable {
                    mutableStateOf(container.packLibrary.activePackId())
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = tab == 0,
                                onClick = { tab = 0 },
                                icon = { Text("📖") },
                                label = { Text("Read") },
                            )
                            NavigationBarItem(
                                selected = tab == 1,
                                onClick = { tab = 1 },
                                icon = { Text("🧠") },
                                label = { Text("Train") },
                            )
                            NavigationBarItem(
                                selected = tab == 2,
                                onClick = { tab = 2 },
                                icon = { Text("🎓") },
                                label = { Text("Learn") },
                            )
                            NavigationBarItem(
                                selected = tab == 3,
                                onClick = { tab = 3 },
                                icon = { Text("📚") },
                                label = { Text("Library") },
                            )
                        }
                    },
                ) { padding ->
                    when (tab) {
                        0 -> ReaderRoute(
                            readerRepository = container.readerRepository,
                            vocabularyRepository = container.vocabularyRepository,
                            modifier = Modifier.padding(padding),
                            contentKey = activePackId,
                        )
                        1 -> ReviewRoute(
                            repository = container.srsRepository,
                            seeds = container.srsSeeds,
                            modifier = Modifier.padding(padding),
                            vocabUnlocked = container.vocabUnlocked,
                            onOpenLearn = { tab = 2 },
                        )
                        2 -> LearnRoute(
                            modifier = Modifier.padding(padding),
                        )
                        else -> LibraryRoute(
                            library = container.packLibrary,
                            onReadPack = { packId ->
                                activePackId = packId
                                tab = 0
                            },
                            modifier = Modifier.padding(padding),
                        )
                    }
                }
            }
        }
    }
}
