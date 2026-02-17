package com.github.miwu.logic.handler

data class DeviceMetadataHandler(
    val iconMap: Map<String, String>,
    val roomMap: Map<String, String>
) {
    fun getRoom(did: String): String = roomMap[did] ?: "未知位置" // TODO i18n
    fun getIcon(model: String): String? = iconMap[model]
}
