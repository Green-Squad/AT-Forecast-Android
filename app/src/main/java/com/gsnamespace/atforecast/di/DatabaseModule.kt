package com.gsnamespace.atforecast.di

import android.content.Context
import androidx.room.Room
import com.gsnamespace.atforecast.data.local.ATForecastDatabase
import com.gsnamespace.atforecast.data.local.dao.DailyWeatherDao
import com.gsnamespace.atforecast.data.local.dao.HourlyWeatherDao
import com.gsnamespace.atforecast.data.local.dao.ShelterDao
import com.gsnamespace.atforecast.data.local.dao.StateDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing database-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ATForecastDatabase {
        return Room.databaseBuilder(
            context,
            ATForecastDatabase::class.java,
            ATForecastDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideStateDao(database: ATForecastDatabase): StateDao {
        return database.stateDao()
    }

    @Provides
    @Singleton
    fun provideShelterDao(database: ATForecastDatabase): ShelterDao {
        return database.shelterDao()
    }

    @Provides
    @Singleton
    fun provideDailyWeatherDao(database: ATForecastDatabase): DailyWeatherDao {
        return database.dailyWeatherDao()
    }

    @Provides
    @Singleton
    fun provideHourlyWeatherDao(database: ATForecastDatabase): HourlyWeatherDao {
        return database.hourlyWeatherDao()
    }
}
