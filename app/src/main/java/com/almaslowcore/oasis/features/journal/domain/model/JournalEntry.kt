package com.almaslowcore.oasis.features.journal.domain.model

data class JournalEntry(
    val id: Long = 0L,

    val moodType: MoodType,

    val note: String,

    /**
     * Timestamp in milliseconds.
     *
     * This represents the actual date and time of the mood check-in,
     * not necessarily the time the entry was created.
     */
    val dateTime: Long,

    /**
     * Optional related activity.
     *
     * MVP rule:
     * One journal mood entry can be linked to at most one activity.
     */
    val relatedActivityId: String? = null,

    /**
     * This is mainly for displaying metadata in the UI.
     * It can be filled by repository/mapper later if needed.
     */
    val relatedActivityTitle: String? = null,

    /**
     * Timestamp when this entry was created.
     */
    val createdAt: Long,

    /**
     * Timestamp when this entry was last updated.
     * Null means the entry has not been edited yet.
     */
    val updatedAt: Long? = null
) {
    val moodScore: Int
        get() = moodType.score
}