package com.anamnesis.core.data.packs

import android.content.Context
import com.anamnesis.core.domain.packs.PackCatalog
import com.anamnesis.core.domain.packs.PackKind
import com.anamnesis.core.domain.packs.recitationEntryPath
import com.anamnesis.core.domain.repository.RecitationRepository
import java.io.File
import java.util.zip.ZipFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Serves per-line recitation audio from installed audio packs. The packs are
 * stored zips (AAC is already compressed); a requested line is extracted once
 * into `cacheDir/recitations/<pack>/book_B/line_L.mp4` and played from there —
 * the cache is re-extractable, so Android may reclaim it freely.
 */
class RecitationLibrary(
    context: Context,
    private val packLibrary: PackLibrary = PackLibrary(context),
) : RecitationRepository {
    private val cacheRoot = File(context.applicationContext.cacheDir, "recitations")

    override suspend fun audioFileFor(ctsUrn: String): String? = withContext(Dispatchers.IO) {
        val entryPath = recitationEntryPath(ctsUrn) ?: return@withContext null
        for (descriptor in PackCatalog.remote) {
            if (descriptor.kind != PackKind.AUDIO) continue
            val workId = descriptor.workId ?: continue
            if (!ctsUrn.contains(workId)) continue
            val zip = packLibrary.installedFile(descriptor) ?: continue

            val extracted = File(cacheRoot, "${descriptor.id}/$entryPath")
            if (extracted.length() > 0) return@withContext extracted.absolutePath

            runCatching {
                ZipFile(zip).use { zf ->
                    val entry = zf.getEntry(entryPath) ?: return@use
                    extracted.parentFile?.mkdirs()
                    zf.getInputStream(entry).use { input ->
                        extracted.outputStream().use(input::copyTo)
                    }
                }
            }
            if (extracted.length() > 0) return@withContext extracted.absolutePath
        }
        null
    }
}
