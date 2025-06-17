package org.weather.app.domain

import org.weather.app.data.repository.LocalStorageImpl

actual fun getLocalStorage(): LocalStorage {
    return LocalStorageImpl()
}