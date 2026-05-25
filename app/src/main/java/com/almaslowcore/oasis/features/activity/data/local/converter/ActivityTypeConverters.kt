package com.almaslowcore.oasis.features.activity.data.local.converter

import androidx.room.TypeConverter
import com.almaslowcore.oasis.features.activity.domain.model.ActivityTrackingType
import com.almaslowcore.oasis.features.activity.domain.model.ActivityType
import com.almaslowcore.oasis.features.activity.domain.model.MeasurableMode
import com.almaslowcore.oasis.features.activity.domain.model.RepeatEndType
import com.almaslowcore.oasis.features.activity.domain.model.RepeatUnit
import com.almaslowcore.oasis.features.activity.domain.model.TimeOfDay

class ActivityTypeConverters {

    @TypeConverter
    fun fromActivityType(value: ActivityType?): String? {
        return value?.name
    }

    @TypeConverter
    fun toActivityType(value: String?): ActivityType? {
        return value?.let {
            ActivityType.valueOf(it)
        }
    }

    @TypeConverter
    fun fromActivityTrackingType(value: ActivityTrackingType?): String? {
        return value?.name
    }

    @TypeConverter
    fun toActivityTrackingType(value: String?): ActivityTrackingType? {
        return value?.let {
            ActivityTrackingType.valueOf(it)
        }
    }

    @TypeConverter
    fun fromMeasurableMode(value: MeasurableMode?): String? {
        return value?.name
    }

    @TypeConverter
    fun toMeasurableMode(value: String?): MeasurableMode? {
        return value?.let {
            MeasurableMode.valueOf(it)
        }
    }

    @TypeConverter
    fun fromTimeOfDay(value: TimeOfDay?): String? {
        return value?.name
    }

    @TypeConverter
    fun toTimeOfDay(value: String?): TimeOfDay? {
        return value?.let {
            TimeOfDay.valueOf(it)
        }
    }

    @TypeConverter
    fun fromRepeatUnit(value: RepeatUnit?): String? {
        return value?.name
    }

    @TypeConverter
    fun toRepeatUnit(value: String?): RepeatUnit? {
        return value?.let {
            RepeatUnit.valueOf(it)
        }
    }

    @TypeConverter
    fun fromRepeatEndType(value: RepeatEndType?): String? {
        return value?.name
    }

    @TypeConverter
    fun toRepeatEndType(value: String?): RepeatEndType? {
        return value?.let {
            RepeatEndType.valueOf(it)
        }
    }
}