package com.anamnesis.core.domain.repository

import com.anamnesis.core.domain.model.Passage

/** Source of readable passages (a content pack, sample data, etc.). */
interface ReaderRepository {
    suspend fun loadPassages(): List<Passage>
}
