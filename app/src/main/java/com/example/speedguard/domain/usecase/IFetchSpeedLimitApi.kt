package com.example.speedguard.domain.usecase

import com.example.speedguard.data.Rental

interface IFetchSpeedLimitApi {
    suspend fun fetchSpeedLimit(vehicleId: String, carRegNumber: String): Rental
}