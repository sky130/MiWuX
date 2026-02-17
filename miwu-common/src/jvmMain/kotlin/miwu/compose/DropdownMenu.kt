package miwu.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.window.rememberPopupPositionProviderAtPosition
import kotlinx.coroutines.delay
import miwu.compose.basic.MiwuTheme

inline fun Modifier.normalClickable(crossinline onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}

@Composable
fun rememberDropdownMenuState() = remember { DropdownMenuState() }

class DropdownMenuState {
    var expanded by mutableStateOf(false)

    fun toggle() {
        expanded = !expanded
    }

    fun expand() {
        expanded = true
    }

    fun collapse() {
        expanded = false
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DropdownMenu(state: DropdownMenuState, modifier: Modifier = Modifier, context: @Composable () -> Unit) {
    val provider = rememberPopupPositionProviderAtPosition(Offset(0f, 0f))
    var animated by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    LaunchedEffect(state.expanded) {
        if (state.expanded) {
            expanded = true
            animated = false
            animated = true
        } else {
            animated = false
            delay(300)
            expanded = false
        }
    }
    if (state.expanded || expanded)
        Popup(
            provider,
            properties = PopupProperties(focusable = true),
            onDismissRequest = { }
        ) {
            AnimatedVisibility(animated, enter = fadeIn(), exit = fadeOut()) {
                Box(
                    Modifier
                        .size(LocalWindowInfo.current.containerDpSize)
                        .background(Color.Black.copy(0.6f))
                        .normalClickable {
                            state.collapse()
                        }
                )
            }
            Box(modifier.fillMaxWidth()) {
                val origin = remember { TransformOrigin(0f, 0f) }
                AnimatedVisibility(
                    animated,
                    enter = fadeIn() + scaleIn(transformOrigin = origin),
                    exit = fadeOut() + scaleOut(transformOrigin = origin)
                ) {
                    val shape = remember { RoundedCornerShape(15.dp) }
                    Column(
                        Modifier
                            .width(IntrinsicSize.Max)
                            .clip(shape)
                            .background(MiwuTheme.colors.background, shape)
                    ) {
                        context()
                    }
                }
            }
        }
}

@Composable
fun DropdownMenuItem(selected: Boolean, text: String, label: String, onClick: () -> Unit) {
    val colorScheme = MiwuTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val background = if (selected) colorScheme.primary.copy(0.1f) else Color.Transparent
    val textColor = if (selected) colorScheme.primary else colorScheme.onSurface
    val ripple = remember { ripple() }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(background)
            .clickable(
                interactionSource,
                indication = ripple
            ) {
                onClick()
            }
    ) {
        Column(Modifier.padding(vertical = 13.dp, horizontal = 15.dp).sizeIn(minWidth = 200.dp)) {
            Title(color = textColor) {
                Text(text)
            }
            Label(color = textColor) {
                Text(label)
            }
        }
    }
}