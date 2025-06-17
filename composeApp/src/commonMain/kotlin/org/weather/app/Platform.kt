package org.weather.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform