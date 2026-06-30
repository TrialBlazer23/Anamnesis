package com.anamnesis.core.data.content

import android.content.Context
import com.anamnesis.core.domain.model.Passage
import com.anamnesis.core.domain.repository.ReaderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** [ReaderRepository] backed by the bundled content pack. */
class ContentPackReaderRepository(
    private val context: Context,
) : ReaderRepository {

    override suspend fun loadPassages(): List<Passage> = withContext(Dispatchers.IO) {
        ContentPackDataSource(ContentPackProvisioner.ensure(context)).loadPassages()
    }

    override suspend fun search(query: String): List<Passage> = withContext(Dispatchers.IO) {
        if (query.isBlank()) {
            emptyList()
        } else {
            ContentPackDataSource(ContentPackProvisioner.ensure(context)).search(query)
        }
    }
}
