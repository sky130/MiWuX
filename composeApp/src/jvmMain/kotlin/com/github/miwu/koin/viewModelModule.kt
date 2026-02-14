package com.github.miwu.koin

import com.github.miwu.logic.datastore.dataStoreModule
import com.github.miwu.logic.setting.AppSetting
import com.github.miwu.screen.device.DeviceViewModel
import com.github.miwu.screen.login.LoginViewModel
import com.github.miwu.screen.main.viewModel.MainViewModel
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import miwu.miot.common.MiotApiKoinModule
import miwu.miot.kmp.Client
import miwu.miot.kmp.Provider
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single<Job> { Job() }
    single { CoroutineScope(get<Job>()) }
}


val settingModule = module {
    single { AppSetting() }
}

val viewModelModule = module {
    viewModelOf(::DeviceViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::MainViewModel)
}