package com.anamnesis.core.data.content

import android.content.Context
import java.io.File

/**
 * Makes the bundled, read-only content pack available on disk.
 *
 * SQLite cannot open a database directly from the compressed APK assets, so the
 * pack is copied into `filesDir` once and opened from there. Content packs are
 * public-domain/openly-licensed data — they are NOT placed in the encrypted
 * SQLCipher user DB.
 */
object ContentPackProvisioner {
    private const val ASSET_DIR = "content"
    private const val FILE_NAME = "meditations.db"
    private const val ASSET_PATH = "$ASSET_DIR/$FILE_NAME"

    /** True if the starter pack is bundled in the APK assets. */
    fun isBundled(context: Context): Boolean =
        runCatching { context.assets.list(ASSET_DIR)?.contains(FILE_NAME) == true }
            .getOrDefault(false)

    /** Copy the bundled pack into `filesDir` (once) and return its absolute path. */
    fun ensure(context: Context): String {
        val target = File(context.filesDir, FILE_NAME)
        if (!target.exists() || target.length() == 0L) {
            context.assets.open(ASSET_PATH).use { input ->
                target.outputStream().use(input::copyTo)
            }
        }
        return target.absolutePath
    }
}
