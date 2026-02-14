package miwu.compose.wrapper.base

import androidx.compose.runtime.Composable
import miwu.support.base.MiwuWidget
import miwu.support.base.MiwuWrapper

abstract class ComposeMiwuWrapper<T>(widget: MiwuWidget<T>): MiwuWrapper<T>(widget) {

    @Composable
    abstract fun Content()

}