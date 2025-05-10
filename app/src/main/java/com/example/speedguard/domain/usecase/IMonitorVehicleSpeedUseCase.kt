package com.example.speedguard.domain.usecase

import kotlinx.coroutines.flow.Flow

/**
 * Interface for monitoring the speed of vehicle.
 */
interface IMonitorVehicleSpeedUseCase {

    /**
     * Flow that emits the pair that contains speed and a boolean indicating
     * whether the speed violated at regular intervals.
     * @param intervalMillis the interval for each emission in millis.
     * @param speedLimit The speed limit value set by rental company.
     * Flow of Pair<double, boolean> indicating the speed of vehicle and is over speed or not.
     */
    suspend fun execute(intervalMillis: Long, speedLimit: Double): Flow<Pair<Double, Boolean>>

}