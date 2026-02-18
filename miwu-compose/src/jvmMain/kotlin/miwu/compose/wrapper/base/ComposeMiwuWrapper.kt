package miwu.compose.wrapper.base

import androidx.compose.runtime.Composable
import miwu.compose.icon.miwuIconPainter
import miwu.support.base.MiwuWidget
import miwu.support.base.MiwuWrapper

abstract class ComposeMiwuWrapper<T>(widget: MiwuWidget<T>) : MiwuWrapper<T>(widget) {

    @Suppress("PropertyName")
    val Icons = widget.Icons

    /**
     * 手表端组件架构和其他平台的不一样，所以需要在 Wrapper 中重新指定组件在界面中的位置
     * @see Zone
     */
    open val remapTo: Zone = Zone.Unspecified

    @Suppress("PropertyName")
    val ComposeIcon @Composable get() = miwuIconPainter(icon)

    @Composable
    abstract fun Content()

}