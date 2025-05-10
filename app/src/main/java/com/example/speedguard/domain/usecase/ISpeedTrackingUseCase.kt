package com.example.speedguard.domain.usecase

import kotlinx.coroutines.flow.Flow


/**
 * Interface for fetching vehicle speed as flow.
 */
interface ISpeedTrackingUseCase {
    /**
     * The flow emit the instantaneous speed of the vehicle at the requested intervals
     * @param intervalMillis interval for each emission in millis.
     * @return Flow of double indicating the speed of vehicle.
     */
    fun getSpeedFlow(intervalMillis: Long): Flow<Double>
}