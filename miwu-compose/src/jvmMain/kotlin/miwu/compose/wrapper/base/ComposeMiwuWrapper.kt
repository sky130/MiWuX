package miwu.compose.wrapper.base

import androidx.compose.runtime.Composable
import miwu.compose.icon.iconPainter
import miwu.support.base.MiwuWidget
import miwu.support.base.MiwuWrapper

abstract class ComposeMiwuWrapper<T>(widget: MiwuWidget<T>) : MiwuWrapper<T>(widget) {


    val composeIcon @Composable get() = iconPainter(icon)

    @Composable
    abstract fun Content()

}