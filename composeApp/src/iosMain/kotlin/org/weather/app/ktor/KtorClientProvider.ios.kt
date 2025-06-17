package org.weather.app.ktor

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin

actual fun getHttpClientEngineFactory(): HttpClientEngineFactory<HttpClientEngineConfig> = Darwin