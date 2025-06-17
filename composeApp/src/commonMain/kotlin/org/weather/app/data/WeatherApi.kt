package org.weather.app.data

import org.weather.app.data.model.WeatherResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.weather.app.ktor.KtorClientProvider

class WeatherApi(private val client: HttpClient = KtorClientProvider.getSalesforceClient()) {
    suspend fun fetchWeather(lat: Double, lon: Double): WeatherResponse {
        val response: HttpResponse = client.get("https://api.open-meteo.com/v1/forecast") {
            parameter("latitude", lat)
            parameter("longitude", lon)
            parameter("current_weather", true)
        }
        return response.body()
    }
}