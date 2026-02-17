package miwu.compose.basic

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font

fun font(name: String) = "composeResources/miwu.common.resources/font/$name"

val harmonyOSFontFamily = FontFamily(
    Font(font("HarmonyOS_SansSC_Regular.ttf"), FontWeight.Normal),
    Font(font("HarmonyOS_SansSC_Thin.ttf"), FontWeight.Thin),
    Font(font("HarmonyOS_SansSC_Light.ttf"), FontWeight.Light),
    Font(font("HarmonyOS_SansSC_Medium.ttf"), FontWeight.Medium),
    Font(font("HarmonyOS_SansSC_Bold.ttf"), FontWeight.Bold),
    Font(font("HarmonyOS_SansSC_Black.ttf"), FontWeight.Black),
    Font(font("HarmonyOS_SansSC_Semibold.ttf"), FontWeight.SemiBold)
)

val miSansFontFamily =  FontFamily(
    Font(font("font/mi_sans_vf.ttf"))
)