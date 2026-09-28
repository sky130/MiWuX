package com.github.miwu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.koin.KermitKoinLogger
import com.github.miwu.logging.Logger
import com.github.miwu.logging.Logging
import com.github.miwu.tray.DecoratedTrayApp
import dev.nucleusframework.application.NucleusBackend
import dev.nucleusframework.application.nucleusApplication
import dev.nucleusframework.composenativetray.trayapp.rememberTrayAppState
import miwu.common.resources.Res
import miwu.common.resources.ic_miwu_round
import miwu.common.resources.ic_miwu_tray
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.KoinApplication
import org.koin.dsl.includes
import org.koin.dsl.koinConfiguration as configureKoin
import org.koin.plugin.module.dsl.koinConfiguration as generatedKoinConfiguration

private val logger = Logger<KoinApp>()

fun main() {
    Logging.initialize()
    logger.info("Starting application")
    nucleusApplication(
        backend = NucleusBackend.Tao,
        dockIconFollowsWindows = true,
    ) {
        KoinApplication(
            configuration = configureKoin {
                includes(generatedKoinConfiguration<KoinApp>())
                logger(KermitKoinLogger(Logging.kermitLogger("Koin")))
            },
        ) {
            val state = rememberTrayAppState(
                initialWindowSize = DpSize(300.dp, 600.dp),
            )
            DecoratedTrayApp(
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
}
