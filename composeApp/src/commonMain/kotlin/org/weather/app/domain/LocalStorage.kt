package org.weather.app.domain

import kotlin.reflect.KClass

interface LocalStorage {
    fun <T : Any> save(key: String, value: T, clazz: KClass<T>)
    fun <T : Any> get(key: String, clazz: KClass<T>): T?
    fun clearEverything()
}

expect fun getLocalStorage(): LocalStorage