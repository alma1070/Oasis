package com.almaslowcore.oasis.features.journal.data.mapper


import com.almaslowcore.oasis.features.journal.data.local.JournalEntryEntity
import com.almaslowcore.oasis.features.journal.domain.model.JournalEntry
import com.almaslowcore.oasis.features.journal.domain.model.MoodType

fun JournalEntryEntity.toDomain(): JournalEntry {
    val mappedMoodType = moodType.toMoodTypeOrDefault()

    return JournalEntry(
        id = id,
        moodType = mappedMoodType,
        note = note,
        dateTime = dateTime,
        relatedActivityId = relatedActivityId,

        // MVP: chưa join với Activity table nên để null.
        // Sau này có thể fill bằng ActivityRepository hoặc query join.
        relatedActivityTitle = null,

        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun JournalEntry.toEntity(): JournalEntryEntity {
    return JournalEntryEntity(
        id = id,
        moodType = moodType.name,
        moodScore = moodType.score,
        note = note,
        dateTime = dateTime,
        relatedActivityId = relatedActivityId,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun String.toMoodTypeOrDefault(): MoodType {
    return runCatching {
        MoodType.valueOf(this)
    }.getOrDefault(MoodType.NEUTRAL)
}