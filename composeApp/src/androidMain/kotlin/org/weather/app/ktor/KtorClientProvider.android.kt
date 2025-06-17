package org.weather.app.ktor

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp

actual fun getHttpClientEngineFactory(): HttpClientEngineFactory<HttpClientEngineConfig> = OkHttp