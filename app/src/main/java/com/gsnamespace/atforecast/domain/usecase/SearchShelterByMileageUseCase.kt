package com.gsnamespace.atforecast.domain.usecase

import com.gsnamespace.atforecast.data.repository.ShelterRepository
import com.gsnamespace.atforecast.domain.mapper.toDomain
import com.gsnamespace.atforecast.domain.model.Shelter
import javax.inject.Inject

/**
 * Use case for searching a shelter by NOBO mile marker.
 */
class SearchShelterByMileageUseCase @Inject constructor(
    private val shelterRepository: ShelterRepository
) {

    /**
     * Search for the closest shelter to the given mileage marker.
     *
     * @param mileage The NOBO mile marker (0-2500)
     * @return Result containing the closest shelter or error if not found
     */
    suspend operator fun invoke(mileage: Double): Result<Shelter> {
        return try {
            // Validate mileage range
            if (mileage < 0 || mileage > 2500) {
                return Result.failure(IllegalArgumentException("Mileage must be between 0 and 2500"))
            }

            val shelter = shelterRepository.searchByMileage(mileage)
            if (shelter != null) {
                Result.success(shelter.toDomain())
            } else {
                Result.failure(NoSuchElementException("No shelter found near mileage $mileage"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
