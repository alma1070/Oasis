package com.almaslowcore.oasis.core.di

/*
* Bind Repository -> RepositoryImpl
 */

import com.almaslowcore.oasis.features.activity.data.repository.ActivityRepositoryImpl
import com.almaslowcore.oasis.features.activity.domain.repository.ActivityRepository
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
}