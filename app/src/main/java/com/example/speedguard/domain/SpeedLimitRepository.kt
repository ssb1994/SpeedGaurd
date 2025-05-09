package com.example.speedguard.domain

import com.example.speedguard.data.Rental
import com.example.speedguard.domain.usecase.IFetchSpeedLimitApi

class SpeedLimitRepository(private val fetchSpeedLimitApi: IFetchSpeedLimitApi) :
    ISpeedLimitProvider {
    private val cachedLimit = mutableMapOf<String, Rental>()

    override suspend fun getSpeedLimit(vehicleId: String,
                               carRegNumber: String): Double {
        return cachedLimit[vehicleId]?.speedLimit ?: fetchAndCache(vehicleId, carRegNumber)
    }

    private suspend fun fetchAndCache(vehicleId: String, carRegNumber: String): Double {
        val rental = fetchSpeedLimitApi.fetchSpeedLimit(
            vehicleId,
            carRegNumber
        )
        cachedLimit[vehicleId] = rental

        return rental.speedLimit
    }

}