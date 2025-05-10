package com.example.speedguard.data.api

import com.example.speedguard.data.model.Customer
import com.example.speedguard.data.model.RentalInfo
import com.example.speedguard.util.Logger.logd
import com.example.speedguard.util.Logger.loge
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * A fake implementation of [IFetchRentalInfoApi] that simulates backend call to fetch rental info from remote source.
 * This implementation includes delay and randomized success/failure response.
 */
class FetchRentalInfoApiImpl : IFetchRentalInfoApi {

    /**
     * Simulates an asynchronous API call to fetch rental information.
     * Randomly returns a successful rental info or no active rental info
     * @param vehicleRegNumber the unique identifier for each vehicle
     * @return Result object indicating the outcome.
     */
    override suspend fun fetchRentalInfo(vehicleRegNumber: String): Result {
        return try {
            delay(1000L)//Simulate response delay
            val success = Random.nextBoolean()
            logd("Api fetch status = $success")

            val customer = Customer("124", "Customer-1")

            if (success) {
                Result.Active(
                    RentalInfo(
                        customer,
                        vehicleRegNumber,
                        80.0
                    )
                )
            } else {
                Result.NoActiveRental
            }
        } catch (e: Exception) {
            loge("Error fetching rental info: ", e)
            Result.Error(e)
        }
    }

}