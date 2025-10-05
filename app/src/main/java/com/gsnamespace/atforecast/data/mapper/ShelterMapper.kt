package com.gsnamespace.atforecast.data.mapper

import com.gsnamespace.atforecast.data.local.entity.ShelterEntity
import com.gsnamespace.atforecast.data.remote.dto.ShelterDto

/**
 * Converts ShelterDto from API to ShelterEntity for database storage.
 */
fun ShelterDto.toEntity(stateId: Int): ShelterEntity {
    return ShelterEntity(
        shelterId = shelterId,
        name = name,
        mileage = mileage,
        elevation = elevation,
        latitude = latitude,
        longitude = longitude,
        stateId = this.stateId ?: stateId
    )
}

/**
 * Converts list of ShelterDtos to list of ShelterEntities.
 */
fun List<ShelterDto>.toEntities(stateId: Int): List<ShelterEntity> {
    return map { it.toEntity(stateId) }
}
