package com.github.miwu.domain.repository

import com.github.miwu.domain.model.HomeSelection
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val selectedHomeId: Flow<Long>

    suspend fun getHomeSelection(): HomeSelection?

    suspend fun selectHome(homeId: Long, ownerId: Long)

    suspend fun clearHomeSelection()

    suspend fun getOrCreateDeviceId(): String
}
