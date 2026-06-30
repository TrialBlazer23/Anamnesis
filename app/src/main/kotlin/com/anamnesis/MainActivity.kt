package com.anamnesis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.anamnesis.feature.reader.ReaderScreen
import com.anamnesis.feature.reader.SAMPLE_PASSAGES

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    // Reads from SAMPLE_PASSAGES until the content-pack DB is wired.
                    ReaderScreen(
                        passages = SAMPLE_PASSAGES,
                        modifier = Modifier.padding(padding),
                    )
                }
            }
        }
    }
}
