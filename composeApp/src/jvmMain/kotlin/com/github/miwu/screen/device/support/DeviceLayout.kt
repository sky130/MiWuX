package com.github.miwu.screen.device.support

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.github.miwu.screen.device.isValueList
import miwu.compose.wrapper.base.ComposeMiwuWrapper

typealias ComposeMiwuWrapperList = SnapshotStateList<ComposeMiwuWrapper<*>>

data class DeviceLayout(
    val wrapperList: MutableList<ComposeMiwuWrapper<*>> = mutableListOf(),
    val headerList: ComposeMiwuWrapperList = mutableStateListOf(),
    val subHeaderList: ComposeMiwuWrapperList = mutableStateListOf(),
    val bodyList: ComposeMiwuWrapperList = mutableStateListOf(),
    val subFooterList: ComposeMiwuWrapperList = mutableStateListOf(),
    val footerList: ComposeMiwuWrapperList = mutableStateListOf(),
    val group: MutableMap<String, ComposeMiwuWrapperList> = mutableMapOf()
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
            Column {
                list.forEach {
                    it.Content()
                }
            }
        }
    }

    @Suppress("FunctionName")
    fun LazyListScope.GroupList(list: List<ComposeMiwuWrapper<*>>) {
        item {
            FlowRow {
                list.forEach {
                    it.Content()
                }
            }
        }
    }
}