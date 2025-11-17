package com.github.miwu.screen.device

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.miwu.AppViewModel
import com.github.miwu.LocalRootNavController
import miwu.ui.MiwuTheme
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import miwu.miot.MiotManager
import miwu.miot.model.miot.MiotDevice
import miwu.support.manager.MiotDeviceManager
import miwu.support.manager.callback.DeviceManagerCallback
import miwu.common.resources.*
import miwu.compose.icon.generated.icon.ComposeIcons
import miwu.compose.wrapper.base.BaseMiwuWrapper
import miwu.support.base.MiwuWidget
import miwu.support.layout.on
import miwu.widget.generated.wrapper.WrapperRegistry
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DeviceControlScreen(device: MiotDevice, appViewModel: AppViewModel = koinViewModel()) {
    val miotManager: MiotManager = koinInject()
    val wrapperList = remember { arrayListOf<BaseMiwuWrapper<*>>() }
    val headerList = remember { mutableStateListOf<BaseMiwuWrapper<*>>() }
    val subHeaderList = remember { mutableStateListOf<BaseMiwuWrapper<*>>() }
    val bodyList = remember { mutableStateListOf<BaseMiwuWrapper<*>>() }
    val subFooterList = remember { mutableStateListOf<BaseMiwuWrapper<*>>() }
    val footerList = remember { mutableStateListOf<BaseMiwuWrapper<*>>() }
    var isDeviceInitiated by remember { mutableStateOf(false) }
    val manager = remember {
        MiotDeviceManager(
            appViewModel.miotClient,
            miotManager,
            device,
            ComposeIcons,
            ComposeCache(),
            ComposeTranslateHelper,
            Dispatchers.Default,
            deviceCallback {
                isDeviceInitiated = true
            }
        )
    }
    LaunchedEffect(device) {
        manager.init()
    }
    LaunchedEffect(isDeviceInitiated) {
        if (!isDeviceInitiated) return@LaunchedEffect
        on(manager.layout) {
            Header { widget ->
                headerList.let { viewGroup ->
                    createWrapper(widget)?.let {
                        wrapperList.add(it)
                        viewGroup.add(it)
                    }
                }
            }
            SubHeader { widget ->
                subHeaderList.let { viewGroup ->
                    createWrapper(widget)?.let {
                        wrapperList.add(it)
                        viewGroup.add(it)
                    }
                }
            }
            Body { widget ->
                bodyList.let { viewGroup ->
                    createWrapper(widget)?.let {
                        wrapperList.add(it)
                        viewGroup.add(it)
                    }
                }
            }
            SubFooter { widget ->
                subFooterList.let { viewGroup ->
                    createWrapper(widget)?.let {
                        wrapperList.add(it)
                        viewGroup.add(it)
                    }
                }
            }
            Footer { widget ->
                footerList.let { viewGroup ->
                    createWrapper(widget)?.let {
                        wrapperList.add(it)
                        viewGroup.add(it)
                    }
                }
            }
            Unknown {

            }
        }
        wrapperList.forEach {
            it.init()
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(MiwuTheme.colors.background)) {
        TitleBar(device){
            // manager.
        }
        LazyColumn(Modifier.padding(horizontal = 10.dp)) {
            List(headerList)
            List(subHeaderList)
            List(bodyList)
            List(subFooterList)
            List(footerList)
        }
    }
}

@Suppress("FunctionName")
fun LazyListScope.List(list: List<BaseMiwuWrapper<*>>) {
    item {
        Column {
            list.forEach { it.Content() }
        }
    }
}

@Suppress("UNCHECKED_CAST")
private fun createWrapper(miotWidget: MiwuWidget<*>): BaseMiwuWrapper<*>? {
    val wrapperClass =
        WrapperRegistry.registry[miotWidget::class.java] as? Class<out BaseMiwuWrapper<*>>
            ?: return null
    return wrapperClass.getDeclaredConstructor(
        MiwuWidget::class.java,
    ).newInstance(miotWidget)
}

inline fun deviceCallback(crossinline onDeviceInitiated: () -> Unit) = object : DeviceManagerCallback {
    override fun onDeviceInitiated() {
        onDeviceInitiated()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TitleBar(device: MiotDevice, onBack: () -> Unit = {}) {
    val navController = LocalRootNavController.current
    Row(modifier = Modifier.fillMaxWidth().padding(5.dp)) {
        Surface(
            onClick = {
                onBack()
                navController.popBackStack()
            },
            modifier = Modifier.clip(CircleShape).wrapContentSize(),
            color = Color.Transparent
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                Icon(
                    painterResource(Res.drawable.ic_return),
                    contentDescription = null,
                    tint = MiwuTheme.colors.onSurface,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Text(
            text = device.name,
            color = MiwuTheme.colors.onSurface,
            modifier = Modifier.wrapContentHeight().weight(1f).align(Alignment.CenterVertically),
            fontFamily = MiwuTheme.fontFamily,
            fontWeight = FontWeight(600),
            fontSize = 19.sp,
        )
    }
}

@Serializable
data class Device(val json: String) {
    constructor(device: MiotDevice) : this(Gson().toJson(device))

    val device: MiotDevice get() = Gson().fromJson(json, MiotDevice::class.java)
}