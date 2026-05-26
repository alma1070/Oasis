package com.almaslowcore.oasis.features.journal.data.repository

import com.almaslowcore.oasis.features.journal.data.local.JournalDao
import com.almaslowcore.oasis.features.journal.data.local.JournalEntryEntity
import com.almaslowcore.oasis.features.journal.data.mapper.toDomain
import com.almaslowcore.oasis.features.journal.data.mapper.toEntity
import com.almaslowcore.oasis.features.journal.domain.model.JournalEntry
import com.almaslowcore.oasis.features.journal.domain.model.MoodType
import com.almaslowcore.oasis.features.journal.domain.repository.JournalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class JournalRepositoryImpl @Inject constructor(
    private val journalDao: JournalDao
) : JournalRepository {

    override fun observeEntriesBetween(
        startTime: Long,
        endTime: Long
    ): Flow<List<JournalEntry>> {
        return journalDao
            .observeEntriesBetween(
                startTime = startTime,
                endTime = endTime
            )
            .map { entries ->
                entries.map { it.toDomain() }
            }
    }

    override suspend fun getEntryById(id: Long): JournalEntry? {
        return journalDao
            .getEntryById(id)
            ?.toDomain()
    }

    override suspend fun createEntry(
        moodType: MoodType,
        note: String,
        dateTime: Long,
        relatedActivityId: String?
    ): Long {
        val now = System.currentTimeMillis()

        val entity = JournalEntryEntity(
            moodType = moodType.name,
            moodScore = moodType.score,
            note = note,
            dateTime = dateTime,
            relatedActivityId = relatedActivityId,
            createdAt = now,
            updatedAt = null
        )

        return journalDao.insertEntry(entity)
    }

    override suspend fun updateEntry(entry: JournalEntry) {
        val updatedEntry = entry.copy(
            updatedAt = System.currentTimeMillis()
        )

        journalDao.updateEntry(updatedEntry.toEntity())
    }

    override suspend fun deleteEntryById(id: Long) {
        journalDao.deleteEntryById(id)
    }
}