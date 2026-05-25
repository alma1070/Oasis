package com.almaslowcore.oasis.features.activity.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "activity_logs",
    foreignKeys = [
        ForeignKey(
            entity = ActivityEntity::class,
            parentColumns = ["id"],
            childColumns = ["activityId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["activityId"]),
        Index(
            value = ["activityId", "date"],
            unique = true
        )
    ]
)
data class ActivityLogEntity(
    @PrimaryKey
    val id: String,

    val activityId: String,

    /**
     * Format: yyyy-MM-dd
     * Ví dụ: 2026-05-24
     */
    val date: String,

    val isCompleted: Boolean = false,

    /**
     * Dùng cho measurable numeric.
     * Ví dụ:
     * - uống nước: 1250.0 ml
     * - đọc sách: 20.0 trang
     * - học tập: 60.0 phút
     *
     * Với YES_NO hoặc CHECKLIST có thể để null.
     */
    val value: Double? = null,

    val note: String? = null,

    val createdAt: Long,
    val updatedAt: Long
)