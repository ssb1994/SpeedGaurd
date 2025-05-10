package com.example.speedguard.presentation

import androidx.lifecycle.LifecycleService
import com.example.speedguard.data.api.FetchRentalInfoApiImpl
import com.example.speedguard.domain.usecaseImpl.FirebaseSpeedNotificationService
import com.example.speedguard.domain.usecaseImpl.MonitorVehicleSpeedUseCaseImpl
import com.example.speedguard.domain.usecaseImpl.SpeedTrackingUseCaseImpl
import com.example.speedguard.util.Logger.logd
import com.example.speedguard.util.createNotification
import com.example.speedguard.util.SpeedObserver

/**
 * Android service that initializes and starts the speed monitoring system.
 * It sets up dependencies manually and invokes the ViewModel to handle logic.
 */
class SpeedTrackingService : LifecycleService() {
    private val notificationID = 101
    private val channelID = "speed_tracking"
    private val channelName = "Speed Tracking Channel"
    private val notificationContentTitle = "Speed Guard"
    private val notificationIcon = android.R.drawable.ic_menu_mylocation

    private lateinit var viewModel: SpeedMonitorViewModel

    // TODO: replace hardcoded reg number with intent extra or binding
    private val vehicleRegNum = "XX01AW1234"

    override fun onCreate() {
        super.onCreate()

        logd("OnCreate() service invoked")

        startForeground(
            notificationID, createNotification(
                this, "",
                channelID,
                channelName,
                notificationContentTitle,
                notificationIcon
            )
        )

        val fetchRentalInfoApi = FetchRentalInfoApiImpl()
        val speedObserver = SpeedObserver(this)
        val speedTrackingUseCase = SpeedTrackingUseCaseImpl(speedObserver)
        val monitorVehicleSpeedUseCase = MonitorVehicleSpeedUseCaseImpl(speedTrackingUseCase)
        val speedViolationNotifier = FirebaseSpeedNotificationService()

        viewModel = SpeedMonitorViewModel(
            fetchRentalInfoApi,
            monitorVehicleSpeedUseCase,
            speedViolationNotifier
        )

        viewModel.startMonitoring(vehicleRegNum, 1000L)
    }

    override fun onDestroy() {
        super.onDestroy()

        viewModel.stopMonitoring()
    }

}