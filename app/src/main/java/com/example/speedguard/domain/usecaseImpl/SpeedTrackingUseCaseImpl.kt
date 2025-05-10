package com.example.speedguard.domain.usecaseImpl

import com.example.speedguard.domain.usecase.ISpeedTrackingUseCase
import com.example.speedguard.util.SpeedObserver
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of [ISpeedTrackingUseCase] to get the instantaneous speed of the vehicle at
 * regular intervals
 */
class SpeedTrackingUseCaseImpl(
    private val speedObserver: SpeedObserver,
) : ISpeedTrackingUseCase {

    /**
     * To get the instantaneous speed of the vehicle.
     * @param intervalMillis The interval for each emission.
     * @return flow of double indicating the speed of the vehicle at the instance.
     */
    override fun getSpeedFlow(intervalMillis: Long): Flow<Double> {
       return speedObserver.observeSpeed(intervalMillis)
    }
}