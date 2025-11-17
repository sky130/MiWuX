package miwu.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
/**
 * Miwu应用主题颜色配置
 * @param primary 主题色，用于选中状态和强调元素
 * @param secondary 次要颜色，用于未选中状态和辅助元素
 * @param divider 分割线颜色
 * @param background 页面背景色
 * @param surface 卡片/表面背景色
 * @param onPrimary 主题色上的文字颜色
 * @param onSecondary 次要颜色上的文字颜色
 * @param onBackground 背景上的主要文字颜色
 * @param onSurface 表面上的主要文字颜色
 * @param onSurfaceVariant 表面上的次要文字颜色
 */
data class MiwuColorScheme(
    val primary: Color = Color(0xFF57D1B8),
    val secondary: Color = Color(0xFFB0B6C2),
    val divider: Color = Color(0xFFE0EBF3),
    val background: Color = Color(0xFFF3F6FD),
    val surface: Color = Color(0xCCFFFFFF),
    val onPrimary: Color = Color(0xFFFFFFFF),
    val onSecondary: Color = Color(0xFFFFFFFF),
    val onBackground: Color = Color(0xFF222222),
    val onSurface: Color = Color(0xFF222222),
    val onSurfaceVariant: Color = Color(0xFF777777),
)

data class MiwuColor(
    val lightColorScheme: MiwuColorScheme = MiwuColorScheme(),
    val darkColorScheme: MiwuColorScheme = MiwuColorScheme(
        primary = Color(0xFF4FC3AA),
        secondary = Color(0xFF6B7280),
        divider = Color(0xFF374151),
        background = Color(0xFF111827),
        surface = Color(0xCC1F2937),
        onPrimary = Color(0xFF000000),
        onSecondary = Color(0xFF000000),
        onBackground = Color(0xFFE5E7EB),
        onSurface = Color(0xFFE5E7EB),
        onSurfaceVariant = Color(0xFF9CA3AF),
    ),
)


val LocalColor = compositionLocalOf<MiwuColorScheme> {
    error("Color not set")
}

val LocalFontFamily = compositionLocalOf<FontFamily> {
    error("FontFamily not set")
}

object MiwuTheme {
    val colors: MiwuColorScheme @Composable get() = LocalColor.current
    val fontFamily: FontFamily @Composable get() = LocalFontFamily.current
}

@Composable
fun MiwuTheme(
    color: MiwuColor = MiwuColor(),
    fontFamily: FontFamily = miSansFontFamily,
    isDarkMode: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalColor provides if (isDarkMode) color.darkColorScheme else color.lightColorScheme,
        LocalFontFamily provides fontFamily
    ) {
        content()
    }
}