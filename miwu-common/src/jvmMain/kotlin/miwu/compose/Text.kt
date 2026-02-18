package miwu.compose

import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import miwu.compose.basic.DefaultColorScheme
import androidx.compose.material.Text as MaterialText
import miwu.compose.basic.MiwuColor
import miwu.compose.basic.MiwuTheme

internal val LocalTextColor = compositionLocalOf { DefaultColorScheme.onSurface }
internal val LocalTextFontWeight = compositionLocalOf { FontWeight.Normal }
internal val LocalTextFontSize = compositionLocalOf { TextStyle.Default.fontSize }

@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color? = null,
    fontWeight: FontWeight? = null,
    fontSize: TextUnit? = null,
    fontFamily: FontFamily? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    BasicText(
        text,
        modifier = modifier,
        style = TextStyle(
            color = color ?: LocalTextColor.current,
            fontFamily = fontFamily ?: MiwuTheme.fontFamily,
            fontSize = fontSize ?: LocalTextFontSize.current,
            fontWeight = fontWeight ?: LocalTextFontWeight.current,
        ),
        maxLines = maxLines,
        overflow = overflow
    )
}


@Composable
fun Title(
    color: Color = Color.Unspecified,
    fontWeight: FontWeight = FontWeight.Medium,
    fontSize: TextUnit = 18.sp,
    content: @Composable () -> Unit
) {
    val textColor = if (color == Color.Unspecified) MiwuTheme.colors.onSurface else color
    CompositionLocalProvider(
        LocalTextColor provides textColor,
        LocalTextFontWeight provides fontWeight,
        LocalTextFontSize provides fontSize,
    ) {
        content()
    }
}

@Composable
fun WrapperTitle(
    color: Color = Color.Unspecified,
    fontWeight: FontWeight = FontWeight.Medium,
    fontSize: TextUnit = 16.sp,
    content: @Composable () -> Unit
) {
    val textColor = if (color == Color.Unspecified) MiwuTheme.colors.onSurface else color
    CompositionLocalProvider(
        LocalTextColor provides textColor,
        LocalTextFontWeight provides fontWeight,
        LocalTextFontSize provides fontSize,
    ) {
        content()
    }
}

@Composable
fun Label(
    color: Color = Color.Unspecified,
    fontWeight: FontWeight = FontWeight.Normal,
    fontSize: TextUnit = 14.sp,
    content: @Composable () -> Unit
) {
    val textColor = if (color == Color.Unspecified) MiwuTheme.colors.onSurface else color
    CompositionLocalProvider(
        LocalTextColor provides textColor.copy(0.7f),
        LocalTextFontWeight provides fontWeight,
        LocalTextFontSize provides fontSize,
    ) {
        content()
    }
}

@Composable
fun WrapperLabel(
    color: Color = Color.Unspecified,
    fontWeight: FontWeight = FontWeight.Normal,
    fontSize: TextUnit = 13.sp,
    content: @Composable () -> Unit
) {
    val textColor = if (color == Color.Unspecified) MiwuTheme.colors.onSurface else color
    CompositionLocalProvider(
        LocalTextColor provides textColor.copy(0.7f),
        LocalTextFontWeight provides fontWeight,
        LocalTextFontSize provides fontSize,
    ) {
        content()
    }
}