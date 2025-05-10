package com.example.speedguard.domain.usecaseImpl

import app.cash.turbine.test
import com.example.speedguard.domain.usecase.IMonitorVehicleSpeedUseCase
import com.example.speedguard.domain.usecase.ISpeedTrackingUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MonitorVehicleSpeedUseCaseImplTest {

    private val speedTrackingUseCase = object : ISpeedTrackingUseCase {
        override fun getSpeedFlow(intervalMillis: Long): Flow<Double> {
            return flowOf(10.0, 20.0, 50.0)
        }
    }

    private lateinit var monitorSpeedUseCase: IMonitorVehicleSpeedUseCase

    @Before
    fun setUp() {
        monitorSpeedUseCase = MonitorVehicleSpeedUseCaseImpl(speedTrackingUseCase)
    }

    @Test
    fun `execute emits speed with overspeeding flag correctly`() = runTest {
        val speedLimit = 40.0
        val resultFlow = monitorSpeedUseCase.execute(1000L, speedLimit)

        resultFlow.test {
            assertEquals(Pair(10.0, false), awaitItem())
            assertEquals(Pair(20.0, false), awaitItem())
            assertEquals(Pair(50.0, true), awaitItem())

            awaitComplete()
        }
    }

}