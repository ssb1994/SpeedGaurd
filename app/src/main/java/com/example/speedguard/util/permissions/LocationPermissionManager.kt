package com.example.speedguard.util.permissions

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat

/**
 * This class handles the location permission related functionalities
 */
class LocationPermissionManager(
    private val activity: Activity
) {

    /**
     * The method will check whether the application have coarse and fine location permissions.
     * @return TRUE/FALSE
     */
    fun hasForegroundLocationPermissions(): Boolean {
        val hasFineLocationPermission = ActivityCompat.checkSelfPermission(
            activity, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarseLocationPermission = ActivityCompat.checkSelfPermission(
            activity, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return (hasCoarseLocationPermission && hasFineLocationPermission)
    }

    /**
     * The method will check whether the application have background location permissions.
     * @return TRUE/FALSE
     */
    fun hasBackgroundLocationPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    /**
     * The helper method support for requesting foreground location permission
     */
    fun requestForegroundPermissions(requestCode: Int) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            requestCode
        )
    }

    /**
     * The helper method support for requesting background location permission
     */
    fun requestBackgroundPermission(requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                requestCode
            )
        }
    }

    /**
     * The helper method support in opening the application settings for the requested permissions
     */
    fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", activity.packageName, null)
        activity.startActivity(intent)
    }

    /**
     * This method returns true only if the user previously denied the permission
     * without selecting "Don't ask again." (Second time onwards)
     */
    fun shouldShowRationale(): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

}