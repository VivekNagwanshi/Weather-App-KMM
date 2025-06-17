package org.weather.app

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual class LocationManager {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(APPLICATION_CONTEXT)

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    actual suspend fun requestLocationPermission(): LocationPermissionStatus {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        return suspendCancellableCoroutine { continuation ->
            if (ActivityCompat.checkSelfPermission(
                    APPLICATION_CONTEXT,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                continuation.resume(LocationPermissionStatus.ACCEPTED)
            } else {
                ActivityCompat.requestPermissions(
                    ACTIVITY,
                    arrayOf(permission),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
                CoroutineScope(Dispatchers.IO).launch {
                    // todo: remove this delay for checking the permission status after 5 seconds
                    delay(5000)
                    ACTIVITY.runOnUiThread {
                        if (ActivityCompat.checkSelfPermission(
                                APPLICATION_CONTEXT,
                                permission
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            continuation.resume(LocationPermissionStatus.ACCEPTED)
                        } else {
                            continuation.resume(LocationPermissionStatus.RESTRICTED_OR_DENIED)
                        }
                    }
                }
            }
        }
    }

    @Suppress("MissingPermission")
    actual suspend fun requestCurrentLocation(): Pair<Double, Double> {
        val location: Location? = fusedLocationClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .await()
        return if (location != null) {
            location.latitude to location.longitude
        } else {
            0.0 to 0.0
        }
    }

    @SuppressLint("MissingPermission")
    actual suspend fun getCurrentCityName(): String{
        return suspendCancellableCoroutine { continuation ->
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    try {
                        val geocoder = Geocoder(APPLICATION_CONTEXT, Locale.getDefault())
                        val addresses =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        if (addresses != null && addresses.isNotEmpty()) {
                            val city = addresses[0].locality
                            continuation.resume(city)
                        } else {
                            continuation.resume("")
                        }
                    } catch (e: Exception) {
                        continuation.resumeWithException(e)
                    }
                } else {
                    continuation.resume("")
                }
            }.addOnFailureListener { e ->
                continuation.resumeWithException(e)
            }
        }

    }
}