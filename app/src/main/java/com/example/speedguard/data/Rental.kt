package com.example.speedguard.data

data class Rental (
    val customer: Customer,
    val vehicleId: String,
    val vehicleModel: String,
    val speedLimit: Double
)