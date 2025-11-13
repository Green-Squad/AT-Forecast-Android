package com.gsnamespace.atforecast.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gsnamespace.atforecast.data.local.entity.StateEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for accessing State data from the database.
 */
@Dao
interface StateDao {

    @Query("SELECT * FROM states ORDER BY name ASC")
    fun getAllStates(): Flow<List<StateEntity>>

    @Query("SELECT * FROM states ORDER BY name ASC")
    suspend fun getAllStatesOnce(): List<StateEntity>

    @Query("SELECT * FROM states WHERE stateId = :stateId")
    suspend fun getStateById(stateId: Int): StateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStates(states: List<StateEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertState(state: StateEntity)

    @Update
    suspend fun updateState(state: StateEntity)

    @Query("DELETE FROM states")
    suspend fun deleteAllStates()
}
