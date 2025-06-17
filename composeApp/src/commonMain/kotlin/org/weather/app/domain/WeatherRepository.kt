package org.weather.app.domain

import org.weather.app.data.WeatherApi
import org.weather.app.data.model.WeatherResponse

class WeatherRepository(private val api: WeatherApi= WeatherApi()) {
    private var cachedWeather: WeatherResponse? = null

    suspend fun getWeather(lat: Double, lon: Double): WeatherResponse {
        return try {
            val response = api.fetchWeather(lat, lon)
            cachedWeather = response
            response
        } catch (e: Exception) {
            cachedWeather ?: throw e
        }
    }
}