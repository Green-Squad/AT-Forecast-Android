package com.gsnamespace.atforecast.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module for providing location-related dependencies.
 * This will be implemented in Phase 5 when we add GPS functionality.
 */
@Module
@InstallIn(SingletonComponent::class)
object LocationModule {
    // FusedLocationProviderClient will be added in Phase 5
}
