package com.anamnesis.core.domain.packs

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RecitationPathTest {

    @Test
    fun verseUrnMapsToBookLinePath() {
        assertEquals(
            "book_1/line_611.mp4",
            recitationEntryPath("urn:cts:greekLit:tlg0012.tlg001.perseus-grc2:1.611"),
        )
        assertEquals(
            "book_24/line_1.mp4",
            recitationEntryPath("urn:cts:greekLit:tlg0012.tlg001.perseus-grc2:24.1"),
        )
    }

    @Test
    fun proseAndMalformedUrnsHaveNoAudioPath() {
        // Three-part prose ref (Meditations book.chapter.section) — no match.
        assertNull(recitationEntryPath("urn:cts:greekLit:tlg0562.tlg001.perseus-grc2:1.1.1"))
        assertNull(recitationEntryPath("urn:cts:greekLit:tlg0012.tlg001.perseus-grc2:1"))
        assertNull(recitationEntryPath("not a urn"))
    }
}
