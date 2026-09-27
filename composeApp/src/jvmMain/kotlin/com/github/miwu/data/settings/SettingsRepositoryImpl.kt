package com.github.miwu.data.settings

import com.github.miwu.domain.model.HomeSelection
import com.github.miwu.domain.repository.SettingsRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import miwu.miot.common.getRandomDeviceId
import org.koin.core.annotation.Singleton

@Singleton
class SettingsRepositoryImpl(
    private val settings: AppSettings,
) : SettingsRepository {
    private val deviceIdMutex = Mutex()

    override val selectedHomeId = settings.homeId.flow

    override suspend fun getHomeSelection(): HomeSelection? {
        val homeId = settings.homeId.getValue()
        val ownerId = settings.ownerId.getValue()
        return if (homeId == 0L || ownerId == 0L) null else HomeSelection(homeId, ownerId)
    }

    override suspend fun selectHome(homeId: Long, ownerId: Long) {
        settings.homeId.setValue(homeId)
        settings.ownerId.setValue(ownerId)
    }

    override suspend fun clearHomeSelection() {
        settings.homeId.setValue(0L)
        settings.ownerId.setValue(0L)
    }

    override suspend fun getOrCreateDeviceId(): String = deviceIdMutex.withLock {
        settings.deviceId.getValue().takeIf(String::isNotBlank) ?: getRandomDeviceId().also {
            settings.deviceId.setValue(it)
        }
    }
}
