package com.example.speedguard.domain.usecaseImpl

import com.example.speedguard.domain.usecase.ISpeedViolationNotifier
import com.example.speedguard.util.Logger.logd


/**
 * A fake implementation of [ISpeedViolationNotifier] to simulate Firebase backend call on speed violation.
 */
class FirebaseSpeedNotificationService : ISpeedViolationNotifier {

    /**
     * Notifies the rental company about the speed violation.
     * @param vehicleRegNum Unique identifier of the vehicle
     * @param currentSpeed The speed at which the vehicle is running.
     */
    override fun notifyCompany(vehicleRegNum: String, currentSpeed: Double) {
        logd("Firebase: notifyCompany api call: vehicleRegNum=$vehicleRegNum, currentSpeed = $currentSpeed")
    }

    /**
     * Notifies the driver about the speed violation.
     * @param vehicleRegNum Unique identifier of the vehicle
     * @param currentSpeed The speed at which the vehicle is running.
     */
    override fun alertUser(vehicleRegNum: String, currentSpeed: Double) {
        logd("Firebase: alertUser api call: vehicleRegNum=$vehicleRegNum, currentSpeed = $currentSpeed")
    }
}