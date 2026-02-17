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
import miwu.miot.model.MiotUser
import miwu.miot.model.miot.MiotDevice
import miwu.support.base.MiwuWidget
import miwu.support.base.MiwuWrapper
import miwu.support.manager.MiotDeviceManager
import miwu.compose.basic.MiwuTheme
import miwu.widget.generated.wrapper.WrapperRegistry
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DeviceControlScreen(
    miotUser: MiotUser,
    device: MiotDevice,
    viewModel: DeviceViewModel = koinViewModel { parametersOf(miotUser, device) }
) {
    val layout = remember { DeviceLayout() }
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is DeviceInitiated -> {
                    initDeviceLayout(viewModel.manager, layout)
                }
            }
        }
    }
    LaunchedEffect(device) {
        viewModel.manager.init()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MiwuTheme.colors.background)
    ) {
        TitleBar(device) {
            runCatching {
                viewModel.manager.stop()
            }
        }
        with(layout) {
            LazyColumn(Modifier.padding(horizontal = 10.dp)) {
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
fun TitleBar(device: MiotDevice, onBack: () -> Unit = {}) {
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
                    onBack()
                    navController.removeLast()
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
            ?.also {
                if (widget.isValueList()) {
                    group.getOrPut(widget::class.java.name) {
                        mutableStateListOf()
                    }.add(it)
                } else {
                    add(it)
                }
            }
            ?.also(wrapperList::add)
    with(manager.layout) {
        Header(headerList::addWidget)
        SubHeader(subHeaderList::addWidget)
        Body(bodyList::addWidget)
        SubFooter(subFooterList::addWidget)
        Footer(footerList::addWidget)
        Unknown { /* TODO */ }
    }
    wrapperList.forEach(MiwuWrapper<*>::init)
}
