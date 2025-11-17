package com.github.miwu.screen.main


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import miwu.ui.MiwuTheme
import miwu.ui.miSansFontFamily
import miwu.common.resources.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel


val LocalTabNavController = compositionLocalOf<NavHostController> {
    error("No LocalNavController provided")
}

var selectedIndex by mutableStateOf(0)

fun startDestination(index: Int) = when (index) {
    0 -> "home"
    1 -> "device"
    2 -> "scene"
    3 -> "setting"
    else -> "home"
}

@Composable
fun MainScreen() {
    val mainViewModel: MainViewModel = koinViewModel()
    val navController = rememberNavController()
    CompositionLocalProvider(LocalTabNavController provides navController) {
        Box(
            modifier = Modifier.fillMaxSize().background(MiwuTheme.colors.background)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                val controller = LocalTabNavController.current
                NavHost(
                    navController = controller,
                    startDestination = startDestination(selectedIndex),
                    modifier = Modifier.weight(1f)
                ) {
                    composable("home") { HomeScreen(mainViewModel) }
                    composable("device") { DeviceScreen(mainViewModel) }
                    composable("scene") { SceneScreen(mainViewModel) }
                    composable("setting") { SettingScreen(mainViewModel) }
                }
                Column(modifier = Modifier.fillMaxWidth().height(1.dp).background(MiwuTheme.colors.divider)) { }
                Row(
                    modifier = Modifier.fillMaxWidth().background(MiwuTheme.colors.surface).padding(11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val iconList = listOf(
                        Res.drawable.ic_home,
                        Res.drawable.ic_device,
                        Res.drawable.ic_scene,
                    )
                    repeat(3) { idx ->
                        MiwuIcon(
                            res = iconList[idx], selected = selectedIndex == idx, onClick = {
                                selectedIndex = idx
                                navController.navigate(startDestination(idx))
                            })
                    }
                    Spacer(Modifier.weight(1f))
                    MiwuIcon(
                        res = Res.drawable.ic_setting, selected = selectedIndex == 3, onClick = {
                            selectedIndex = 3
                            navController.navigate(startDestination(3))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CardTitle(text: String) {
    BasicText(
        text = text,
        style = TextStyle(
            color = MiwuTheme.colors.onSurface,
            fontFamily = miSansFontFamily,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        ),
        maxLines = 1
    )
}

@Composable
fun CardSubtitle(text: String) {
    BasicText(
        text = text,
        style = TextStyle(
            color = MiwuTheme.colors.onSurfaceVariant,
            fontSize = 14.sp,
            fontFamily = miSansFontFamily,
            fontWeight = FontWeight.Normal
        ),
        maxLines = 1
    )
}

@Composable
fun MiwuIcon(
    res: DrawableResource, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(44.dp).clip(RoundedCornerShape(10.dp))
            .background(if (selected) MiwuTheme.colors.primary.copy(alpha = 0.12f) else Color.Transparent)
            .clickable(onClick = onClick), contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(res),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            colorFilter = ColorFilter.tint(if (selected) MiwuTheme.colors.primary else MiwuTheme.colors.secondary)
        )
    }
}
