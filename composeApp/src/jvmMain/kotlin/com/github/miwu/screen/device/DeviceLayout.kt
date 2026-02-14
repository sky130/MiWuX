package com.github.miwu.screen.device

import androidx.compose.runtime.mutableStateListOf
import miwu.compose.wrapper.base.ComposeMiwuWrapper

typealias ComposeMiwuWrapperList = MutableList<ComposeMiwuWrapper<*>>

data class DeviceLayout(
    val wrapperList: ComposeMiwuWrapperList = arrayListOf(),
    val headerList: ComposeMiwuWrapperList = mutableStateListOf(),
    val subHeaderList: ComposeMiwuWrapperList = mutableStateListOf(),
    val bodyList: ComposeMiwuWrapperList = mutableStateListOf(),
    val subFooterList: ComposeMiwuWrapperList = mutableStateListOf(),
    val footerList: ComposeMiwuWrapperList = mutableStateListOf()
) {
    fun clearAll() {
        wrapperList.clear()
        headerList.clear()
        subHeaderList.clear()
        bodyList.clear()
        subFooterList.clear()
        footerList.clear()
    }
}