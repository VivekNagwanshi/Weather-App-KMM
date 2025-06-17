package org.weather.app.ktor

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

const val BASE_URL = "https://api.open-meteo.com/v1/forecast"

expect fun getHttpClientEngineFactory(): HttpClientEngineFactory<HttpClientEngineConfig>
//expect fun getHttpClientEngine(): HttpClientEngine
object KtorClientProvider {

    fun getSalesforceClient(
        httpClientEngineFactory: HttpClientEngineFactory<HttpClientEngineConfig> = getHttpClientEngineFactory(),
//        localRepository: LocalRepository
    ): HttpClient {
        return HttpClient(httpClientEngineFactory) {
            defaultRequest {
                url(BASE_URL)
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        encodeDefaults = true
                        prettyPrint = true
                        ignoreUnknownKeys = true
                        isLenient = true
                    }
                )
            }
            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.ALL
            }
        }
    }
}