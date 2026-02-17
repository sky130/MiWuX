package miwu.compose.wrapper.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import miwu.annotation.Wrapper
import miwu.compose.wrapper.base.ComposeMiwuWrapper
import miwu.support.base.MiwuWidget
import miwu.widget.Switch

/**
 * 可以根据这个模板来实现新的 Wrapper
 */
@Wrapper(Nothing::class)
class NothingWrapper(widget: MiwuWidget<Unit>) : ComposeMiwuWrapper<Unit>(widget) {
    @Composable
    override fun Content() {

    }

    override fun initWrapper() {

    }

    override fun onUpdateValue(value: Unit) {

    }
}