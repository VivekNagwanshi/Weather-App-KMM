package org.weather.app.presentation

import kotlinx.coroutines.CoroutineExceptionHandler
import org.weather.app.data.model.WeatherResponse
import org.weather.app.domain.WeatherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.weather.app.LocationManager
import org.weather.app.LocationPermissionStatus
import org.weather.app.domain.LocalStorage
import org.weather.app.domain.getLocalStorage
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class WeatherViewModel(private val repository: WeatherRepository = WeatherRepository(),
    private val localRepository: LocalStorage = getLocalStorage()) {
    private val _weatherState = MutableStateFlow<Result<WeatherResponse>?>(null)
    val weatherState: StateFlow<Result<WeatherResponse>?> = _weatherState.asStateFlow()
    private val viewModelScope = CoroutineScope(Dispatchers.Default)
    private lateinit var fetchCurrentLocation: Job
    private val locationManager = LocationManager()
    var currentLocation = Pair(0.0, 0.0)
    var cityName = ""

    fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val result = repository.getWeather(lat, lon)
                localRepository.save("weather", result, WeatherResponse::class)
                _weatherState.value = Result.success(result)
            } catch (e: Exception) {
                _weatherState.value = Result.failure(e)
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    fun fetchWeatherIfNeeded(lat: Double, lon: Double) {
        viewModelScope.launch {
            val lastWeather: WeatherResponse? = localRepository.get("weather", WeatherResponse::class)

            val shouldFetch = lastWeather?.current_weather?.time?.let { lastTimeString ->
                val lastTime = Instant.parse("${lastTimeString}:00Z") // Make sure it's a valid ISO string
                val now = Clock.System.now()
                val durationSinceLastFetch = now - lastTime
                durationSinceLastFetch >= 1.hours
            } ?: true
            val distance = getFormattedDistance(lat, lon, lastWeather?.latitude?:0.0, lastWeather?.longitude?:0.0)
            println("Last saved $lastWeather")
            println("Distance $distance")
            val isLocationChange = false
//                if (distance >=1000 ){
//                true
//            }else{false}

            if (shouldFetch) {
                try {
                    val result = repository.getWeather(lat, lon)
                    localRepository.save("weather", result, WeatherResponse::class)
                    _weatherState.value = Result.success(result)
                } catch (e: Exception) {
                    _weatherState.value = Result.failure(e)
                }
            } else {
                // Optional: Load cached result
                _weatherState.value = Result.success(lastWeather)
            }
        }
    }

    init {
        getCurrentLocation()
    }

    fun getCurrentLocation() {
        fetchCurrentLocation = viewModelScope.launch(
            start = CoroutineStart.LAZY,
            context = CoroutineExceptionHandler { _, _ -> }
        ) {
            val locationAsync = async {
                val locationPermissionStatus = locationManager.requestLocationPermission()
                if (locationPermissionStatus == LocationPermissionStatus.ACCEPTED)
                    currentLocation = locationManager.requestCurrentLocation()
                cityName = locationManager.getCurrentCityName()
                println("current location $currentLocation $cityName")
            }
            locationAsync.await()
        }
        fetchCurrentLocation.start()
    }

    fun toRadians(degrees: Double): Double = degrees * (PI / 180)

    fun getFormattedDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): String {
        val earthRadiusKm = 6371.0

        val dLat = toRadians(lat2 - lat1)
        val dLon = toRadians(lng2 - lng1)

        val a = sin(dLat / 2).pow(2) +
                cos(toRadians(lat1)) * cos(toRadians(lat2)) *
                sin(dLon / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        val distanceInKm = earthRadiusKm * c

        return if (distanceInKm < 1.0) {
            val distanceInMeters = (distanceInKm * 1000).toInt()
            "$distanceInMeters m"
        } else {
            val kmRounded = (distanceInKm * 10).roundToInt() / 10.0
            "$kmRounded km"
        }
    }

}