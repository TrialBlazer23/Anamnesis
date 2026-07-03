package com.anamnesis.core.domain.packs

/**
 * The fields of a pack's `.manifest.json` the installer needs. The manifests
 * are flat JSON written by the pipeline; the values are extracted with regexes
 * so the pure-domain module needs no JSON dependency (org.json is Android-only
 * at runtime and stubbed in JVM unit tests).
 */
data class PackManifest(
    val fileName: String,
    val sizeBytes: Long,
    val sha256: String,
) {
    companion object {
        private val FILE_NAME = Regex("\"file_name\"\\s*:\\s*\"([^\"]+)\"")
        private val SIZE_BYTES = Regex("\"size_bytes\"\\s*:\\s*(\\d+)")
        private val SHA256 = Regex("\"sha256\"\\s*:\\s*\"([0-9a-fA-F]{64})\"")

        fun parse(json: String): PackManifest {
            val fileName = FILE_NAME.find(json)?.groupValues?.get(1)
                ?: throw IllegalArgumentException("manifest missing file_name")
            val sizeBytes = SIZE_BYTES.find(json)?.groupValues?.get(1)?.toLong()
                ?: throw IllegalArgumentException("manifest missing size_bytes")
            val sha256 = SHA256.find(json)?.groupValues?.get(1)?.lowercase()
                ?: throw IllegalArgumentException("manifest missing sha256")
            return PackManifest(fileName, sizeBytes, sha256)
        }
    }
}
