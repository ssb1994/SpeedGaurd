package com.example.speedguard.util.location

import android.content.Context
import android.location.Location
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.runningReduce
import kotlinx.coroutines.flow.zip
import kotlin.math.roundToLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

fun Context.locationTracking(coroutineScope: CoroutineScope) {
    val locationObserver = LocationObserver(this)

    timeAndEmit(3f)
        .runningReduce { totalElapsedTime, newElapsedTime ->
            totalElapsedTime + newElapsedTime
        }
        .zip(
            locationObserver.observeLocation(1000L)
        ) { totalDuration, location ->
            totalDuration to location
        }.onEach { (totalDuration, location) ->
            println(
                "Location (${location.latitude}, ${location.longitude}) was tracked" +
                        "after ${totalDuration.inWholeMilliseconds} milliseconds."
            )
        }
        .runningFold(emptyList<Pair<Duration, Location>>()) { locations, newLocation ->
            locations + newLocation
        }
        .map { allLocations ->
            allLocations.zipWithNext { (duration1, location1), (duration2, location2) ->
                val distance = location1.distanceTo(location2)
                val durationDifference = (duration2 - duration1).toDouble(DurationUnit.HOURS)

                if (durationDifference > 0.0) {
                    ((distance / 1000.0) / durationDifference)
                } else 0.0
            }.average()
        }
        .onEach { avgSpeed ->
            println("Average speed is $avgSpeed km/h.")
        }
        .launchIn(coroutineScope)
}

/**
 * This function will emit the duration or time elapsed based on the required emissions per second,
 * the emission will start from 0 and will share the updated value each time
 */
private fun timeAndEmit(emissionsPerSecond: Float): Flow<Duration> {
    return flow {
        var lastEmitTime = System.currentTimeMillis()
        emit(Duration.ZERO)

        while (true) {
            delay((1000L / emissionsPerSecond).roundToLong())
            val currentTime = System.currentTimeMillis()
            val elapsedTime = currentTime - lastEmitTime

            emit(elapsedTime.milliseconds)
            lastEmitTime = currentTime
        }
    }
}