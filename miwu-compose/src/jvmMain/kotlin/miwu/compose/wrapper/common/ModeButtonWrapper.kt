package miwu.compose.wrapper.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import miwu.compose.WrapperLabel
import miwu.compose.WrapperTitle
import miwu.compose.basic.MiwuTheme
import miwu.compose.ripple
import miwu.compose.wrapper.base.ComposeMiwuWrapper
import miwu.compose.wrapper.base.Zone
import miwu.support.base.MiwuWidget
import miwu.widget.ModeButton
import miwu.widget.SwitchButton

@Wrapper(ModeButton::class)
class ModeButtonWrapper(widget: MiwuWidget<Int>) : ComposeMiwuWrapper<Int>(widget) {
    private var value by mutableStateOf(defaultValue)

    override val remapTo = Zone.SubFooter

    @Composable
    override fun Content() {
        val value = value
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(value == defaultValue)
            Spacer(Modifier.height(5.dp))
            WrapperLabel {
                Text(descriptionTranslation)
            }
        }
    }

    private fun onClick() {
        update(defaultValue)
    }

    @Composable
    private fun Icon(enabled: Boolean) {
        val background = if (enabled) MiwuTheme.colors.enabled else MiwuTheme.colors.disabled
        val shape = RoundedCornerShape(10.dp)
        Box(
            Modifier
                .clip(shape)
                .background(background, shape)
                .ripple {
                    onClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Box(Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                Icon(ComposeIcon, null, Modifier.size(20.dp), tint = Color.White)
            }
        }
    }

    override fun initWrapper() {

    }

    override fun onUpdateValue(value: Int) {
        this.value = value
    }
}