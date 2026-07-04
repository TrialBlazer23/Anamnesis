package com.anamnesis.feature.learn

import com.anamnesis.feature.learn.model.LessonPack
import com.anamnesis.feature.learn.pack.LessonPackParser
import java.io.File

/**
 * The committed pack asset, parsed once for JVM tests. Gradle runs unit tests
 * with the module directory as the working dir, so the asset is reachable by
 * relative path — no Android Context needed (the parser is pure Kotlin).
 */
val TEST_PACK: LessonPack by lazy {
    LessonPackParser.parse(File("src/main/assets/lessons/lessons.json").readText())
}
