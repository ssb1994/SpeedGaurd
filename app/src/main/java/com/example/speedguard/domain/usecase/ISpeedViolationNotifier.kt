package com.example.speedguard.domain.usecase

/**
 * Interface to notify speed violation
 */
interface ISpeedViolationNotifier {
    /**
     * Notifies the rental company about speed violation.
     * @param vehicleRegNum Unique identifier for the vehicle.
     * @param currentSpeed The speed at which the vehicle is running.
     */
    fun notifyCompany(vehicleRegNum: String, currentSpeed: Double)

    /**
     * Notifies the driver about the speed violation.
     * @param vehicleRegNum Unique identifier for the vehicle.
     * @param currentSpeed The speed at which the vehicle is running.
     */
    fun alertUser(vehicleRegNum: String, currentSpeed: Double)
}