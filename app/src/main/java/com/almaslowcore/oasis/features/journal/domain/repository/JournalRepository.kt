package com.almaslowcore.oasis.features.journal.domain.repository

import com.almaslowcore.oasis.features.journal.domain.model.JournalEntry
import com.almaslowcore.oasis.features.journal.domain.model.MoodType
import kotlinx.coroutines.flow.Flow

interface JournalRepository {

    fun observeEntriesBetween(
        startTime: Long,
        endTime: Long
    ): Flow<List<JournalEntry>>

    suspend fun getEntryById(id: Long): JournalEntry?

    suspend fun createEntry(
        moodType: MoodType,
        note: String,
        dateTime: Long,
        relatedActivityId: String? = null
    ): Long

    suspend fun updateEntry(entry: JournalEntry)

    suspend fun deleteEntryById(id: Long)
}