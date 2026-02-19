package com.github.miwu.screen.device.support

import miwu.common.resources.*
import miwu.common.resources.Res
import miwu.support.translate.TranslateHelper
import org.jetbrains.compose.resources.getString

object ComposeTranslateHelper : TranslateHelper {
    private val languageMap = mutableMapOf<String, String>()

    suspend fun init() {
        languageMap += mapOf(
            "Auto" to Res.string.Auto,
            "Cool" to Res.string.Cool,
            "Dry" to Res.string.Dry,
            "Heat" to Res.string.Heat,
            "Fan" to Res.string.Fan,
            "Normal" to Res.string.Normal,
            "Low" to Res.string.LowFood,
            "Empty" to Res.string.EmptyFood,
            "Temperature" to Res.string.Temperature,
            "Relative Humidity" to Res.string.relative_humidity,
            "Battery" to Res.string.battery,
            "Pet Food Out" to Res.string.pet_food_out,
            "fan" to Res.string.fan2,
            "Brightness" to Res.string.Brightness
        ).mapValues { (_, res) -> getString(res) }
    }

    override fun translate(origin: String): String = languageMap[origin] ?: origin
}