package com.gsnamespace.atforecast.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module for providing DataStore dependencies.
 * This will be implemented when we add user preferences (temperature units, theme mode).
 */
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    // DataStore<Preferences> provider will be added when implementing settings
}
