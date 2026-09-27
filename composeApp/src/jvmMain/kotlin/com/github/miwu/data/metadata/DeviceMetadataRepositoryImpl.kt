package com.github.miwu.data.metadata

import com.github.miwu.domain.model.DeviceMetadata
import com.github.miwu.domain.repository.DeviceMetadataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import miwu.dispatchers.IoDispatcher
import miwu.miot.provider.MiotSpecAttrProvider
import org.koin.core.annotation.Singleton

@Singleton
class DeviceMetadataRepositoryImpl(
    private val specAttrProvider: MiotSpecAttrProvider,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : DeviceMetadataRepository {
    private val mutex = Mutex()
    private val mutableMetadata = MutableStateFlow(DeviceMetadata())
    override val metadata: StateFlow<DeviceMetadata> = mutableMetadata.asStateFlow()

    override suspend fun ensureIcons(models: Collection<String>) = withContext(ioDispatcher) {
        val missingModels = models.distinct().filterNot(mutableMetadata.value.icons::containsKey)
        if (missingModels.isEmpty()) return@withContext

        val icons = missingModels.mapNotNull { model ->
            specAttrProvider.getIconUrl(model)
                .getOrNull()
                ?.takeIf(String::isNotEmpty)
                ?.let { model to it }
        }
        if (icons.isEmpty()) return@withContext

        mutex.withLock {
            val current = mutableMetadata.value
            mutableMetadata.value = current.copy(icons = current.icons + icons)
        }
    }

    override suspend fun updateRooms(rooms: Map<String, String>) {
        if (rooms.isEmpty()) return
        mutex.withLock {
            val current = mutableMetadata.value
            mutableMetadata.value = current.copy(rooms = current.rooms + rooms)
        }
    }

    override suspend fun clear() {
        mutex.withLock { mutableMetadata.value = DeviceMetadata() }
    }
}
