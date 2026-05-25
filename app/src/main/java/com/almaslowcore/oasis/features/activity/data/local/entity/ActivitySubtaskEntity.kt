package com.almaslowcore.oasis.features.activity.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "activity_subtasks",
    foreignKeys = [
        ForeignKey(
            entity = ActivityEntity::class,
            parentColumns = ["id"],
            childColumns = ["activityId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["activityId"])
    ]
)
data class ActivitySubtaskEntity(
    @PrimaryKey
    val id: String,

    val activityId: String,

    val title: String,

    val orderIndex: Int,

    val createdAt: Long,
    val updatedAt: Long,

    val isArchived: Boolean = false
)