package org.weather.app

import android.app.Application
import android.content.Context

lateinit var APPLICATION_CONTEXT: Context

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        APPLICATION_CONTEXT = applicationContext
    }
}