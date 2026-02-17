package com.github.miwu.logic.repository.impl

import com.github.miwu.logic.repository.CacheRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import miwu.miot.provider.MiotSpecAttrProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CacheRepositoryImpl : KoinComponent, CacheRepository {
    private val specAttrProvider: MiotSpecAttrProvider by inject()
    private val iconMap = mutableMapOf<String, String>()
    private val iconMutex = Mutex()
    override val icons = MutableStateFlow<Map<String, String>>(emptyMap())

    override suspend fun addIcon(models: List<String>): Unit = withContext(Dispatchers.IO) {
        models
            .mapNotNull { model ->
                if (model in iconMap) null
                else specAttrProvider.getIconUrl(model)
                    .getOrNull()
                    ?.takeIf(String::isNotEmpty)
                    ?.let { model to it }
            }
            .takeIf(List<*>::isNotEmpty)
            ?.also {
                iconMutex.withLock {
                    iconMap.putAll(it)
                    icons.emit(iconMap)
                }
            }
        update()
    }


    override fun getIcon(model: String) = iconMap[model]

    private suspend fun update() {
        icons.emit(iconMap.toMap())
    }
}