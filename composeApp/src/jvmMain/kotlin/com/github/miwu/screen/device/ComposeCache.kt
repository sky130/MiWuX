package com.github.miwu.screen.device

import kndroidx.setting.getAppDataDir
import kotlinx.serialization.encodeToString
import miwu.miot.kmp.utils.json
import miwu.miot.kmp.utils.to
import miwu.miot.model.att.SpecAtt
import miwu.support.api.Cache
import java.io.File

class ComposeCache() : Cache {
    private val cacheDir: String = File(getAppDataDir("MiWuX"), "cache")
        .apply { if (!isDirectory) mkdir() }
        .absolutePath

    private fun specAtt(urn: String) = File("$cacheDir/${urn.hashCode()}.att")

    private fun langMap(urn: String) = File("$cacheDir/${urn.hashCode()}.map")

    override suspend fun getSpecAtt(urn: String): SpecAtt? = runCatching {
        specAtt(urn)
            .takeIf(File::isFile)
            ?.readText()
            ?.to<SpecAtt>()
            ?.getOrNull()
    }.getOrNull()

    override suspend fun putSpecAtt(urn: String, specAtt: SpecAtt) {
        runCatching {
            specAtt(urn).writeText(json.encodeToString(specAtt))
        }
    }

    override suspend fun getLanguageMap(urn: String): Map<String, String>? = runCatching {
        langMap(urn)
            .takeIf(File::isFile)
            ?.readText()
            ?.to<Map<String, String>>()
            ?.getOrNull()
    }.getOrNull()

    override suspend fun putLanguageMap(
        urn: String, map: Map<String, String>
    ) {
        runCatching {
            langMap(urn).writeText(json.encodeToString(map))
        }
    }
}
