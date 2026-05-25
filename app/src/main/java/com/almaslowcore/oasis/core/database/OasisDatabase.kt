package com.almaslowcore.oasis.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.almaslowcore.oasis.features.activity.data.local.converter.ActivityTypeConverters
import com.almaslowcore.oasis.features.activity.data.local.dao.ActivityDao
import com.almaslowcore.oasis.features.activity.data.local.entity.ActivityEntity
import com.almaslowcore.oasis.features.activity.data.local.entity.ActivityLogEntity
import com.almaslowcore.oasis.features.activity.data.local.entity.ActivitySubtaskEntity
import com.almaslowcore.oasis.features.activity.data.local.entity.ActivitySubtaskLogEntity

@Database(
    entities = [
        ActivityEntity::class,
        ActivityLogEntity::class,
        ActivitySubtaskEntity::class,
        ActivitySubtaskLogEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(ActivityTypeConverters::class)
abstract class OasisDatabase : RoomDatabase() {

    abstract fun activityDao(): ActivityDao
}