package miwu.compose.wrapper.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.vivvvek.seeker.Seeker
import miwu.annotation.Wrapper
import miwu.common.resources.Res
import miwu.common.resources.ic_down
import miwu.common.resources.ic_up
import miwu.compose.*
import miwu.compose.wrapper.base.ComposeMiwuWrapper
import miwu.compose.wrapper.base.Zone
import miwu.support.base.MiwuWidget
import miwu.support.unit.Unit
import miwu.widget.IntSeekbar
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import kotlin.math.roundToInt

@Wrapper(IntSeekbar::class)
class IntSeekBarButtonWrapper(widget: MiwuWidget<Int>) : ComposeMiwuWrapper<Int>(widget) {
    private var value by mutableStateOf(0f)

    override val remapTo = Zone.SubHeader

    @Composable
    override fun Content() {
        Box(Modifier.wrapContentSize().border()) {
            Column(Modifier.fillMaxWidth().padding(15.dp)) {
                WrapperTitle {
                    Text(descriptionTranslation)
                }
                Seeker(
                    value = value,
                    onValueChange = {
                        stopUpdate()
                        value = it
                    },
                    range = remember { valueRange.from.toFloat()..valueRange.to.toFloat() },
                    onValueChangeFinished = {
                        continueUpdate()
                        update(value.roundToInt())
                    }
                )
            }
        }
    }

    override fun initWrapper() {

    }

    override fun onUpdateValue(value: Int) {
        this.value = value.toFloat()
    }
}