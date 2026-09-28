package com.github.miwu.tray

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import com.github.miwu.logging.Logger
import dev.nucleusframework.application.DecoratedWindow
import dev.nucleusframework.application.NucleusApplicationScope
import dev.nucleusframework.composenativetray.tray.api.NativeTray
import dev.nucleusframework.composenativetray.tray.impl.WindowsTrayInitializer
import dev.nucleusframework.composenativetray.trayapp.TrayAppState
import dev.nucleusframework.composenativetray.trayapp.getTrayWindowPosition
import dev.nucleusframework.composenativetray.trayapp.getTrayWindowPositionForInstance
import dev.nucleusframework.composenativetray.utils.isMenuBarInDarkMode
import dev.nucleusframework.core.runtime.Platform
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

private const val POSITION_POLL_INTERVAL_MS = 50L
private const val POSITION_TIMEOUT_MS = 3_000L
private const val FOLLOW_INTERVAL_MS = 250L
private const val TOGGLE_DEBOUNCE_MS = 250L
private const val POSITION_CHANGE_THRESHOLD = 1f

private object DecoratedTrayAppLog

private val logger = Logger<DecoratedTrayAppLog>()

/**
 * A tray application backed by a regular Nucleus [DecoratedWindow].
 *
 * Unlike the panel implementation supplied by ComposeNativeTray, this keeps a real window in the
 * composition and continually anchors it to the native tray icon while it is visible.
 */
