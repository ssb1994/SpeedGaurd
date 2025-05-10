package com.example.speedguard.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

/**
 * Observe the device's speed using the fused location provider and emits the speed in km/h.
 * Ensures proper permission and provider checks before starting observation.
 */
class SpeedObserver(private val context: Context) {
    private val client = LocationServices.getFusedLocationProviderClient(context)

    fun observeSpeed(intervalMillis: Long) = callbackFlow<Double> {
        val locationManager = context.getSystemService<LocationManager>() ?: kotlin.run {
            close()
            return@callbackFlow
        }

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
            !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        ) {
            Log.w("TAG", "No provider enabled")
            close()
            return@callbackFlow
        }

        val hasPermissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

        if (!hasPermissions) {
            Log.w("LocationObserver", "Permissions not granted")
            close()
            return@callbackFlow
        }

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            intervalMillis
        ).build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)

                val location = result.lastLocation ?: return
                val speedKmh = location.speed * 3.6 // m/s to km/h
                trySend(speedKmh)
            }
        }

        client.requestLocationUpdates(request, callback, context.mainLooper)
        awaitClose {
            client.removeLocationUpdates(callback)
        }
    }
}