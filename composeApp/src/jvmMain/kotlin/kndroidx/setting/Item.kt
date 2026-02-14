package kndroidx.setting

import kotlinx.coroutines.flow.Flow

interface Item<T> {
    val flow: Flow<T>
    suspend fun getValue(): T
    suspend fun setValue(value: T)
}