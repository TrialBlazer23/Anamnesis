package com.anamnesis.core.data

import android.content.Context
import androidx.room.Room
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

/**
 * Builds the encrypted user database.
 *
 * Uses `net.zetetic:sqlcipher-android` (the modern, 16KB-page-compatible
 * library — the legacy `android-database-sqlcipher` is EOL). The passphrase is
 * obtained from [DatabaseKeyManager] (Android Keystore-backed) and passed
 * through [SupportOpenHelperFactory] into Room's `openHelperFactory`.
 */
object DatabaseFactory {

    fun create(context: Context): AnamnesisDatabase {
        System.loadLibrary("sqlcipher")
        val passphrase = DatabaseKeyManager.getOrCreatePassphrase(context)
        val factory = SupportOpenHelperFactory(passphrase)
        return Room.databaseBuilder(
            context.applicationContext,
            AnamnesisDatabase::class.java,
            "anamnesis.db",
        )
            .openHelperFactory(factory)
            .addMigrations(AnamnesisDatabase.MIGRATION_1_2, AnamnesisDatabase.MIGRATION_2_3)
            .build()
    }
}
