package com.github.miwu.screen.device

import kndroidx.setting.getAppDataDir
import miwu.miot.model.att.SpecAtt
import miwu.miot.utils.gson
import miwu.support.api.Cache
import java.io.File
import miwu.miot.utils.to

class ComposeCache() : Cache {
    private val cacheDir: String = File(getAppDataDir("MiWuX"), "cache").apply {
        if (!isDirectory) mkdir()
    }.absolutePath

    override suspend fun getSpecAtt(urn: String): SpecAtt? {
        try {
            val file = File("$cacheDir/${urn.hashCode()}.att")
            return if (!file.isFile) {
                return null
            } else {
                file.readText().to<SpecAtt>()
            }
        } catch (e: Exception) {
            return null
        }
    }

    override suspend fun putSpecAtt(urn: String, specAtt: SpecAtt) {
        val file = File("$cacheDir/${urn.hashCode()}.att")
        file.writeText(gson.toJson(specAtt))
    }

    override suspend fun getLanguageMap(urn: String): Map<String, String>? {
        try {
            val file = File("$cacheDir/${urn.hashCode()}.map")
            return if (!file.isFile) {
                return null
            } else {
                file.readText().to<Map<String, String>>()
            }
        } catch (e: Exception) {
            return null
        }
    }

    override suspend fun putLanguageMap(
        urn: String, map: Map<String, String>
    ) {
        val file = File("$cacheDir/${urn.hashCode()}.map")
        file.writeText(gson.toJson(map))
    }
}
