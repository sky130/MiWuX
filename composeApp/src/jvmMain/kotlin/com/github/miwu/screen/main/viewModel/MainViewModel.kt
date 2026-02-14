package com.github.miwu.screen.main.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.miwu.logic.datastore.MiotUserDataStore
import com.github.miwu.logic.datastore.serializer.MiotUserSerializer
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.logic.repository.DeviceRepository
import fr.haan.resultat.Resultat
import fr.haan.resultat.runCatchingL
import fr.haan.resultat.toResultat
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import miwu.miot.model.miot.MiotDevice
import miwu.miot.model.miot.MiotHome
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainViewModel() : ViewModel(), KoinComponent {
    private val appRepository: AppRepository by inject()
    private val deviceRepository: DeviceRepository by inject()
    private val miotUserDataStore: MiotUserDataStore by inject()

    val deviceMetadataHandler = deviceRepository.deviceMetadataHandler

    val homeList = appRepository.homes

    val miotUser = miotUserDataStore.data.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        MiotUserSerializer.defaultValue
    )

    val deviceList: StateFlow<Resultat<List<Triple<MiotDevice, String, String>>>> = combine(
        appRepository.devices,
        deviceRepository.deviceMetadataHandler
    ) { devices, handler ->
        when {
            devices.isLoading -> Resultat.loading()
            else -> runCatchingL {
                devices.getOrNull()
                    .orEmpty()
                    .map { device ->
                        Triple(
                            device,
                            handler.getRoom(device.did),
                            handler.getIcon(device.model) ?: ""
                        )
                    }
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        Resultat.loading()
    )

    fun updateHome(miotHome: MiotHome) {
        viewModelScope.launch {
            appRepository.setActiveHome(miotHome)
        }
    }
}