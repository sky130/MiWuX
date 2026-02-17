package com.github.miwu.screen.main.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.miwu.logic.datastore.MiotUserDataStore
import com.github.miwu.logic.datastore.serializer.MiotUserSerializer
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.logic.repository.CacheRepository
import com.github.miwu.logic.repository.MiotRepository
import com.github.miwu.logic.setting.AppSetting
import fr.haan.resultat.Resultat
import fr.haan.resultat.runCatchingL
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import miwu.miot.model.miot.MiotDevice
import miwu.miot.model.miot.MiotHome
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainViewModel() : ViewModel(), KoinComponent {
    private val miotRepository: MiotRepository by inject()
    private val deviceRepository: CacheRepository by inject()
    private val miotUserDataStore: MiotUserDataStore by inject()
    private val appSetting: AppSetting by inject()

    val currentHome = miotRepository.currentHome

    val iconMap = deviceRepository.icons
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    val currentHomeId = appSetting.homeId.flow
        .map { it.toString() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            appSetting.homeId.defaultValue.toString()
        )


    val homeList = miotRepository.homes
        .map { it.getOrNull().orEmpty() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())


    val miotUser = miotUserDataStore.data.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        MiotUserSerializer.defaultValue
    )

//    val deviceList: StateFlow<Resultat<List<Triple<MiotDevice, String, String>>>> = combine(
//        appRepository.devices,
//        deviceRepository.deviceMetadataHandler
//    ) { devices, handler ->
//        when {
//            devices.isLoading -> Resultat.loading()
//            else -> runCatchingL {
//                devices.getOrNull()
//                    .orEmpty()
//                    .map { device ->
//                        Triple(
//                            device,
//                            handler.getRoom(device.did),
//                            handler.getIcon(device.model) ?: ""
//                        )
//                    }
//            }
//        }
//    }.stateIn(
//        viewModelScope,
//        SharingStarted.Eagerly,
//        Resultat.loading()
//    )

    fun isActiveHome(home: MiotHome): Boolean {
        return runBlocking { appSetting.homeId.getValue().toString() == home.id }
    }

    fun setActiveHome(miotHome: MiotHome) {
        miotRepository.setActiveHome(miotHome)
    }
}