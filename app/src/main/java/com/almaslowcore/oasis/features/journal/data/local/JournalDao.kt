package com.almaslowcore.oasis.features.journal.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {

    @Query(
        """
        SELECT * FROM journal_entries
        WHERE dateTime >= :startTime
        AND dateTime < :endTime
        ORDER BY dateTime DESC
        """
    )
    fun observeEntriesBetween(
        startTime: Long,
        endTime: Long
    ): Flow<List<JournalEntryEntity>>

    @Query(
        """
        SELECT * FROM journal_entries
        WHERE id = :id
        LIMIT 1
        """
    )
    suspend fun getEntryById(id: Long): JournalEntryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: JournalEntryEntity): Long

    @Update
    suspend fun updateEntry(entry: JournalEntryEntity)

    @Delete
    suspend fun deleteEntry(entry: JournalEntryEntity)

    @Query(
        """
        DELETE FROM journal_entries
        WHERE id = :id
        """
    )
    suspend fun deleteEntryById(id: Long)
}