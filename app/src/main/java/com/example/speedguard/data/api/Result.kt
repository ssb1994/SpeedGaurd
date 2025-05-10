package com.example.speedguard.data.api


/**
 * Defines the standardized result types for api fetch operations.
 * Used to standardize response across the domain layer.
 */
sealed class Result {
    /**
     * Represents a successful result with active rental data.
     * @param T the type of returned data.
     */
    data class Active<T>(val info: T) : Result()

    /**
     * Indicates that there is no active rental.
     */
    data object NoActiveRental : Result()

    /**
     * Represents an error that occurred during the operation.
     */
    data class Error(val e: Exception) : Result()
}