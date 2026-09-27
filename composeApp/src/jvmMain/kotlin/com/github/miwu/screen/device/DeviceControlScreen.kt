package com.github.miwu.screen.device

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.github.miwu.LocalRootNavBackStack
import com.github.miwu.screen.device.viewModel.DeviceViewModel.Event.DeviceInitiated
import com.github.miwu.screen.device.support.ComposeMiwuWrapperList
import com.github.miwu.screen.device.support.DeviceLayout
import com.github.miwu.screen.device.viewModel.DeviceViewModel
import miwu.annotation.ValueList
import miwu.common.resources.Res
import miwu.common.resources.ic_return
import miwu.compose.Text
import miwu.compose.Title
import miwu.compose.wrapper.base.ComposeMiwuWrapper
import miwu.miot.model.miot.MiotDevice
import miwu.support.MiwuWidget
import miwu.support.MiwuWrapper
import miwu.support.manager.MiotDeviceManager
import miwu.compose.basic.MiwuTheme
import miwu.compose.wrapper.base.Zone
import miwu.compose.wrapper.base.Zone.*
import miwu.widget.generated.wrapper.WrapperRegistry
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DeviceControlScreen(
    did: String,
    viewModel: DeviceViewModel = koinViewModel { parametersOf(did) },
) {
    val device = viewModel.device
    val manager = viewModel.manager
    if (device == null || manager == null) {
        UnavailableDeviceScreen()
        return
    }
    val layout = remember { DeviceLayout() }
    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                is DeviceInitiated -> {
                    initDeviceLayout(manager, layout)
                }
            }
        }
    }
    LaunchedEffect(manager) {
        manager.init()
    }
    DisposableEffect(manager) {
        onDispose(manager::stop)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MiwuTheme.colors.background)
    ) {
        TitleBar(device)
        with(layout) {
            LazyColumn(
                Modifier.padding(horizontal = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                List(headerList)
                List(subHeaderList)
                group.forEach { (name, list) ->
                    GroupList(list)
                }
                List(bodyList)
                List(subFooterList)
                List(footerList)
            }
        }
    }
}

@Composable
private fun UnavailableDeviceScreen() {
    val backStack = LocalRootNavBackStack.current
    Column(
        modifier = Modifier.fillMaxSize().background(MiwuTheme.colors.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("设备不可用，请返回后重试")
        Box(Modifier.padding(12.dp).clickable { if (backStack.size > 1) backStack.removeLast() }) {
            Text("返回")
        }
    }
}

fun MiwuWidget<*>.isValueList() = this::class.java.annotations.any { it is ValueList }

@Suppress("UNCHECKED_CAST")
private fun createWrapper(miotWidget: MiwuWidget<*>): ComposeMiwuWrapper<*>? {
    val wrapperClass =
        WrapperRegistry.registry[miotWidget::class.java] as? Class<out ComposeMiwuWrapper<*>> ?: return null
    return wrapperClass.getDeclaredConstructor(
        MiwuWidget::class.java,
    ).newInstance(miotWidget)
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TitleBar(device: MiotDevice) {
    val navController = LocalRootNavBackStack.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .clip(CircleShape)
                .clickable(
                    indication = ripple(),
                    interactionSource = remember { MutableInteractionSource() })
                {
                    if (navController.size > 1) navController.removeLast()
                }
        ) {
            Box(Modifier.padding(10.dp)) {
                Icon(
                    painterResource(Res.drawable.ic_return),
                    contentDescription = null,
                    tint = MiwuTheme.colors.onSurface,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
        Spacer(Modifier.width(3.dp))
        Title {
            Text(
                text = device.name,
                modifier = Modifier.weight(1f)
            )
        }
    }
}


suspend fun initDeviceLayout(manager: MiotDeviceManager, layout: DeviceLayout) = with(layout) {
    fun ComposeMiwuWrapperList.addWidget(widget: MiwuWidget<*>) =
        createWrapper(widget)
            ?.also(wrapperList::add)
            ?.also { wrapper ->
                if (widget.isValueList()) {
                    val groupName = widget.serviceName to widget.propertyName
                    group.getOrPut(groupName) { mutableStateListOf() }.add(wrapper)
                } else {
                    when (wrapper.remapTo) {
                        Header -> headerList.add(wrapper)
                        SubHeader -> subHeaderList.add(wrapper)
                        Body -> bodyList.add(wrapper)
                        SubFooter -> subFooterList.add(wrapper)
                        Footer -> footerList.add(wrapper)
                        Unspecified -> this.add(wrapper)
                    }
                }
            }
    with(manager.layout) {
        Header(headerList::addWidget)
        HeaderPanel(subHeaderList::addWidget)
        Body(bodyList::addWidget)
        FooterPanel(subFooterList::addWidget)
        Footer(footerList::addWidget)
        Unknown { /* TODO */ }
    }
    wrapperList.forEach(MiwuWrapper<*>::init)
}
