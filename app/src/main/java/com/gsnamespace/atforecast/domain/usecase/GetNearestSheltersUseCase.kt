package com.gsnamespace.atforecast.domain.usecase

import com.gsnamespace.atforecast.data.repository.ShelterRepository
import com.gsnamespace.atforecast.domain.mapper.toDomainShelters
import com.gsnamespace.atforecast.domain.model.Shelter
import javax.inject.Inject

/**
 * Use case for finding nearest shelters based on GPS coordinates.
 */
class GetNearestSheltersUseCase @Inject constructor(
    private val shelterRepository: ShelterRepository
) {

    /**
     * Find shelters nearest to the given GPS coordinates.
     *
     * @param latitude Current latitude
     * @param longitude Current longitude
     * @param maxResults Maximum number of results to return (default 5)
     * @return Result containing list of nearest shelters or error
     */
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        maxResults: Int = 5
    ): Result<List<Shelter>> {
        return try {
            val shelters = shelterRepository.findNearestShelters(
                latitude = latitude,
                longitude = longitude,
                maxResults = maxResults
            )
            Result.success(shelters.toDomainShelters())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
