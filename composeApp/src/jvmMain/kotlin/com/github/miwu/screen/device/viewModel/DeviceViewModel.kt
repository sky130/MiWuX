package com.github.miwu.screen.device.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.miwu.ktx.MiotDeviceClient
import com.github.miwu.screen.device.support.ComposeCache
import com.github.miwu.screen.device.support.ComposeTranslateHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import miwu.compose.icon.generated.icon.ComposeIcons
import miwu.miot.model.MiotUser
import miwu.miot.model.miot.MiotDevice
import miwu.miot.provider.MiotSpecAttrProvider
import miwu.support.manager.MiotDeviceManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DeviceViewModel(user: MiotUser, device: MiotDevice) : ViewModel(), MiotDeviceManager.Callback, KoinComponent {
    private val specAttrProvider: MiotSpecAttrProvider by inject()
    private val miotDeviceClient = MiotDeviceClient(user)
    private val _event = MutableSharedFlow<Event>()
    val event = _event.asSharedFlow()
    val manager = MiotDeviceManager.Companion.build(
        miotDeviceClient,
        specAttrProvider,
        device,
        ComposeIcons,
        ComposeCache(),
        ComposeTranslateHelper,
        Dispatchers.Default,
        this
    )

    override fun onDeviceInitiated() {
        viewModelScope.launch {
            _event.emit(Event.DeviceInitiated)
        }
    }

    override fun onCleared() {
        manager.stop()
        super.onCleared()
    }

    sealed interface Event {
        object DeviceInitiated : Event
    }
}