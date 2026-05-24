package com.almaslowcore.oasis.features.activity.data.local.entity

enum class ActivityType {
    HABIT,
    TASK
}

enum class ActivityTrackingType {
    YES_NO,
    MEASURABLE
}

enum class MeasurableMode {
    NUMERIC,
    CHECKLIST
}

enum class TimeOfDay {
    START_OF_DAY,
    AFTERNOON,
    EVENING,
    BEDTIME,
    ANYTIME,
    SPECIFIC_TIME
}

enum class RepeatUnit {
    DAY,
    WEEK,
    MONTH,
    YEAR
}

enum class RepeatEndType {
    NEVER,
    ON_DATE,
    AFTER_OCCURRENCES
}