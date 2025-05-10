package com.example.speedguard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speedguard.data.api.IFetchRentalInfoApi
import com.example.speedguard.data.api.Result
import com.example.speedguard.data.model.RentalInfo
import com.example.speedguard.domain.usecase.IMonitorVehicleSpeedUseCase
import com.example.speedguard.domain.usecase.ISpeedViolationNotifier
import com.example.speedguard.util.Logger.logd
import com.example.speedguard.util.Logger.loge
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * ViewModel for managing speed monitoring logic and exposing vehicle state.
 * Manages API calls, speed tracking and violation notifications.
 */
class SpeedMonitorViewModel(
    private val fetchRentalInfoApi: IFetchRentalInfoApi,
    private val monitorVehicleSpeedUseCase: IMonitorVehicleSpeedUseCase,
    private val notifier: ISpeedViolationNotifier
) : ViewModel() {

    private var monitorJob: Job? = null

    /**
     * Starts monitoring of the vehicle speed.
     * - It first fetches the rental info.
     * - Then begins the speed tracking if a rental is active.
     */
    fun startMonitoring(vehicleRegNum: String, intervalMillis: Long) {
        viewModelScope.launch {
            when (val result = fetchRentalInfoApi.fetchRentalInfo(vehicleRegNum)) {
                is Result.Active<*> -> {
                    val rentalInfo = result.info as RentalInfo
                    logd("Active rental info received, vehicleRegNum = ${rentalInfo.vehicleRegNum}, speedLimit=${rentalInfo.speedLimit}")

                    monitorJob = monitorVehicleSpeedUseCase.execute(
                        intervalMillis, rentalInfo.speedLimit
                    ).onEach {(speed, isOverSpeeding) ->
                        logd("Speed = $speed, isOverLimit = $isOverSpeeding")

                        if (isOverSpeeding) {
                            notifier.notifyCompany(rentalInfo.vehicleRegNum, speed)
                            notifier.alertUser(rentalInfo.vehicleRegNum, speed)
                        }
                    }.launchIn(this)
                }

                is Result.NoActiveRental -> {
                    logd("No active rental info received, no monitoring")
                }

                is Result.Error -> {
                    loge("Api fetch error", result.e)
                }
            }
        }
    }


    /**
     * Stops any ongoing monitoring job and resets state.
     */
    fun stopMonitoring() {
        monitorJob?.cancel()
    }

}