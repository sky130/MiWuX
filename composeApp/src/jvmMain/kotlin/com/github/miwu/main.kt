package com.github.miwu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import com.kdroid.composetray.tray.api.ExperimentalTrayAppApi
import com.kdroid.composetray.tray.api.TrayApp
import com.kdroid.composetray.tray.api.TrayAppState
import miwu.common.resources.Res
import miwu.common.resources.ic_miwu_round
import miwu.common.resources.ic_miwu_tray
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.plugin.module.dsl.modules
import org.koin.plugin.module.dsl.startKoin

@OptIn(ExperimentalTrayAppApi::class)
fun main() {
    startKoin<KoinApp>()
    application {
        val state = remember { TrayAppState(DpSize(width = 300.dp, height = 600.dp)) }
        TrayApp(
            windowsIcon = painterResource(Res.drawable.ic_miwu_round),
            macLinuxIcon = vectorResource(Res.drawable.ic_miwu_tray),
            tooltip = "MiWuX",
            state = state,
        ) {
            Box(Modifier.clip(RoundedCornerShape(15.dp))) {
                App()
            }
        }
    }
}
