package com.anamnesis.core.data.content

import android.content.Context
import java.io.File
import java.security.MessageDigest

/**
 * Makes the bundled, read-only content pack available on disk.
 *
 * SQLite cannot open a database directly from the compressed APK assets, so the
 * pack is copied into `filesDir` and opened from there. The copy is refreshed
 * whenever the bundled asset changes (tracked by a SHA-256 fingerprint in
 * SharedPreferences) — otherwise an app update shipping a newer pack would
 * keep serving the stale copy forever. Content packs are public-domain/openly-
 * licensed data — they are NOT placed in the encrypted SQLCipher user DB.
 */
object ContentPackProvisioner {
    private const val ASSET_DIR = "content"
    private const val FILE_NAME = "meditations.db"
    private const val ASSET_PATH = "$ASSET_DIR/$FILE_NAME"
    private const val PREFS = "content_pack_provisioner"
    private const val KEY_FINGERPRINT = "bundled_asset_sha256"

    /** Computed once per process; the asset cannot change while we run. */
    @Volatile private var assetFingerprint: String? = null

    /** True if the starter pack is bundled in the APK assets. */
    fun isBundled(context: Context): Boolean =
        runCatching { context.assets.list(ASSET_DIR)?.contains(FILE_NAME) == true }
            .getOrDefault(false)

    /**
     * Copy the bundled pack into `filesDir` (when missing or outdated) and
     * return its absolute path.
     */
    @Synchronized
    fun ensure(context: Context): String {
        val target = File(context.filesDir, FILE_NAME)
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val fingerprint = assetFingerprint
            ?: fingerprintAsset(context).also { assetFingerprint = it }

        val stale = prefs.getString(KEY_FINGERPRINT, null) != fingerprint
        if (!target.exists() || target.length() == 0L || stale) {
            context.assets.open(ASSET_PATH).use { input ->
                target.outputStream().use(input::copyTo)
            }
            prefs.edit().putString(KEY_FINGERPRINT, fingerprint).apply()
        }
        return target.absolutePath
    }

    private fun fingerprintAsset(context: Context): String {
        val digest = MessageDigest.getInstance("SHA-256")
        context.assets.open(ASSET_PATH).use { input ->
            val buffer = ByteArray(64 * 1024)
            while (true) {
                val read = input.read(buffer)
                if (read < 0) break
                digest.update(buffer, 0, read)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }
}
