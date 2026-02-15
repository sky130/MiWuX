package com.github.miwu.screen.main


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.ripple
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import coil3.compose.AsyncImage
import com.github.miwu.route.Route
import com.github.miwu.route.replaceCurrent
import com.github.miwu.screen.main.viewModel.MainViewModel
import miwu.compose.Divider
import miwu.common.resources.*
import miwu.compose.basic.MiwuTheme
import miwu.compose.basic.miSansFontFamily
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

val LocalMainSingleBackStack = compositionLocalOf<SnapshotStateList<Route>> {
    error("No LocalNavController provided")
}

var selectedIndex by mutableStateOf(0)

@Composable
fun MainScreen(viewModel: MainViewModel = koinViewModel()) {
    val userInfo by viewModel.userInfo.collectAsState()
    val backStack: SnapshotStateList<Route> = remember { mutableStateListOf(Route.Main.entities[selectedIndex]) }
    CompositionLocalProvider(LocalMainSingleBackStack provides backStack) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MiwuTheme.colors.background)
        ) {
            var expanded by remember { mutableStateOf(false) }
            Column(modifier = Modifier.fillMaxSize()) {
                val backStack = LocalMainSingleBackStack.current
                NavDisplay(
                    backStack = backStack,
                    modifier = Modifier.weight(1f)
                ) { key ->
                    NavEntry(key = key, content = { key.Content() })
                }
                Divider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val iconList = listOf(
                        Res.drawable.ic_home,
                        Res.drawable.ic_device,
                        Res.drawable.ic_scene,
                    )
                    repeat(3) { idx ->
                        MiwuIcon(
                            res = iconList[idx],
                            selected = selectedIndex == idx,
                            onClick = {
                                selectedIndex = idx
                                backStack.replaceCurrent(Route.Main.entities[idx])
                            },
                            modifier = Modifier
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    MiwuImage(
                        url = userInfo.avatar,
                        selected = selectedIndex == 3,
                        onClick = {
                            selectedIndex = 3
                            backStack.replaceCurrent(Route.Main.Setting)
                        },
                    )
                }
            }

        }
    }
}

@Composable
fun CardTitle(text: String) {
    BasicText(
        text = text, style = TextStyle(
            color = MiwuTheme.colors.onSurface,
            fontFamily = miSansFontFamily,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        ), maxLines = 1
    )
}

@Composable
fun CardSubtitle(text: String) {
    BasicText(
        text = text, style = TextStyle(
            color = MiwuTheme.colors.onSurfaceVariant,
            fontSize = 15.sp,
            fontFamily = miSansFontFamily,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.25.sp
        ), maxLines = 1
    )
}

@Composable
fun MiwuIcon(
    res: DrawableResource, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val backgroundColor = when {
        selected -> MiwuTheme.colors.primary.copy(alpha = 0.16f)
        isHovered -> MiwuTheme.colors.primary.copy(alpha = 0.08f)
        else -> Color.Transparent
    }
    val colors = MiwuTheme.colors
    val ripple = remember { ripple(bounded = true, color = colors.primary) }
    Box(
        modifier = modifier
            .width(40.dp)
            .height(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .clickable(
                onClick = onClick,
                indication = ripple,
                interactionSource = interactionSource
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(res),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = if (selected) MiwuTheme.colors.primary else MiwuTheme.colors.secondary
        )
    }
}

@Composable
fun MiwuImage(
    url: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val backgroundColor = when {
        selected -> MiwuTheme.colors.primary.copy(alpha = 0.16f)
        isHovered -> MiwuTheme.colors.primary.copy(alpha = 0.08f)
        else -> Color.Transparent

    }
    val colors = MiwuTheme.colors
    val ripple = remember { ripple(bounded = true, color = colors.primary) }
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(
                onClick = onClick,
                indication = ripple,
                interactionSource = interactionSource
            ),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = url,
            contentDescription = null,
            modifier = Modifier
                .size(33.dp)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = if (selected) MiwuTheme.colors.primary else Color.Transparent,
                    shape = CircleShape
                )
        )
    }
}
