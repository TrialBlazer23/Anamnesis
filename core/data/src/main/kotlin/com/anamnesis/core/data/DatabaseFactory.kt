package com.anamnesis.core.data

import android.content.Context
import androidx.room.Room
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

/**
 * Builds the encrypted user database.
 *
 * Uses `net.zetetic:sqlcipher-android` (the modern, 16KB-page-compatible
 * library — the legacy `android-database-sqlcipher` is EOL). The passphrase is
 * passed through [SupportOpenHelperFactory] into Room's `openHelperFactory`.
 *
 * The passphrase MUST come from a secure source (Android Keystore-backed key),
 * not a literal — wiring that in is part of Phase 2.
 */
object DatabaseFactory {

    fun create(context: Context, passphrase: ByteArray): AnamnesisDatabase {
        System.loadLibrary("sqlcipher")
        val factory = SupportOpenHelperFactory(passphrase)
        return Room.databaseBuilder(
            context,
            AnamnesisDatabase::class.java,
            "anamnesis.db",
        )
            .openHelperFactory(factory)
            .build()
    }
}
