package com.almaslowcore.oasis.features.activity.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "activity_subtask_logs",
    foreignKeys = [
        ForeignKey(
            entity = ActivitySubtaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["subtaskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["subtaskId"]),
        Index(
            value = ["subtaskId", "date"],
            unique = true
        )
    ]
)
data class ActivitySubtaskLogEntity(
    @PrimaryKey
    val id: String,

    val subtaskId: String,

    /**
     * Format: yyyy-MM-dd
     * Ví dụ: 2026-05-24
     */
    val date: String,

    val isCompleted: Boolean = false,

    /**
     * Thời điểm subtask được hoàn thành.
     * Nếu chưa hoàn thành thì null.
     */
    val completedAt: Long? = null,

    val createdAt: Long,
    val updatedAt: Long
)