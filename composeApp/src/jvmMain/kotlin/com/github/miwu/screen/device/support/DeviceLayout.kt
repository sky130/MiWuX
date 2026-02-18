package com.github.miwu.screen.device.support

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.miwu.screen.device.isValueList
import miwu.compose.border
import miwu.compose.wrapper.base.ComposeMiwuWrapper

typealias ComposeMiwuWrapperList = SnapshotStateList<ComposeMiwuWrapper<*>>

data class DeviceLayout(
    val wrapperList: MutableList<ComposeMiwuWrapper<*>> = mutableListOf(),
    val headerList: ComposeMiwuWrapperList = mutableStateListOf(),
    val subHeaderList: ComposeMiwuWrapperList = mutableStateListOf(),
    val bodyList: ComposeMiwuWrapperList = mutableStateListOf(),
    val subFooterList: ComposeMiwuWrapperList = mutableStateListOf(),
    val footerList: ComposeMiwuWrapperList = mutableStateListOf(),
    val group: SnapshotStateMap<Pair<String, String>, ComposeMiwuWrapperList> = mutableStateMapOf()
) {
    fun clearAll() {
        wrapperList.clear()
        headerList.clear()
        subHeaderList.clear()
        bodyList.clear()
        subFooterList.clear()
        footerList.clear()
    }

    @Suppress("FunctionName")
    fun LazyListScope.List(list: List<ComposeMiwuWrapper<*>>) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                list.forEach {
                    it.Content()
                }
            }
        }
    }

    @Suppress("FunctionName")
    fun LazyListScope.GroupList(list: List<ComposeMiwuWrapper<*>>) {
        item {
            Box(
                Modifier
                    .fillMaxWidth()
                    .border()
            ) {
                FlowRow(
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        10.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalArrangement = Arrangement.spacedBy(
                        5.dp,
                        Alignment.CenterVertically
                    ),
                    itemVerticalAlignment = Alignment.CenterVertically
                ) {
                    list.forEach {
                        it.Content()
                    }
                }
            }
        }
    }
}