@Composable
fun NucleusApplicationScope.DecoratedTrayApp(
    windowsIcon: Painter,
    macLinuxIcon: ImageVector,
    tooltip: String,
    state: TrayAppState,
    horizontalOffset: Int = 0,
    verticalOffset: Int = 8,
    content: @Composable () -> Unit,
) {
    val isVisible by state.isVisible.collectAsState()
    val windowSize by state.windowSize.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val tray = remember { NativeTray() }
    val instanceKey = remember(tray) { tray.instanceKey() }
    val isDarkMenuBar = isMenuBarInDarkMode()

    var windowVisible by remember { mutableStateOf(false) }
    var pendingPosition by remember { mutableStateOf<WindowPosition?>(null) }
    var lastTrayActionAt by remember { mutableStateOf(0L) }
    var lastHiddenAt by remember { mutableStateOf(0L) }

    val windowState = rememberWindowState(
        size = windowSize,
        position = WindowPosition.PlatformDefault,
    )

    LaunchedEffect(windowSize) {
        windowState.size = windowSize
    }

    val hideWindow = {
        lastHiddenAt = System.currentTimeMillis()
        state.hide()
    }

    val trayAction by rememberUpdatedState {
        coroutineScope.launch {
            val now = System.currentTimeMillis()
            if (now - lastTrayActionAt < TOGGLE_DEBOUNCE_MS) return@launch
            lastTrayActionAt = now

            if (state.isVisible.value) {
                hideWindow()
                return@launch
            }
            if (now - lastHiddenAt < TOGGLE_DEBOUNCE_MS) return@launch

            pendingPosition = resolveStableTrayPosition(
                instanceKey = instanceKey,
                windowSize = state.windowSize.value,
                horizontalOffset = horizontalOffset,
                verticalOffset = verticalOffset,
            )
            state.show()
        }
    }

    LaunchedEffect(tray, tooltip, windowsIcon, macLinuxIcon, isDarkMenuBar) {
        tray.updateComposable(
            iconContent = {
                if (Platform.Current == Platform.Windows) {
                    Image(
                        painter = windowsIcon,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Image(
                        imageVector = macLinuxIcon,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        colorFilter = ColorFilter.tint(
                            if (isDarkMenuBar) Color.White else Color.Black,
                        ),
                    )
                }
            },
            tooltip = tooltip,
            primaryAction = { trayAction() },
        )
    }

    DisposableEffect(tray) {
        onDispose { tray.dispose() }
    }

    LaunchedEffect(isVisible, windowSize, horizontalOffset, verticalOffset) {
        if (!isVisible) {
            if (windowVisible) lastHiddenAt = System.currentTimeMillis()
            windowVisible = false
            return@LaunchedEffect
        }

        val position = pendingPosition
            ?.takeIf(::isUsablePosition)
            ?: resolveStableTrayPosition(
                instanceKey = instanceKey,
                windowSize = windowSize,
                horizontalOffset = horizontalOffset,
                verticalOffset = verticalOffset,
            )
        pendingPosition = null
        windowState.position = position
        windowVisible = true
    }

    LaunchedEffect(windowVisible, windowSize, horizontalOffset, verticalOffset) {
        while (windowVisible) {
            delay(FOLLOW_INTERVAL_MS)
            if (Platform.Current == Platform.Windows) {
                runCatching { WindowsTrayInitializer.refreshPosition(instanceKey) }
                    .onFailure { logger.debug("Unable to refresh the Windows tray position", it) }
            }

            val nextPosition = getTrayWindowPositionForInstance(
                instanceId = instanceKey,
                windowWidth = windowSize.width.value.toInt(),
                windowHeight = windowSize.height.value.toInt(),
                horizontalOffset = horizontalOffset,
                verticalOffset = verticalOffset,
            )
            if (
                nextPosition is WindowPosition.Absolute &&
                isUsablePosition(nextPosition) &&
                positionChanged(windowState.position, nextPosition)
            ) {
                windowState.position = nextPosition
            }
        }
    }

    DecoratedWindow(
        onCloseRequest = hideWindow,
        state = windowState,
        visible = windowVisible,
        resizable = false,
        focusable = true,
        alwaysOnTop = true,
        undecorated = true,
        hiddenFromDock = true,
        transparent = true,
        visibleOnAllWorkspaces = true,
        onPreviewKeyEvent = { event ->
            if (event.key == Key.Escape && event.type == KeyEventType.KeyDown) {
                hideWindow()
                true
            } else {
                false
            }
        },
    ) {
        val window = nucleusWindow

        LaunchedEffect(windowVisible) {
            if (windowVisible) {
                runCatching {
                    window.toFront()
                    window.requestFocus()
                }.onFailure { logger.debug("Unable to focus the tray window", it) }
            }
        }

        LaunchedEffect(window) {
            var hadFocus = false
            window.focusFlow.collect { focused ->
                if (focused) {
                    hadFocus = true
                } else if (hadFocus && windowVisible) {
                    hadFocus = false
                    hideWindow()
                }
            }
        }

        content()
    }
}

private suspend fun resolveStableTrayPosition(
    instanceKey: String,
    windowSize: DpSize,
    horizontalOffset: Int,
    verticalOffset: Int,
): WindowPosition {
    val width = windowSize.width.value.toInt()
    val height = windowSize.height.value.toInt()

    if (Platform.Current == Platform.Windows) {
        runCatching { WindowsTrayInitializer.refreshPosition(instanceKey) }
            .onFailure { logger.debug("Unable to refresh the Windows tray position", it) }
    }

    var previous: WindowPosition.Absolute? = null
    val attempts = (POSITION_TIMEOUT_MS / POSITION_POLL_INTERVAL_MS).toInt()
    repeat(attempts) {
        val current = getTrayWindowPositionForInstance(
            instanceId = instanceKey,
            windowWidth = width,
            windowHeight = height,
            horizontalOffset = horizontalOffset,
            verticalOffset = verticalOffset,
        )
        if (current is WindowPosition.Absolute && isUsablePosition(current)) {
            if (previous != null && !positionChanged(previous, current)) return current
            previous = current
        }
        delay(POSITION_POLL_INTERVAL_MS)
    }

    previous?.let { return it }
    logger.warn("Tray position was unavailable; using the platform fallback")
    return getTrayWindowPosition(
        windowWidth = width,
        windowHeight = height,
        horizontalOffset = horizontalOffset,
        verticalOffset = verticalOffset,
    )
}

private fun isUsablePosition(position: WindowPosition): Boolean {
    if (position !is WindowPosition.Absolute) return false
    return !(position.x.value == 0f && position.y.value == 0f)
}

private fun positionChanged(
    current: WindowPosition,
    next: WindowPosition.Absolute,
): Boolean {
    if (current !is WindowPosition.Absolute) return true
    return abs(current.x.value - next.x.value) > POSITION_CHANGE_THRESHOLD ||
        abs(current.y.value - next.y.value) > POSITION_CHANGE_THRESHOLD
}
