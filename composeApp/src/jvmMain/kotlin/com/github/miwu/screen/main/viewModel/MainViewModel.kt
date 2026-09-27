package com.github.miwu.screen.main.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.miwu.domain.repository.DeviceMetadataRepository
import com.github.miwu.domain.repository.HomeRepository
import com.github.miwu.domain.repository.SettingsRepository
import com.github.miwu.domain.usecase.device.GetSortedDevicesUseCase
import com.github.miwu.domain.usecase.room.GetSortedRoomsUseCase
import com.github.miwu.domain.usecase.scene.GetHomeScenesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import miwu.miot.model.miot.MiotHome
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class MainViewModel(
    private val homeRepository: HomeRepository,
    metadataRepository: DeviceMetadataRepository,
    settingsRepository: SettingsRepository,
    getSortedDevices: GetSortedDevicesUseCase,
    getSortedRooms: GetSortedRoomsUseCase,
    getHomeScenes: GetHomeScenesUseCase,
) : ViewModel() {
    val currentHome = homeRepository.currentHome
    val userInfo = homeRepository.userInfo
    val devices = getSortedDevices().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val rooms = getSortedRooms().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val scenes = getHomeScenes().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val iconMap = metadataRepository.metadata
        .map { it.icons }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())
    val currentHomeId = settingsRepository.selectedHomeId
        .map(Long::toString)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "0")
    val homeList = homeRepository.homes
        .map { it.getOrNull().orEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setActiveHome(home: MiotHome) {
        viewModelScope.launch { homeRepository.selectHome(home) }
    }

    fun refreshHome() {
        viewModelScope.launch { homeRepository.refreshCurrentHome() }
    }
}
