package miwu.compose

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import miwu.common.resources.Res
import miwu.common.resources.ic_close
import miwu.common.resources.ic_dry
import miwu.compose.basic.LocalColor
import miwu.compose.basic.MiwuTheme
import org.jetbrains.compose.resources.painterResource
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class SnackState {
    private val messages = mutableStateListOf<SnackMessage>()
    var currentMessage by mutableStateOf<SnackMessage?>(null)
        private set

    fun showMessage(text: String, duration: Duration = 2.seconds) {
        messages.add(SnackMessage(text, duration))
        if (currentMessage == null) next()
    }

    internal fun next() {
        currentMessage = messages.removeFirstOrNull()
    }

    data class SnackMessage(
        val text: String,
        val duration: Duration,
        val timestamp: Long = System.currentTimeMillis()
    )
}

@Composable
fun rememberSnackState() = remember { SnackState() }

@Composable
fun SnackHost(
    snackState: SnackState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp, vertical = 10.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        var visible by remember { mutableStateOf(false) }
        var dismiss by remember { mutableStateOf(false) }
        val message = snackState.currentMessage
        if (message != null) {

            suspend fun dismiss() {
                visible = false
                delay(300)
                snackState.next()
            }

            LaunchedEffect(message) {
                dismiss = false
                visible = false
                visible = true
                delay(message.duration)
                dismiss = true
            }

            LaunchedEffect(dismiss) {
                if (dismiss) {
                    dismiss()
                }
            }

            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn(
                    initialAlpha = 0.3f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
                ),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 300)
                ) + fadeOut(animationSpec = tween(durationMillis = 300)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, MiwuTheme.colors.border, RoundedCornerShape(8.dp))
                        .background(MiwuTheme.colors.surface)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Title {
                        Text(
                            text = message.text,
                            modifier = Modifier.background(Color.Transparent),
                            color = MiwuTheme.colors.onBackground,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    IconButton({
                        dismiss = true
                    }, modifier = Modifier.size(24.dp)) {
                        Icon(
                            painterResource(Res.drawable.ic_close),
                            contentDescription = null,
                            tint = LocalColor.current.onBackground
                        )
                    }
                }
            }
        }
    }
}
