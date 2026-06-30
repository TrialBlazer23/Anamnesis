package com.anamnesis.core.data.srs

import android.content.Context
import com.anamnesis.core.data.DatabaseFactory
import com.anamnesis.core.domain.repository.SrsRepository

/**
 * Builds the [SrsRepository] backed by the encrypted database. Keeps Room types
 * inside `:core:data` so consumers (e.g. `:app`) only depend on the domain
 * interface and don't need Room on their compile classpath.
 */
object SrsRepositoryFactory {
    fun create(context: Context): SrsRepository =
        RoomSrsRepository(DatabaseFactory.create(context).cardDao())
}
