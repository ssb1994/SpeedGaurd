package com.example.speedguard.domain.usecaseImpl

import com.example.speedguard.domain.usecase.IMonitorVehicleSpeedUseCase
import com.example.speedguard.domain.usecase.ISpeedTrackingUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of [IMonitorVehicleSpeedUseCase] to get the speed violation information at regular intervals.
 */
class MonitorVehicleSpeedUseCaseImpl(
    private val speedTrackingUseCase: ISpeedTrackingUseCase,
) : IMonitorVehicleSpeedUseCase {

    /**
     * To get the speed violation information at regular intervals.
     * @param intervalMillis The interval timing in milliseconds.
     * @param speedLimit The speed cutoff value set by rental company.
     * @return flow of pair<double, boolean> indicating the instantaneous speed and is over speeding.
     */
    override suspend fun execute(intervalMillis: Long, speedLimit: Double): Flow<Pair<Double, Boolean>> {
        return speedTrackingUseCase.getSpeedFlow(intervalMillis)
            .map { speed ->
                val isOverSpeeding = speed > speedLimit
                speed to isOverSpeeding
            }
    }


}