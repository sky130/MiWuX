package com.github.miwu.koin

import com.github.miwu.logic.setting.AppSetting
import com.github.miwu.screen.login.LoginViewModel
import com.github.miwu.AppViewModel
import com.github.miwu.screen.main.MainViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val settingModule = module {
    single { AppSetting() }
}

val viewModelModule = module {
    single { AppViewModel() }
    viewModel { LoginViewModel() }
    viewModel { MainViewModel() }
}