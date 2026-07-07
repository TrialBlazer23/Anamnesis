package com.anamnesis.feature.learn.pack

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import com.anamnesis.feature.learn.model.LessonPack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Loads the bundled lessons pack from module assets, once; the parsed pack is
 * tiny (tens of KB) and shared by the Learn UI and the SRS seed source.
 */
object LessonPackLoader {
    const val ASSET_PATH = "lessons/lessons.json"

    @Volatile
    private var cached: LessonPack? = null

    /** The already-parsed pack, if any — avoids a loading flash on re-entry. */
    fun peek(): LessonPack? = cached

    fun load(context: Context): LessonPack =
        cached ?: synchronized(this) {
            cached ?: context.applicationContext.assets.open(ASSET_PATH)
                .bufferedReader()
                .use { it.readText() }
                .let(LessonPackParser::parse)
                .also { cached = it }
        }
}

/** The lessons pack, loaded off the main thread; null while loading. */
@Composable
fun rememberLessonPack(): LessonPack? {
    val context = LocalContext.current.applicationContext
    return produceState<LessonPack?>(initialValue = LessonPackLoader.peek(), context) {
        value = withContext(Dispatchers.IO) { LessonPackLoader.load(context) }
    }.value
}
