package miwu.compose.wrapper

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import miwu.annotation.Wrapper
import miwu.compose.wrapper.base.BaseMiwuWrapper
import miwu.support.base.MiwuWidget
import miwu.ui.MiwuTheme
import miwu.widget.StatusText

@Wrapper(StatusText::class)
class ValueTextWrapper(widget: MiwuWidget<Int>) : BaseMiwuWrapper<Int>(widget) {
    var text by mutableStateOf("")
    var unit by mutableStateOf("")
    var desc by mutableStateOf("")


    @Composable
    override fun Content() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 5.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Column {
                Row {
                    Text(
                        text = text,
                        fontSize = 39.sp,
                        color = MiwuTheme.colors.onSurface,
                        modifier = Modifier.padding(bottom = 3.dp),
                        fontWeight = FontWeight(600)
                    )
                    Text(
                        text = unit,
                        color = MiwuTheme.colors.onSurfaceVariant,
                        fontSize = 17.sp,
                        modifier = Modifier.padding(top = 10.dp, start = 5.dp)
                    )
                }
                Text(
                    color = MiwuTheme.colors.onSurfaceVariant,
                    text = desc,
                    fontSize = 17.sp,
                )
            }
        }
    }

    override fun onUpdateValue(value: Int) {
        text =  valueList.firstOrNull { it.value == value }?.descriptionTranslation ?: "--"
    }

    override fun initWrapper() {
         unit = valueUnit
         desc = descriptionTranslation
    }
}