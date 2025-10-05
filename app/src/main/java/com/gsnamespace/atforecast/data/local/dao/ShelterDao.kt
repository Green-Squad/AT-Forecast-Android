package com.gsnamespace.atforecast.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gsnamespace.atforecast.data.local.entity.ShelterEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for accessing Shelter data from the database.
 */
@Dao
interface ShelterDao {

    @Query("SELECT * FROM shelters WHERE stateId = :stateId ORDER BY mileage ASC")
    fun getSheltersByState(stateId: Int): Flow<List<ShelterEntity>>

    @Query("SELECT * FROM shelters WHERE shelterId = :shelterId")
    suspend fun getShelterById(shelterId: Int): ShelterEntity?

    @Query("SELECT * FROM shelters WHERE shelterId = :shelterId")
    fun getShelterByIdFlow(shelterId: Int): Flow<ShelterEntity?>

    @Query("SELECT * FROM shelters ORDER BY mileage ASC")
    fun getAllShelters(): Flow<List<ShelterEntity>>

    @Query("""
        SELECT * FROM shelters
        WHERE mileage >= :mileage
        ORDER BY ABS(mileage - :mileage) ASC
        LIMIT 1
    """)
    suspend fun findShelterByMileage(mileage: Double): ShelterEntity?

    @Query("""
        SELECT s2.* FROM shelters s1
        INNER JOIN shelters s2 ON s2.mileage < s1.mileage
        WHERE s1.shelterId = :currentShelterId
        ORDER BY s2.mileage DESC
        LIMIT 1
    """)
    suspend fun getPreviousShelter(currentShelterId: Int): ShelterEntity?

    @Query("""
        SELECT s2.* FROM shelters s1
        INNER JOIN shelters s2 ON s2.mileage > s1.mileage
        WHERE s1.shelterId = :currentShelterId
        ORDER BY s2.mileage ASC
        LIMIT 1
    """)
    suspend fun getNextShelter(currentShelterId: Int): ShelterEntity?

    /**
     * Find shelters near given coordinates.
     * Note: This uses a simple bounding box. For production, consider using
     * the Haversine formula in the repository layer for accurate distance calculation.
     */
    @Query("""
        SELECT * FROM shelters
        WHERE latitude BETWEEN :minLat AND :maxLat
        AND longitude BETWEEN :minLon AND :maxLon
        ORDER BY mileage ASC
    """)
    suspend fun getSheltersInBounds(
        minLat: Double,
        maxLat: Double,
        minLon: Double,
        maxLon: Double
    ): List<ShelterEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShelters(shelters: List<ShelterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShelter(shelter: ShelterEntity)

    @Update
    suspend fun updateShelter(shelter: ShelterEntity)

    @Query("DELETE FROM shelters")
    suspend fun deleteAllShelters()
}
