package com.example.speedguard.domain.usecaseImpl

import android.util.Log
import com.example.speedguard.data.Rental
import com.example.speedguard.domain.usecase.SpeedMonitoringUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class SpeedMonitoringUseCaseImpl: SpeedMonitoringUseCase {
    private val tag = SpeedMonitoringUseCaseImpl::class.java.simpleName
    private var activeRental: Rental? = null

    override fun registerRental(rental: Rental) {
        activeRental = rental
    }

    override fun observeSpeed(vehicleId: String): Flow<Int> {
        if (activeRental == null) {
            Log.i(tag, "No rental details found hence returning empty flow")
            return emptyFlow()
        }

    }
}