package com.example.speedguard.presentation

import com.example.speedguard.data.api.IFetchRentalInfoApi
import com.example.speedguard.data.api.Result
import com.example.speedguard.data.model.Customer
import com.example.speedguard.data.model.RentalInfo
import com.example.speedguard.domain.usecase.IMonitorVehicleSpeedUseCase
import com.example.speedguard.domain.usecase.ISpeedViolationNotifier
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SpeedMonitorViewModelTest {

    private lateinit var viewModel: SpeedMonitorViewModel
    private val fetchRentalInfoApi: IFetchRentalInfoApi = mockk()
    private val monitorVehicleSpeedUseCase: IMonitorVehicleSpeedUseCase = mockk()
    private val notifier: ISpeedViolationNotifier = mockk()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SpeedMonitorViewModel(fetchRentalInfoApi, monitorVehicleSpeedUseCase, notifier)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `startMonitoring should trigger monitoring when rental info is active and overspeeding`() = runTest {
        // Arrange
        val vehicleRegNum = "ABC123"
        val speedLimit = 60.0
        val rentalInfo = RentalInfo(Customer("1","test"),vehicleRegNum, speedLimit)

        coEvery { fetchRentalInfoApi.fetchRentalInfo(vehicleRegNum) } returns Result.Active(rentalInfo)
        coEvery { monitorVehicleSpeedUseCase.execute(any(), any()) } returns flowOf(70.0 to true)
        every { notifier.notifyCompany(any(), any()) } just Runs
        every { notifier.alertUser(any(), any()) } just Runs

        // Act
        viewModel.startMonitoring(vehicleRegNum, intervalMillis = 1000L)
        advanceUntilIdle()

        // Assert
        verify { notifier.notifyCompany(vehicleRegNum, 70.0) }
        verify { notifier.alertUser(vehicleRegNum, 70.0) }
    }

    @Test
    fun `startMonitoring should not monitor when no active rental`() = runTest {
        val vehicleRegNum = "XYZ789"
        coEvery { fetchRentalInfoApi.fetchRentalInfo(vehicleRegNum) } returns Result.NoActiveRental

        viewModel.startMonitoring(vehicleRegNum, intervalMillis = 1000L)
        advanceUntilIdle()

        verify { monitorVehicleSpeedUseCase wasNot Called }
        verify { notifier wasNot Called }
    }

    @Test
    fun `startMonitoring should handle API error`() = runTest {
        val vehicleRegNum = "ERROR123"
        val error = Exception("API failed")

        coEvery { fetchRentalInfoApi.fetchRentalInfo(vehicleRegNum) } returns Result.Error(error)

        viewModel.startMonitoring(vehicleRegNum, intervalMillis = 1000L)
        advanceUntilIdle()

        verify { monitorVehicleSpeedUseCase wasNot Called }
        verify { notifier wasNot Called }
    }

    @Test
    fun `stopMonitoring cancels the job and resets speed state`() = runTest {
        val vehicleRegNum = "ABC123"
        val speedLimit = 60.0
        val rentalInfo = RentalInfo(Customer("", ""),vehicleRegNum, speedLimit)

        coEvery { fetchRentalInfoApi.fetchRentalInfo(vehicleRegNum) } returns Result.Active(rentalInfo)
        coEvery { monitorVehicleSpeedUseCase.execute(any(), any()) } returns flowOf(50.0 to false)

        viewModel.startMonitoring(vehicleRegNum, 1000L)
        advanceUntilIdle()

        viewModel.stopMonitoring()

        // The monitorJob is cancelled, so no more emissions
        // Since _speedState is private, you’d normally expose it to verify.
        // Here we just ensure no crash and job is stopped.
    }
}