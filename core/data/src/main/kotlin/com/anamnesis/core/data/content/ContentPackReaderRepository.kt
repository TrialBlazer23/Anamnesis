package com.anamnesis.core.data.content

import android.content.Context
import com.anamnesis.core.domain.model.Passage
import com.anamnesis.core.domain.repository.ReaderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * [ReaderRepository] backed by a content pack. [dbPath] is resolved on every
 * call so switching the active pack (Library tab) takes effect on next load.
 */
class ContentPackReaderRepository(
    private val dbPath: () -> String,
) : ReaderRepository {

    constructor(context: Context) : this({ ContentPackProvisioner.ensure(context) })

    override suspend fun loadPassages(): List<Passage> = withContext(Dispatchers.IO) {
        ContentPackDataSource(dbPath()).loadPassages()
    }

    override suspend fun search(query: String): List<Passage> = withContext(Dispatchers.IO) {
        if (query.isBlank()) {
            emptyList()
        } else {
            ContentPackDataSource(dbPath()).search(query)
        }
    }
}
