package com.github.miwu

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.github.miwu.koin.appModule
import com.github.miwu.koin.settingModule
import com.github.miwu.koin.viewModelModule
import com.github.miwu.logic.MiotLoginProviderImpl
import com.github.miwu.logic.datastore.dataStoreModule
import com.github.miwu.logic.repository.repositoryModule
import miwu.miot.common.MiotApiKoinModule
import miwu.miot.kmp.Client
import miwu.miot.kmp.impl.provider.MiotSpecAttrProviderImpl
import miwu.miot.provider.MiotLoginProvider
import miwu.miot.provider.MiotSpecAttrProvider
import org.koin.compose.KoinApplication
import org.koin.dsl.module

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
                single<MiotSpecAttrProvider>{
                    MiotSpecAttrProviderImpl()
                }
            }
            // TODO MiotApiKoinModule.KMP.Provider,
        )
    }) {
        Window(
            onCloseRequest = ::exitApplication,
            title = "MiWuX",
        ) {
            App()
        }
    }
}