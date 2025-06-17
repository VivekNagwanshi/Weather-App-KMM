package org.weather.app

enum class LocationPermissionStatus {
    RESTRICTED_OR_DENIED,
    NOT_DETERMINED,
    ACCEPTED
}

expect class LocationManager() {
    suspend fun requestLocationPermission(): LocationPermissionStatus
    suspend fun requestCurrentLocation(): Pair<Double, Double>
    suspend fun getCurrentCityName(): String
}