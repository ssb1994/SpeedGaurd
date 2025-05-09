package com.example.speedguard.domain.usecaseImpl

import com.example.speedguard.data.Customer
import com.example.speedguard.data.Rental
import com.example.speedguard.domain.usecase.IFetchSpeedLimitApi
import kotlinx.coroutines.delay

class FetchSpeedLimitApiImpl : IFetchSpeedLimitApi {

    override suspend fun fetchSpeedLimit(vehicleId: String, carRegNumber: String): Rental {
        delay(1000L)//Simulate response delay

        val customer = Customer("124", "Customer-1")

        return Rental(
            customer,
            vehicleId,
            "model-12",
            70
        )
    }

}