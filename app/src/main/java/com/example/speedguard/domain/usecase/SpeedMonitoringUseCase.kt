package com.example.speedguard.domain.usecase

import com.example.speedguard.data.Rental
import kotlinx.coroutines.flow.Flow


interface SpeedMonitoringUseCase {
    fun registerRental(rental: Rental)
    fun observeSpeed(vehicleId: String): Flow<Int>
}