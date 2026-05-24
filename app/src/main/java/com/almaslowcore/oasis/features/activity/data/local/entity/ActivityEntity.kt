package com.almaslowcore.oasis.features.activity.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey
    val id: String,

    // Basic information
    val title: String,
    val description: String? = null,
    val iconName: String? = null,
    val colorHex: String? = null,

    // Type
    val activityType: ActivityType,
    val trackingType: ActivityTrackingType,
    val measurableMode: MeasurableMode? = null,

    // Numeric measurable fields
    val targetValue: Double? = null,
    val unit: String? = null,

    // Classification
    val categoryId: String? = null,
    val lifeAreaId: String? = null,

    // Time
    val timeOfDay: TimeOfDay = TimeOfDay.ANYTIME,
    val specificTimeMinutes: Int? = null,

    // Repeat rule
    val repeatEnabled: Boolean = false,
    val repeatInterval: Int? = null,
    val repeatUnit: RepeatUnit? = null,
    val repeatStartDate: String? = null,
    val repeatEndType: RepeatEndType = RepeatEndType.NEVER,
    val repeatEndDate: String? = null,
    val repeatEndOccurrences: Int? = null,

    // Metadata
    val createdAt: Long,
    val updatedAt: Long,
    val isArchived: Boolean = false
)