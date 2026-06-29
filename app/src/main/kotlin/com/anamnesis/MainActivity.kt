package com.anamnesis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    // Placeholder entry point. Navigation graph wiring the
                    // :feature:reader and :feature:srs screens lands in Phase 2.
                    Text(
                        text = "Ἀνάμνησις",
                        modifier = Modifier.padding(padding),
                        style = MaterialTheme.typography.headlineLarge,
                    )
                }
            }
        }
    }
}
