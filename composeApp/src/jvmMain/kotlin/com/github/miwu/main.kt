package com.github.miwu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.miwu.koin.appModule
import com.github.miwu.koin.settingModule
import com.github.miwu.koin.viewModelModule
import com.github.miwu.logic.MiotLoginProviderImpl
import com.github.miwu.logic.datastore.dataStoreModule
import com.github.miwu.logic.repository.repositoryModule
import com.kdroid.composetray.tray.api.ExperimentalTrayAppApi
import com.kdroid.composetray.tray.api.Tray
import com.kdroid.composetray.tray.api.TrayApp
import com.kdroid.composetray.tray.api.TrayAppState
import com.kdroid.composetray.utils.getTrayWindowPosition
import miwu.common.resources.Res
import miwu.common.resources.ic_miwu_placeholder
import miwu.common.resources.ic_miwu_round
import miwu.common.resources.ic_miwu_tray
import miwu.common.resources.ic_miwu_tray_mac
import miwu.common.resources.ic_miwu_with_bg
import miwu.miot.common.MiotApiKoinModule
import miwu.miot.kmp.Client
import miwu.miot.kmp.impl.provider.MiotSpecAttrProviderImpl
import miwu.miot.provider.MiotLoginProvider
import miwu.miot.provider.MiotSpecAttrProvider
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.KoinApplication
import org.koin.dsl.module

@OptIn(ExperimentalTrayAppApi::class)
fun main() = application {
    KoinApplication(application = {
        modules(
            appModule,
            settingModule,
            viewModelModule,
            dataStoreModule,
            repositoryModule,
            MiotApiKoinModule.KMP.Client,
            module {
                single<MiotLoginProvider> {
                    MiotLoginProviderImpl()
                }
                single<MiotSpecAttrProvider> {
                    MiotSpecAttrProviderImpl()
                }
            }
            // TODO MiotApiKoinModule.KMP.Provider,
        )
    }) {
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