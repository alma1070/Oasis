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
import com.almaslowcore.oasis.features.journal.data.local.JournalDao
import com.almaslowcore.oasis.features.journal.data.local.JournalEntryEntity
@Database(
    entities = [
        ActivityEntity::class,
        ActivityLogEntity::class,
        ActivitySubtaskEntity::class,
        ActivitySubtaskLogEntity::class,
        JournalEntryEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(ActivityTypeConverters::class)
abstract class OasisDatabase : RoomDatabase() {

    abstract fun activityDao(): ActivityDao
    abstract fun journalDao(): JournalDao
}