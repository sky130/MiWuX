package com.github.miwu.logic.repository

import com.github.miwu.logic.handler.DeviceMetadataHandler
import kotlinx.coroutines.flow.StateFlow

interface DeviceRepository {
    val deviceMetadataHandler: StateFlow<DeviceMetadataHandler>

    suspend fun addIcon(models: List<String>)

    suspend fun addRoom(input: List<Pair<String, String>>)

    fun getRoom(did: String): String

    fun getIcon(model: String): String?
}