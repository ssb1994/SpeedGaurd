package com.example.speedguard.domain.usecaseImpl

import app.cash.turbine.test
import com.example.speedguard.util.SpeedObserver
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class SpeedTrackingUseCaseImplTest {
    private val speedObserver = mockk<SpeedObserver>()

    @Test
    fun `getSpeedFlow returns expected speed values`() = runTest {
        every {
            speedObserver.observeSpeed(1000L)
        } returns flowOf(15.0, 30.0, 60.0)

        val useCase = SpeedTrackingUseCaseImpl(speedObserver)

        useCase.getSpeedFlow(1000L).test {
            assertEquals(15.0, awaitItem(),  0.001)
            assertEquals(30.0, awaitItem(), 0.001)
            assertEquals(60.0, awaitItem(), 0.001)
            awaitComplete()
        }
    }

}