package miwu.compose.wrapper.common

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.sp
import miwu.annotation.Wrapper
import miwu.common.resources.Res
import miwu.common.resources.ic_down
import miwu.common.resources.ic_up
import miwu.compose.Label
import miwu.compose.Text
import miwu.compose.Title
import miwu.compose.basic.MiwuTheme
import miwu.compose.border
import miwu.compose.ripple
import miwu.compose.wrapper.base.ComposeMiwuWrapper
import miwu.compose.wrapper.base.Zone
import miwu.support.base.MiwuWidget
import miwu.support.unit.Unit
import miwu.widget.DoubleValueController
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Wrapper(DoubleValueController::class)
class DoubleValueButtonWrapper(widget: MiwuWidget<Double>) : ComposeMiwuWrapper<Double>(widget) {
    private var value by mutableStateOf(16.0)
    private val unit = when (valueOriginUnit) {
        Unit.Celsius -> "°"
        else -> valueUnit
    }

    override val remapTo = Zone.SubHeader

    @Composable
    override fun Content() {
        val value = value
        Box(Modifier.wrapContentSize().border()) {
            Row(Modifier.fillMaxWidth().padding(10.dp)) {
                Icon(Res.drawable.ic_down) {
                    update((value - 1).coerceIn(valueRange.first, valueRange.second))
                }
                Row(
                    Modifier.fillMaxHeight().weight(1f).align(Alignment.CenterVertically),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    val text = "$value".takeIf { !it.endsWith(".0") } ?: "${value.toInt()}"
                    Title {
                        Text(text, fontSize = 23.sp)
                    }
                    Spacer(Modifier.width(3.dp))
                    Label {
                        Text(unit, fontSize = 18.sp)
                    }
                }
                Icon(Res.drawable.ic_up) {
                    update((value + 1).coerceIn(valueRange.first, valueRange.second))
                }
            }
        }
    }


    @Composable
    private fun Icon(icon: DrawableResource, onClick: () -> kotlin.Unit = {}) {
        Box(Modifier.clip(CircleShape).ripple(onClick)) {
            Box(Modifier.padding(10.dp)) {
                Icon(
                    painterResource(icon),
                    null,
                    Modifier.size(20.dp)
                )
            }
        }
    }

    override fun initWrapper() {

    }

    override fun onUpdateValue(value: Double) {
        this.value = value
    }
}