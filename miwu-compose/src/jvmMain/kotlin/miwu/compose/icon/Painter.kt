package miwu.compose.icon

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import miwu.support.icon.Icon
import org.jetbrains.compose.resources.painterResource

private val emptyImageBitmap: ImageBitmap by lazy { ImageBitmap(1, 1) }
private val emptySvgPainter: Painter by lazy { BitmapPainter(emptyImageBitmap) }


@Composable
fun miwuIconPainter(icon: Icon) = when (icon) {
    is ComposeIcon -> painterResource(icon.res)
    else -> emptySvgPainter
}