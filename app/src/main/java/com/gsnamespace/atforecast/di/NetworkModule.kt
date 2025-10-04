package com.gsnamespace.atforecast.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module for providing network-related dependencies.
 * This will be implemented in Phase 2 when we create the Retrofit API service.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    // Retrofit, OkHttp, and API service providers will be added in Phase 2
}
