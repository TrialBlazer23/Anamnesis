package com.anamnesis.core.domain.packs

/** What a pack contains — a readable text DB or a recitation audio zip. */
enum class PackKind { TEXT, AUDIO }

/**
 * A downloadable content pack as listed in the library. The authoritative
 * size + SHA-256 live in the pack's `.manifest.json` next to the file on the
 * release; [approxSizeBytes] is only for display before download.
 */
data class PackDescriptor(
    val id: String,
    val title: String,
    val subtitle: String,
    val kind: PackKind,
    val fileName: String,
    val manifestFileName: String,
    val approxSizeBytes: Long,
    /** CTS work id (e.g. "tlg0012.tlg001") an audio pack recites, else null. */
    val workId: String? = null,
)

/**
 * The known packs. Content packs are plain read-only SQLite / zip published to
 * the rolling `latest` GitHub release by CI (hybrid delivery: Meditations is
 * bundled in the APK; everything else is downloaded on demand and verified
 * against its manifest's SHA-256).
 */
object PackCatalog {
    /** The starter pack shipped inside the APK — always available. */
    const val BUNDLED_ID = "meditations"

    const val BASE_URL =
        "https://github.com/TrialBlazer23/Anamnesis/releases/download/latest/"

    val remote: List<PackDescriptor> = listOf(
        PackDescriptor(
            id = "iliad",
            title = "Iliad",
            subtitle = "Homer · 15,687 verse lines · dictionary + tap-to-parse",
            kind = PackKind.TEXT,
            fileName = "iliad.db",
            manifestFileName = "iliad.manifest.json",
            approxSizeBytes = 30_000_000,
        ),
        PackDescriptor(
            id = "iliad-book1-audio",
            title = "Iliad Book 1 — recitation",
            subtitle = "611 lines read by David Chamberlain (CC BY, hypotactic.com)",
            kind = PackKind.AUDIO,
            fileName = "iliad_book1_audio.zip",
            manifestFileName = "iliad_book1_audio.manifest.json",
            approxSizeBytes = 45_000_000,
            workId = "tlg0012.tlg001",
        ),
    )

    fun byId(id: String): PackDescriptor? = remote.firstOrNull { it.id == id }
}
