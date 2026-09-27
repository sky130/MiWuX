package com.github.miwu.data.home

import com.github.miwu.domain.model.HomeData
import com.github.miwu.domain.repository.DeviceMetadataRepository
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import miwu.dispatchers.IoDispatcher
import miwu.miot.client.MiotHomeClient
import miwu.miot.model.miot.MiotDevice
import miwu.miot.model.miot.MiotHome
import miwu.miot.model.miot.MiotRoom
import miwu.miot.model.miot.MiotScene
import org.koin.core.annotation.Singleton

@Singleton
class HomeDataLoader(
    private val metadataRepository: DeviceMetadataRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    private val sceneHomes = ConcurrentHashMap<MiotScene, MiotHome>()

    suspend fun load(client: MiotHomeClient, homes: List<MiotHome>): Map<String, HomeData> =
        withContext(ioDispatcher) {
            sceneHomes.clear()
            coroutineScope {
                homes.map { home -> async { loadHome(client, home).let { home.id to it } } }
                    .awaitAll()
                    .toMap()
            }
        }

    suspend fun load(client: MiotHomeClient, home: MiotHome): HomeData =
        withContext(ioDispatcher) { loadHome(client, home) }

    fun getSceneHome(scene: MiotScene): MiotHome? = sceneHomes[scene]

    fun clear() {
        sceneHomes.clear()
    }

    private suspend fun loadHome(client: MiotHomeClient, home: MiotHome): HomeData {
        val devices = client.getDevices(home)
            .rethrowCancellation()
            .getOrThrow()
            .result
            .deviceInfo
            .orEmpty()
        val deviceById = devices.associateBy(MiotDevice::did)
        val roomByDevice = devices.associate { device ->
            device.did to (home.rooms.firstOrNull { device.did in it.dids }?.name ?: UNKNOWN_ROOM)
        }

        metadataRepository.updateRooms(roomByDevice)
        metadataRepository.ensureIcons(devices.map(MiotDevice::model))

        val scenes = client.getScenes(home)
            .rethrowCancellation()
            .getOrThrow()
            .result
            .scenes
            .orEmpty()
            .sortedBy(MiotScene::name)
        sceneHomes.putAll(scenes.associateWith { home })

        return HomeData(
            home = home,
            rooms = home.rooms.associateBy(MiotRoom::name) { room ->
                room.dids.mapNotNull(deviceById::get).sortedBy(MiotDevice::name)
            },
            scenes = scenes,
            devices = devices.sortedBy(MiotDevice::name),
            roomMap = home.rooms.associateBy(MiotRoom::name),
        )
    }

    private fun <T> Result<T>.rethrowCancellation(): Result<T> =
        onFailure { if (it is CancellationException) throw it }

    private companion object {
        const val UNKNOWN_ROOM = "未知"
    }
}
