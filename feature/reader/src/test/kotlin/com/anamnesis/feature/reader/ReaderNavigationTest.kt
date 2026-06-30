package com.anamnesis.feature.reader

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReaderNavigationTest {

    @Test
    fun nextAndPreviousClampToBounds() {
        assertEquals(1, ReaderNavigation.next(0, size = 3))
        assertEquals(2, ReaderNavigation.next(2, size = 3)) // clamped at last
        assertEquals(0, ReaderNavigation.previous(0))        // clamped at first
        assertEquals(1, ReaderNavigation.previous(2))
    }

    @Test
    fun edgeFlagsReflectPosition() {
        assertFalse(ReaderNavigation.canGoPrevious(0))
        assertTrue(ReaderNavigation.canGoPrevious(1))
        assertTrue(ReaderNavigation.canGoNext(0, size = 3))
        assertFalse(ReaderNavigation.canGoNext(2, size = 3))
    }

    @Test
    fun handlesSingletonAndEmptyLists() {
        assertFalse(ReaderNavigation.canGoNext(0, size = 1))
        assertFalse(ReaderNavigation.canGoPrevious(0))
        assertEquals(0, ReaderNavigation.next(0, size = 1))
        assertEquals(0, ReaderNavigation.next(0, size = 0)) // no crash on empty
    }

    @Test
    fun sampleDataIsPresentAndNfc() {
        assertEquals(3, SAMPLE_PASSAGES.size)
        // Greek present, translation pending (null) for now.
        assertTrue(SAMPLE_PASSAGES.all { it.greek.isNotBlank() })
        assertTrue(SAMPLE_PASSAGES.all { it.translation == null })
    }
}
