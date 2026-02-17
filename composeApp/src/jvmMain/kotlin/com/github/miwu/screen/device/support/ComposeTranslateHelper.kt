package com.github.miwu.screen.device.support

import miwu.common.resources.*
import miwu.common.resources.Res
import miwu.support.translate.TranslateHelper
import org.jetbrains.compose.resources.getString

object ComposeTranslateHelper : TranslateHelper {
    private val languageMap = mutableMapOf<String, String>()

    suspend fun init() {
        languageMap += mapOf(
            "Auto" to getString(Res.string.Auto),
            "Cool" to getString(Res.string.Cool),
            "Dry" to getString(Res.string.Dry),
            "Heat" to getString(Res.string.Heat),
            "Fan" to getString(Res.string.Fan),
            "Normal" to getString(Res.string.Normal),
            "Low" to getString(Res.string.LowFood),
            "Empty" to getString(Res.string.EmptyFood),
            "Temperature" to getString(Res.string.Temperature),
            "Relative Humidity" to getString(Res.string.relative_humidity),
            "Battery" to getString(Res.string.battery),
            "Pet Food Out" to getString(Res.string.pet_food_out),
            "fan" to getString(Res.string.fan2),
        )
    }

    override fun translate(origin: String): String = languageMap[origin] ?: origin
}