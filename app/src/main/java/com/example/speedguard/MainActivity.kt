package com.example.speedguard

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.speedguard.ui.theme.SpeedGuardTheme
import com.example.speedguard.util.permissions.LocationPermissionManager
import com.example.speedguard.util.location.locationTracking
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                }
            }
        }

        locationPermissionManager = LocationPermissionManager(this)

        if (locationPermissionManager.hasForegroundLocationPermissions()) {
            if (locationPermissionManager.hasBackgroundLocationPermissions()) {

                locationTracking(CoroutineScope(Dispatchers.IO))
            } else {
                locationPermissionManager.requestBackgroundPermission(LOCATION_BACKGROUND_REQUEST_CODE)
            }
        } else {
            locationPermissionManager.requestForegroundPermissions(LOCATION_FOREGROUND_REQUEST_CODE)
        }

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
                        locationTracking(CoroutineScope(Dispatchers.IO))
                    }else{
                        locationPermissionManager.requestBackgroundPermission(
                            LOCATION_BACKGROUND_REQUEST_CODE)
                    }
                } else if (locationPermissionManager.shouldShowRationale()) {

                } else {
                    showPermissionDeniedDialog()
                }
            }

            LOCATION_BACKGROUND_REQUEST_CODE -> {
                if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
                    //startLocationMonitoring()
                    locationTracking(CoroutineScope(Dispatchers.IO))
                } else {
                    showPermissionDeniedDialog()
                }
            }
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
