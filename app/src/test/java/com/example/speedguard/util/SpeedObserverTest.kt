package com.example.speedguard.util

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [30])
class SpeedObserverTest {
    private lateinit var context: Context
    private lateinit var client: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager


    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        locationManager = mockk()
        client = mockk(relaxed = true)

        mockkStatic(LocationServices::class)
        mockkStatic(ActivityCompat::class)
        //mockkStatic(Context::getSystemService)

        every { LocationServices.getFusedLocationProviderClient(context) } returns client
        every { context.getSystemService(LocationManager::class.java) } returns locationManager
        every { context.getSystemService(Context.LOCATION_SERVICE) } returns locationManager


    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun `flow closes when no provider is enabled`() = runTest {
        every { !locationManager.isProviderEnabled(any()) } returns true
        every {
            ActivityCompat.checkSelfPermission(
                context,
                any()
            )
        } returns PackageManager.PERMISSION_DENIED

        val observer = SpeedObserver(context)
        val speed = observer.observeSpeed(1000L).firstOrNull()

        assertNull(speed)
    }

    @Test
    fun `closes flow if permissions not granted`() = runTest {
        every { !locationManager.isProviderEnabled(any()) } returns false
        every {
            ActivityCompat.checkSelfPermission(
                context,
                any()
            )
        } returns PackageManager.PERMISSION_DENIED

        val observer = SpeedObserver(context)
        val result = observer.observeSpeed(1000).firstOrNull()

        assertNull(result)
    }

    @Test
    fun `emits speed value when location received`() = runTest {
        every { locationManager.isProviderEnabled(any()) } returns true
        every {
            ActivityCompat.checkSelfPermission(
                context,
                any()
            )
        } returns PackageManager.PERMISSION_GRANTED

        val callbackSlot = slot<LocationCallback>()
        val location = mockk<Location> {
            every { speed } returns 15.0f //m/s
        }

        every {
            client.requestLocationUpdates(any(), capture(callbackSlot), any())
        } answers {
            mockk() // Return dummy Task
        }

        val observer = SpeedObserver(context)

        var result: Double? = null
        val job = launch {
            observer.observeSpeed(1000L).collect {
                result = it
            }
        }

        advanceUntilIdle()
        val resultObj = LocationResult.create(listOf(location))
        callbackSlot.captured.onLocationResult(resultObj)

        advanceUntilIdle()

        assertEquals(54.0, result)
        job.cancel()
    }

    @Test
    fun `removes updates when flow is closed`() = runTest {
        every { locationManager.isProviderEnabled(any()) } returns true
        every {
            ActivityCompat.checkSelfPermission(
                context,
                any()
            )
        } returns PackageManager.PERMISSION_GRANTED

        val callback = slot<LocationCallback>()

        every {
            client.requestLocationUpdates(any(),capture(callback), any())
        } returns mockk()

        every {
            client.removeLocationUpdates(any<LocationCallback>())
        } returns mockk<Task<Void>>(relaxed = true)

        val observer = SpeedObserver(context)
        val job = launch {
            observer.observeSpeed(1000L).collect {
                cancel() //Simulate termination
            }
        }

        advanceUntilIdle()

        // Simulate location update to enter collect block
        val location = mockk<Location> { every { speed } returns 10.0f }
        val locationResult = LocationResult.create(listOf(location))
        callback.captured.onLocationResult(locationResult)

        advanceUntilIdle()
        verify { client.removeLocationUpdates(callback.captured) }

        job.cancel()
    }

//    @Test
//    fun `observer closes when permissions are not granted`() = runTest {
//        val observer = SpeedObserver(context)
//        val flow = observer.observeSpeed(1000L)
//
//        val values = mutableListOf<Double>()
//        val job = launch {
//            flow.collect { values.add(it) }
//        }
//        advanceUntilIdle()
//        assertTrue(values.isEmpty())
//        job.cancel()
//    }

}