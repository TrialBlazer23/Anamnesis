package com.anamnesis.feature.reader

/**
 * Pure navigation logic for stepping through a list of passages. Kept free of
 * Compose/Android so it is unit-testable on the JVM.
 */
object ReaderNavigation {
    fun next(index: Int, size: Int): Int = (index + 1).coerceIn(0, (size - 1).coerceAtLeast(0))

    fun previous(index: Int): Int = (index - 1).coerceAtLeast(0)

    fun canGoNext(index: Int, size: Int): Boolean = index < size - 1

    fun canGoPrevious(index: Int): Boolean = index > 0
}
