package com.almaslowcore.oasis.core.di

/*
* Bind Repository -> RepositoryImpl
 */

import com.almaslowcore.oasis.features.activity.data.repository.ActivityRepositoryImpl
import com.almaslowcore.oasis.features.activity.domain.repository.ActivityRepository
import com.almaslowcore.oasis.features.gamification.data.repository.GamificationRepositoryImpl
import com.almaslowcore.oasis.features.gamification.domain.repository.GamificationRepository
import com.almaslowcore.oasis.features.journal.data.repository.JournalRepositoryImpl
import com.almaslowcore.oasis.features.journal.domain.repository.JournalRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindActivityRepository(
        impl: ActivityRepositoryImpl
    ): ActivityRepository

    @Binds
    @Singleton
    abstract fun bindJournalRepository(
        impl: JournalRepositoryImpl
    ): JournalRepository

    @Binds
    @Singleton
    abstract fun bindGamificationRepository(
        impl: GamificationRepositoryImpl
    ): GamificationRepository
}