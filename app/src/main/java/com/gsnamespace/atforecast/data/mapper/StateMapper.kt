package com.gsnamespace.atforecast.data.mapper

import com.gsnamespace.atforecast.data.local.entity.StateEntity
import com.gsnamespace.atforecast.data.remote.dto.StateDto

/**
 * Converts StateDto from API to StateEntity for database storage.
 */
fun StateDto.toEntity(): StateEntity {
    return StateEntity(
        stateId = stateId,
        name = name,
        averageHigh = averageHigh,
        averageLow = averageLow,
        updatedAt = System.currentTimeMillis()
    )
}

/**
 * Converts list of StateDtos to list of StateEntities.
 */
fun List<StateDto>.toEntities(): List<StateEntity> {
    return map { it.toEntity() }
}
