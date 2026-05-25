package com.almaslowcore.oasis.core.di

/*
* Provide OasisDatabase, Dao
*
 */

import android.content.Context
import androidx.room.Room
import com.almaslowcore.oasis.core.database.OasisDatabase
import com.almaslowcore.oasis.features.activity.data.local.dao.ActivityDao
import com.almaslowcore.oasis.features.journal.data.local.JournalDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideOasisDatabase(
        @ApplicationContext context: Context
    ): OasisDatabase {
        return Room.databaseBuilder(
            context,
            OasisDatabase::class.java,
            "oasis_database"
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    fun provideActivityDao(
        database: OasisDatabase
    ): ActivityDao {
        return database.activityDao()
    }

    @Provides
    fun provideJournalDao(
        database: OasisDatabase
    ): JournalDao {
        return database.journalDao()
    }
}