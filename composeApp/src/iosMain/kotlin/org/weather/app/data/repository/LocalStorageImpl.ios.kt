package org.weather.app.data.repository

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.weather.app.domain.LocalStorage
import platform.Foundation.NSUserDefaults
import kotlin.reflect.KClass

class LocalStorageImpl: LocalStorage {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    @OptIn(InternalSerializationApi::class)
    override fun <T : Any> save(key: String, value: T, clazz: KClass<T>) {
        val jsonString = Json.encodeToString(clazz.serializer(), value)
        userDefaults.setObject(jsonString, key)
    }

    @OptIn(InternalSerializationApi::class)
    override fun <T : Any> get(key: String, clazz: KClass<T>): T? {
        val jsonString = userDefaults.objectForKey(key) as String?
        return jsonString?.let {
            Json.decodeFromString(clazz.serializer(), jsonString)
        }
    }

    override fun clearEverything() {
        userDefaults.dictionaryRepresentation().keys.forEach { key ->
            (key as? String)?.let {
                userDefaults.removeObjectForKey(it)
            }
        }
    }
}

