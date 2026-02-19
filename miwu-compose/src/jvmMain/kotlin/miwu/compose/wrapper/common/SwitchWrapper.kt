package miwu.compose.wrapper.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import miwu.annotation.Wrapper
import miwu.compose.Text
import miwu.compose.Title
import miwu.compose.WrapperTitle
import miwu.compose.basic.MiwuTheme
import miwu.compose.border
import miwu.compose.ripple
import miwu.compose.wrapper.base.ComposeMiwuWrapper
import miwu.support.base.MiwuWidget
import miwu.widget.Switch

@Wrapper(Switch::class)
class SwitchWrapper(widget: MiwuWidget<Boolean>) : ComposeMiwuWrapper<Boolean>(widget) {
    private var value by mutableStateOf(false)

    @Composable
    override fun Content() {
        val value = value
        Box(
            Modifier
                .fillMaxWidth()
                .border()
                .ripple {
                    onClick(value)
                }
        ) {
            Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(value)
                val text = if (value) "开启" else "关闭"
                Spacer(Modifier.width(10.dp))
                WrapperTitle {
                    Text(text)
                }
            }
        }
    }

    private fun onClick(current: Boolean) {
        update(!current)
    }

    @Composable
    private fun Icon(enabled: Boolean) {
        val background = if (enabled) MiwuTheme.colors.enabled else MiwuTheme.colors.disabled
        Box(
            Modifier
                .clip(CircleShape)
                .background(background, CircleShape)
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(ComposeIcon, null, Modifier.size(20.dp), tint = Color.White)
        }
    }

    override fun initWrapper() {

    }

    override fun onUpdateValue(value: Boolean) {
        this.value = value
    }
}