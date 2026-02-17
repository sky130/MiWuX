package com.github.miwu.logic.repository

import kotlinx.coroutines.flow.StateFlow
import miwu.miot.model.miot.MiotDevice

interface CacheRepository {
    val icons: StateFlow<Map<String, String>>

    suspend fun addIcon(models: List<String>)

    fun getIcon(model: String): String?
}