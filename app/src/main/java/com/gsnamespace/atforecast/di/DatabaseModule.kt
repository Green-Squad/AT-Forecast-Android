package com.gsnamespace.atforecast.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module for providing database-related dependencies.
 * This will be implemented in Phase 2 when we create Room entities and DAOs.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    // Database and DAO providers will be added in Phase 2
}
