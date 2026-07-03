package com.anamnesis.core.domain.packs

/**
 * Maps a verse CTS URN to its entry path inside a recitation audio pack.
 * The pack convention is `book_B/line_L.mp4` for a URN ending `:B.L` —
 * the path IS the manifest. Prose URNs (three-part refs like `1.1.1`) and
 * non-verse refs return null.
 */
private val VERSE_URN = Regex(":(\\d+)\\.(\\d+)$")

fun recitationEntryPath(ctsUrn: String): String? {
    val match = VERSE_URN.find(ctsUrn) ?: return null
    val (book, line) = match.destructured
    return "book_$book/line_$line.mp4"
}
