package org.weather.app.data.repository

import android.content.Context
import androidx.core.content.edit
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.weather.app.domain.LocalStorage
import kotlin.reflect.KClass

class LocalStorageImpl(context: Context): LocalStorage {
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    @OptIn(InternalSerializationApi::class)
    override fun <T : Any> save(key: String, value: T, clazz: KClass<T>) {
        val jsonString = Json.encodeToString(clazz.serializer(), value)
        sharedPreferences.edit { putString(key, jsonString) }
    }

    @OptIn(InternalSerializationApi::class)
    override fun <T : Any> get(key: String, clazz: KClass<T>): T? {
        val jsonString: String? = sharedPreferences.getString(key, null)
        return jsonString?.let {
            Json.decodeFromString(clazz.serializer(), jsonString)
        }
    }

    override fun clearEverything() {
        sharedPreferences.edit { clear() }
    }
}