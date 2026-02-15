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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import miwu.compose.basic.DefaultColorScheme
import androidx.compose.material.Text as MaterialText
import miwu.compose.basic.MiwuColor
import miwu.compose.basic.MiwuTheme

internal val LocalTextStyle = compositionLocalOf { TextStyle.Default }
internal val LocalTextColor = compositionLocalOf { DefaultColorScheme.onSurface }
internal val LocalTextFontWeight = compositionLocalOf { FontWeight.Normal }
internal val LocalTextFontSize = compositionLocalOf { TextStyle.Default.fontSize }

@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
) {
    MaterialText(
        text = text,
        color = LocalTextColor.current,
        modifier = modifier,
        fontFamily = MiwuTheme.fontFamily,
        fontWeight = LocalTextFontWeight.current,
        fontSize = LocalTextFontSize.current,
        style = LocalTextStyle.current
    )
}


@Composable
fun Title(
    color: Color = Color.Unspecified,
    fontWeight: FontWeight = FontWeight(600),
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
fun Label(
    color: Color = Color.Unspecified,
    fontWeight: FontWeight = FontWeight.Normal,
    fontSize: TextUnit = 14.sp,
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
