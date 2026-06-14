package com.almaslowcore.oasis.features.activity.data.mapper

import com.almaslowcore.oasis.features.activity.data.local.entity.ActivityEntity
import com.almaslowcore.oasis.features.activity.data.local.entity.ActivityLogEntity
import com.almaslowcore.oasis.features.activity.data.local.entity.ActivitySubtaskEntity
import com.almaslowcore.oasis.features.activity.domain.model.ActivityLogModel
import com.almaslowcore.oasis.features.activity.domain.model.ActivityModel
import com.almaslowcore.oasis.features.activity.domain.model.ActivitySubtaskModel
import com.almaslowcore.oasis.features.activity.data.local.entity.ActivitySubtaskLogEntity
import com.almaslowcore.oasis.features.activity.domain.model.ActivitySubtaskLogModel

fun ActivityEntity.toDomainModel(): ActivityModel {
    return ActivityModel(
        id = id,
        title = title,
        description = description,
        iconName = iconName,
        colorHex = colorHex,
        activityType = activityType,
        dueDate = dueDate,
        trackingType = trackingType,
        measurableMode = measurableMode,
        targetValue = targetValue,
        unit = unit,
        categoryId = categoryId,
        lifeAreaId = lifeAreaId,
        timeOfDay = timeOfDay,
        specificTimeMinutes = specificTimeMinutes,
        repeatInterval = repeatInterval,
        repeatUnit = repeatUnit,
        repeatStartDate = repeatStartDate,
        repeatEndType = repeatEndType,
        repeatEndDate = repeatEndDate,
        repeatEndOccurrences = repeatEndOccurrences,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isArchived = isArchived
    )
}

fun ActivityModel.toEntity(
    updatedAt: Long = this.updatedAt
): ActivityEntity {
    return ActivityEntity(
        id = id,
        title = title,
        description = description,
        iconName = iconName,
        colorHex = colorHex,
        activityType = activityType,
        dueDate = dueDate,
        trackingType = trackingType,
        measurableMode = measurableMode,
        targetValue = targetValue,
        unit = unit,
        categoryId = categoryId,
        lifeAreaId = lifeAreaId,
        timeOfDay = timeOfDay,
        specificTimeMinutes = specificTimeMinutes,
        repeatInterval = repeatInterval,
        repeatUnit = repeatUnit,
        repeatStartDate = repeatStartDate,
        repeatEndType = repeatEndType,
        repeatEndDate = repeatEndDate,
        repeatEndOccurrences = repeatEndOccurrences,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isArchived = isArchived
    )
}

fun ActivityLogEntity.toDomainModel(): ActivityLogModel {
    return ActivityLogModel(
        id = id,
        activityId = activityId,
        date = date,
        isCompleted = isCompleted,
        value = value,
        note = note,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun ActivitySubtaskEntity.toDomainModel(
    isCompleted: Boolean
): ActivitySubtaskModel {
    return ActivitySubtaskModel(
        id = id,
        activityId = activityId,
        title = title,
        orderIndex = orderIndex,
        isCompleted = isCompleted,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isArchived = isArchived
    )
}

fun ActivitySubtaskModel.toEntity(): ActivitySubtaskEntity {
    return ActivitySubtaskEntity(
        id = id,
        activityId = activityId,
        title = title,
        orderIndex = orderIndex,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isArchived = isArchived
    )
}

fun ActivitySubtaskLogEntity.toDomainModel(): ActivitySubtaskLogModel {
    return ActivitySubtaskLogModel(
        id = id,
        subtaskId = subtaskId,
        date = date,
        isCompleted = isCompleted,
        completedAt = completedAt,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}