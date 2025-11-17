package com.github.miwu.screen.main

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.miwu.ktx.simpleDataFlow
import com.github.miwu.logic.setting.AppSetting
import com.github.miwu.AppViewModel
import kotlinx.coroutines.launch
import miwu.miot.MiotClient
import miwu.miot.MiotManager
import miwu.miot.model.miot.MiotDevice
import miwu.miot.model.miot.MiotHome
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainViewModel() : ViewModel(), KoinComponent {
    val appSetting: AppSetting by inject()
    val appViewModel: AppViewModel by inject()
    val homeList = mutableStateListOf<MiotHome>()
    val deviceList = mutableStateListOf<MiotDevice>()

    init {
        viewModelScope.launch {
            appViewModel.miotClient.Home.getHomes().onSuccess {
                with(it.result) {
                    homeList.addAll(shareHomes ?: emptyList())
                }
                runCatching {
                    appViewModel.miotClient.Home.getDevices(homeList.first()).onSuccess {
                        deviceList.addAll(it.result.deviceInfo ?: emptyList())
                    }
                }
            }
        }
    }
}