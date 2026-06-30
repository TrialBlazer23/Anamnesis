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
import com.anamnesis.core.data.content.ContentPackProvisioner
import com.anamnesis.core.data.content.ContentPackReaderRepository
import com.anamnesis.core.domain.repository.ReaderRepository
import com.anamnesis.feature.reader.ReaderRoute
import com.anamnesis.feature.reader.SampleReaderRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Read from the bundled content pack; fall back to sample data if absent.
        val repository: ReaderRepository =
            if (ContentPackProvisioner.isBundled(applicationContext)) {
                ContentPackReaderRepository(applicationContext)
            } else {
                SampleReaderRepository()
            }

        setContent {
            MaterialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    ReaderRoute(
                        repository = repository,
                        modifier = Modifier.padding(padding),
                    )
                }
            }
        }
    }
}
