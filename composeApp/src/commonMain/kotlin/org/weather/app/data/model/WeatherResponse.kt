package org.weather.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("longitude")
    val longitude: Double,
    @SerialName("current_weather")
    val current_weather: CurrentWeather
)

@Serializable
data class CurrentWeather(
    @SerialName("temperature")
    val temperature: Double,
    @SerialName("windspeed")
    val windspeed: Double,
    @SerialName("weathercode")
    val weathercode: Int,
    @SerialName("time")
    val time: String,
)