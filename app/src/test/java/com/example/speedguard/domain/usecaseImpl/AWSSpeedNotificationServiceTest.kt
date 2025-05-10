package com.example.speedguard.domain.usecaseImpl

import android.util.Log
import com.example.speedguard.domain.usecase.ISpeedViolationNotifier
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

class AWSSpeedNotificationServiceTest {
    private val notifier: ISpeedViolationNotifier = AWSSpeedNotificationService()

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `notifyCompany should log without throwing`() {
        notifier.notifyCompany("TEST-123", 75.0)
        verify {
            Log.d("AWSSpeedNotificationService", match { it.contains("notifyCompany") })
        }
    }

    @Test
    fun `alertUser should log without throwing`() {
        notifier.alertUser("TEST-456", 85.5)
        verify {
            Log.d("AWSSpeedNotificationService", match { it.contains("alertUser") })
        }
    }
}