package com.example.speedguard.domain

interface ISpeedLimitProvider {
    suspend fun getSpeedLimit(vehicleId: String, carRegNumber: String): Double
}