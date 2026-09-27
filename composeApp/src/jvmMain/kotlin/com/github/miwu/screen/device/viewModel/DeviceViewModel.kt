package com.github.miwu.screen.device.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.miwu.domain.gateway.MiotClientFactory
import com.github.miwu.domain.usecase.device.ResolveDeviceSessionUseCase
import com.github.miwu.logging.Logger
import com.github.miwu.screen.device.support.ComposeCache
import com.github.miwu.screen.device.support.ComposeTranslateHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import miwu.compose.icon.generated.icon.ComposeIcons
import miwu.dispatchers.DefaultDispatcher
import miwu.dispatchers.UiDispatcher
import miwu.miot.provider.MiotSpecAttrProvider
import miwu.support.manager.MiotDeviceManager
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class DeviceViewModel(
    @InjectedParam did: String,
    resolveDeviceSession: ResolveDeviceSessionUseCase,
    clientFactory: MiotClientFactory,
    specAttrProvider: MiotSpecAttrProvider,
    @UiDispatcher uiDispatcher: CoroutineDispatcher,
    @DefaultDispatcher workDispatcher: CoroutineDispatcher,
) : ViewModel(), MiotDeviceManager.Callback {
    private val logger = Logger()
    private val session = resolveDeviceSession(did)
    private val mutableEvent = MutableSharedFlow<Event>()

    val device = session?.device
    val event = mutableEvent.asSharedFlow()
    val manager: MiotDeviceManager? = session?.let {
        MiotDeviceManager.build(
            clientFactory.createDeviceClient(it.user),
            specAttrProvider,
            it.device,
            ComposeIcons,
            ComposeCache(),
            ComposeTranslateHelper,
            uiDispatcher,
            workDispatcher,
            this,
        )
    }

    init {
        if (session == null) {
            logger.warn("Device session could not be resolved")
        } else {
            logger.info("Device session resolved; manager initialized={}", manager != null)
        }
    }

    override fun onDeviceInitiated() {
        logger.info("Device manager reported initialization")
        viewModelScope.launch { mutableEvent.emit(Event.DeviceInitiated) }
    }

    override fun onCleared() {
        logger.info("Stopping device manager")
        manager?.stop()
        super.onCleared()
    }

    sealed interface Event {
        data object DeviceInitiated : Event
    }
}
