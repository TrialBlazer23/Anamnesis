package com.anamnesis.core.domain.packs

import java.io.File
import java.io.IOException
import java.io.InputStream
import java.security.MessageDigest

/**
 * Downloads a pack with verify-then-install semantics:
 * 1. fetch `<manifestFileName>` and read the expected file name, size, SHA-256;
 * 2. stream the pack to `<file>.part`, hashing as it goes;
 * 3. on checksum match, atomically move `.part` into place — a torn download
 *    or checksum mismatch never leaves a corrupt pack where readers look.
 *
 * Transport is injected ([fetch] maps a URL to an [InputStream]) so the class
 * is pure JVM and unit-testable; Android supplies an HttpURLConnection fetcher.
 */
class PackInstaller(private val fetch: (url: String) -> InputStream) {

    /** Returns the installed file. Throws [IOException] on any failure. */
    fun install(
        baseUrl: String,
        manifestFileName: String,
        targetDir: File,
        onProgress: (bytesCopied: Long, totalBytes: Long) -> Unit = { _, _ -> },
    ): File {
        val manifestJson = fetch(baseUrl + manifestFileName).use {
            it.readBytes().decodeToString()
        }
        val manifest = try {
            PackManifest.parse(manifestJson)
        } catch (e: IllegalArgumentException) {
            throw IOException("Bad pack manifest $manifestFileName: ${e.message}", e)
        }

        if (!targetDir.isDirectory && !targetDir.mkdirs()) {
            throw IOException("Cannot create pack directory $targetDir")
        }
        val target = File(targetDir, manifest.fileName)
        val part = File(targetDir, manifest.fileName + ".part")
        val digest = MessageDigest.getInstance("SHA-256")
        try {
            var copied = 0L
            fetch(baseUrl + manifest.fileName).use { input ->
                part.outputStream().use { output ->
                    val buffer = ByteArray(64 * 1024)
                    while (true) {
                        val read = input.read(buffer)
                        if (read < 0) break
                        output.write(buffer, 0, read)
                        digest.update(buffer, 0, read)
                        copied += read
                        onProgress(copied, manifest.sizeBytes)
                    }
                }
            }
            val actual = digest.digest().joinToString("") { "%02x".format(it) }
            if (actual != manifest.sha256) {
                throw IOException(
                    "Checksum mismatch for ${manifest.fileName}: " +
                        "expected ${manifest.sha256}, got $actual"
                )
            }
            target.delete()
            if (!part.renameTo(target)) {
                throw IOException("Cannot move ${part.name} into place")
            }
            return target
        } finally {
            part.delete()
        }
    }
}
