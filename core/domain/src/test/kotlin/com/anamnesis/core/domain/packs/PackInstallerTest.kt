package com.anamnesis.core.domain.packs

import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.security.MessageDigest
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class PackManifestTest {

    @Test
    fun parsesPipelineManifest() {
        val manifest = PackManifest.parse(
            """
            {
              "pack_id": "iliad",
              "file_name": "iliad.db",
              "size_bytes": 12345678,
              "sha256": "${"ab".repeat(32)}",
              "counts": {"passages": 15687}
            }
            """
        )
        assertEquals("iliad.db", manifest.fileName)
        assertEquals(12_345_678L, manifest.sizeBytes)
        assertEquals("ab".repeat(32), manifest.sha256)
    }

    @Test
    fun rejectsManifestWithoutChecksum() {
        try {
            PackManifest.parse("""{"file_name": "x.db", "size_bytes": 1}""")
            fail("expected IllegalArgumentException")
        } catch (expected: IllegalArgumentException) {
            assertTrue("sha256" in expected.message.orEmpty())
        }
    }
}

class PackInstallerTest {

    @get:Rule
    val tmp = TemporaryFolder()

    private val payload = ByteArray(200_000) { (it % 251).toByte() }
    private val sha256 =
        MessageDigest.getInstance("SHA-256").digest(payload).joinToString("") { "%02x".format(it) }

    private fun manifestJson(sha: String = sha256) =
        """{"file_name": "iliad.db", "size_bytes": ${payload.size}, "sha256": "$sha"}"""

    private fun fetcher(manifest: String): (String) -> InputStream = { url ->
        when {
            url.endsWith(".manifest.json") -> ByteArrayInputStream(manifest.toByteArray())
            url.endsWith("iliad.db") -> ByteArrayInputStream(payload)
            else -> throw IOException("unexpected url $url")
        }
    }

    @Test
    fun installsVerifiedPackAndReportsProgress() {
        val dir = tmp.newFolder("packs")
        val progress = mutableListOf<Pair<Long, Long>>()

        val installed = PackInstaller(fetcher(manifestJson()))
            .install(BASE, "iliad.manifest.json", dir) { copied, total ->
                progress += copied to total
            }

        assertEquals(File(dir, "iliad.db"), installed)
        assertArrayEquals(payload, installed.readBytes())
        assertFalse(File(dir, "iliad.db.part").exists())
        assertEquals(payload.size.toLong(), progress.last().first)
        assertTrue(progress.all { it.second == payload.size.toLong() })
    }

    @Test
    fun checksumMismatchLeavesNothingBehind() {
        val dir = tmp.newFolder("packs")
        try {
            PackInstaller(fetcher(manifestJson(sha = "0".repeat(64))))
                .install(BASE, "iliad.manifest.json", dir)
            fail("expected IOException")
        } catch (expected: IOException) {
            assertTrue("Checksum mismatch" in expected.message.orEmpty())
        }
        assertEquals(emptyList<String>(), dir.list().orEmpty().toList())
    }

    @Test
    fun replacesAnExistingInstall() {
        val dir = tmp.newFolder("packs")
        File(dir, "iliad.db").writeBytes(byteArrayOf(1, 2, 3))

        val installed = PackInstaller(fetcher(manifestJson()))
            .install(BASE, "iliad.manifest.json", dir)

        assertArrayEquals(payload, installed.readBytes())
    }

    private companion object {
        const val BASE = "https://example.test/releases/"
    }
}
