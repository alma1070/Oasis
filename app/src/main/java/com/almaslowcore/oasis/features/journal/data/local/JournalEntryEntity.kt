package com.almaslowcore.oasis.features.journal.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "journal_entries",
    indices = [
        Index(value = ["dateTime"]),
        Index(value = ["relatedActivityId"])
    ]
)
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    /**
     * Store enum name as String.
     * Example: "VERY_BAD", "BAD", "NEUTRAL", "GOOD", "VERY_GOOD"
     */
    val moodType: String,

    /**
     * Store score separately for easier future analytics.
     * Example: 1..5
     */
    val moodScore: Int,

    /**
     * Full journal/mood note.
     */
    val note: String,

    /**
     * The actual check-in date time.
     * Stored as timestamp in milliseconds.
     */
    val dateTime: Long,

    /**
     * Optional related activity.
     * MVP rule: one journal entry can link to at most one activity.
     */
    val relatedActivityId: String? = null,

    /**
     * Timestamp when this entry was created.
     */
    val createdAt: Long,

    /**
     * Timestamp when this entry was last updated.
     */
    val updatedAt: Long? = null
)