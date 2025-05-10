package com.example.speedguard.presentation

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.speedguard.ui.theme.SpeedGuardTheme
import com.example.speedguard.util.Logger.logd
import com.example.speedguard.util.permissions.LocationPermissionManager

class MainActivity : ComponentActivity() {
    companion object {
        private const val LOCATION_FOREGROUND_REQUEST_CODE = 1001
        private const val LOCATION_BACKGROUND_REQUEST_CODE = 1002
    }

    private lateinit var locationPermissionManager: LocationPermissionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpeedGuardTheme {

            }
        }



        locationPermissionManager = LocationPermissionManager(this)

        if (locationPermissionManager.hasForegroundLocationPermissions()) {
            if (locationPermissionManager.hasBackgroundLocationPermissions()) {
                logd("Have permission")
                startLocationService()
            } else {
                locationPermissionManager.requestBackgroundPermission(
                    LOCATION_BACKGROUND_REQUEST_CODE
                )
            }
        } else {
            locationPermissionManager.requestForegroundPermissions(LOCATION_FOREGROUND_REQUEST_CODE)
        }

        logd("MainActivity onCreate()")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)

        when (requestCode) {
            LOCATION_FOREGROUND_REQUEST_CODE -> {
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    // Foreground permissions granted, now request background if needed
                    if (locationPermissionManager.hasBackgroundLocationPermissions()) {
                        startLocationService()
                    } else {
                        locationPermissionManager.requestBackgroundPermission(
                            LOCATION_BACKGROUND_REQUEST_CODE
                        )
                    }
                } else if (locationPermissionManager.shouldShowRationale()) {
                    locationPermissionManager.openAppSettings()
                } else {
                    showPermissionDeniedDialog()
                }
            }

            LOCATION_BACKGROUND_REQUEST_CODE -> {
                if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
                    //startLocationMonitoring()
                    startLocationService()
                } else {
                    showPermissionDeniedDialog()
                }
            }
        }
    }

    private fun startLocationService() {
        logd("startLocationService")
        val intent = Intent(this, SpeedTrackingService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }


    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("Location permission is required to monitor speed. Please enable it in settings.")
            .setCancelable(false)
            .setPositiveButton("Go to Settings") { _, _ -> locationPermissionManager.openAppSettings() }
            .setNegativeButton("Exit App") { _, _ -> finish() }
            .show()
    }


}
