package com.anamnesis.core.data.packs

import android.content.Context
import com.anamnesis.core.data.content.ContentPackProvisioner
import com.anamnesis.core.domain.packs.PackCatalog
import com.anamnesis.core.domain.packs.PackDescriptor
import com.anamnesis.core.domain.packs.PackInstaller
import com.anamnesis.core.domain.packs.PackKind
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Manages downloaded content packs on device: download-with-verification into
 * `filesDir/packs/`, deletion, and which text pack the reader is showing.
 * Packs are openly-licensed public data — plain files, never in the encrypted
 * user DB.
 */
class PackLibrary(context: Context) {
    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences("packs", Context.MODE_PRIVATE)
    private val packsDir = File(appContext.filesDir, "packs")

    fun activePackId(): String =
        prefs.getString(KEY_ACTIVE, PackCatalog.BUNDLED_ID) ?: PackCatalog.BUNDLED_ID

    fun setActivePack(id: String) {
        prefs.edit().putString(KEY_ACTIVE, id).apply()
    }

    fun isInstalled(descriptor: PackDescriptor): Boolean = installedFile(descriptor) != null

    fun installedFile(descriptor: PackDescriptor): File? =
        File(packsDir, descriptor.fileName).takeIf { it.exists() && it.length() > 0 }

    /**
     * The SQLite path the reader/dictionary should open right now: the active
     * downloaded text pack if present, else the bundled starter pack.
     */
    fun activeTextDbPath(): String {
        val id = activePackId()
        if (id != PackCatalog.BUNDLED_ID) {
            val descriptor = PackCatalog.byId(id)
            if (descriptor?.kind == PackKind.TEXT) {
                installedFile(descriptor)?.let { return it.absolutePath }
            }
        }
        return ContentPackProvisioner.ensure(appContext)
    }

    /** Download + SHA-256-verify a pack. Progress is (bytesCopied, totalBytes). */
    suspend fun download(
        descriptor: PackDescriptor,
        onProgress: (Long, Long) -> Unit = { _, _ -> },
    ): File = withContext(Dispatchers.IO) {
        PackInstaller(::openStream)
            .install(PackCatalog.BASE_URL, descriptor.manifestFileName, packsDir, onProgress)
    }

    /** Remove a downloaded pack; falls back to the bundled pack if it was active. */
    fun delete(descriptor: PackDescriptor) {
        File(packsDir, descriptor.fileName).delete()
        File(packsDir, descriptor.fileName + ".part").delete()
        if (activePackId() == descriptor.id) setActivePack(PackCatalog.BUNDLED_ID)
    }

    private fun openStream(url: String): InputStream {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.connectTimeout = 30_000
        connection.readTimeout = 60_000
        connection.instanceFollowRedirects = true
        val code = connection.responseCode
        if (code != HttpURLConnection.HTTP_OK) {
            connection.disconnect()
            throw IOException("HTTP $code fetching $url")
        }
        return connection.inputStream
    }

    private companion object {
        const val KEY_ACTIVE = "active_pack_id"
    }
}
