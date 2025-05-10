package com.example.speedguard.data.api

/**
 * Interface for fetching rental information based on vehicle's registration number.
 */
interface IFetchRentalInfoApi {
    /**
     * Fetch rental information for a given vehicle registration number.
     * @param vehicleRegNumber the registration number of the vehicle.
     * @return Result indicating success, no rental, or error.
     */
    suspend fun fetchRentalInfo(vehicleRegNumber: String): Result
}