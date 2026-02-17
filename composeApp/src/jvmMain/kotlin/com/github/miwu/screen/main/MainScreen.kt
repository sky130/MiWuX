@file:OptIn(ExperimentalMaterialApi::class)

package com.github.miwu.screen.main


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.animation.veilOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.DrawerDefaults.backgroundColor
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.savedstate.savedState
import coil3.compose.AsyncImage
import com.github.miwu.LocalMiotUser
import com.github.miwu.LocalRootNavBackStack
import com.github.miwu.logic.repository.entity.MiotHomeData
import com.github.miwu.route.Route
import com.github.miwu.screen.main.viewModel.MainViewModel
import fr.haan.resultat.Resultat
import fr.haan.resultat.Resultat.*
import fr.haan.resultat.fold
import miwu.common.resources.Res
import miwu.common.resources.ic_dropdown
import miwu.compose.DropdownMenu
import miwu.compose.DropdownMenuItem
import miwu.compose.Label
import miwu.compose.Text
import miwu.compose.Title
import miwu.compose.basic.LocalColor
import miwu.compose.basic.MiwuTheme
import miwu.compose.fadeEdge
import miwu.compose.normalClickable
import miwu.compose.rememberDropdownMenuState
import miwu.miot.client.MiotHomeClient
import miwu.miot.model.miot.MiotDevice
import miwu.widget.Text
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen(viewModel: MainViewModel = koinViewModel()) {
    val currentHome by viewModel.currentHome.collectAsState()
    var currentRoom by rememberSaveable { mutableStateOf("") }
    val iconMap by viewModel.iconMap.collectAsState()
    LaunchedEffect(currentHome) {
        currentRoom = currentHome.getOrNull()?.rooms?.keys?.firstOrNull() ?: ""
    }
    Column(
        Modifier
            .fillMaxSize()
            .background(MiwuTheme.colors.background)
    ) {
        TopAppBar(viewModel)
        Column(Modifier.weight(1f)) {
            when (val home = currentHome) {
                is Failure -> {

                }

                is Loading -> {
                    Text("正在加载房间数据...")
                }

                is Success -> {
                    val home = home.value
                    val rooms = home.rooms.keys.toList()
                    val pageState = rememberPagerState(0) { rooms.size }
                    LaunchedEffect(currentRoom) {
                        pageState.animateScrollToPage(rooms.indexOf(currentRoom))
                    }
                    RoomRow(currentRoom, rooms) {
                        currentRoom = it
                    }
                    HorizontalPager(
                        pageState,
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 10.dp)
                    ) { index ->
                        DeviceGrid(home.rooms[rooms[index]].orEmpty(), iconMap, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun DeviceGrid(
    devices: List<MiotDevice>,
    icons: Map<String, String>,
    modifier: Modifier
) {
    val backStack = LocalRootNavBackStack.current
    val miotUser = LocalMiotUser.current
    LazyVerticalGrid(
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = modifier.padding(start = 10.dp, end = 10.dp).fillMaxSize(),
        columns = GridCells.Adaptive(minSize = 170.dp)
    ) {
        items(devices) { device ->
            val shape = RoundedCornerShape(10.dp)
            Surface(
                onClick = {
                     backStack.add(Route.Device(miotUser, device))
                },
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape)
                    .border(1.dp, MiwuTheme.colors.border, shape),
                color = MiwuTheme.colors.surface
            ) {
                Column(modifier = Modifier.padding(15.dp)) {
                    AsyncImage(model = icons[device.model], contentDescription = null, modifier = Modifier.size(35.dp))
                    Title {
                        Text(device.name)
                    }
                    Label {
                        Text("设备在线")
                    }
                }
            }
        }
    }
}

@Composable
fun RoomRow(currentRoom: String, rooms: List<String>, onClick: (String) -> Unit) {
    val lazyListState = rememberLazyListState()
    LazyRow(
        state = lazyListState,
        horizontalArrangement = Arrangement.spacedBy(17.dp),
        modifier = Modifier
            .padding(horizontal = 15.dp)
            .fadeEdge(
                20.dp,
                start = lazyListState.canScrollBackward,
                end = lazyListState.canScrollForward
            )
    ) {
        items(rooms) {
            val color = if (it == currentRoom) LocalColor.current.onSurface
            else LocalColor.current.onSurface.copy(alpha = 0.4f)
            Text(
                it,
                color = color,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                modifier = Modifier.normalClickable { onClick(it) }
            )
        }
    }
}

@Composable
fun TopAppBar(viewModel: MainViewModel) {
    val homeList by viewModel.homeList.collectAsState()
    val currentHomeId by viewModel.currentHomeId.collectAsState()
    val currentHome by viewModel.homeList.collectAsState()
    val name = currentHome.firstOrNull { it.id == currentHomeId }?.name ?: "null"
    val dropdownMenuState = rememberDropdownMenuState()
    Row(Modifier.padding(15.dp)) {
        HomeTitle(name) {
            dropdownMenuState.toggle()
        }
    }
    DropdownMenu(dropdownMenuState, Modifier.padding(top = 40.dp, start = 5.dp)) {
        for (home in homeList) {
            DropdownMenuItem(
                currentHomeId == home.id,
                home.name,
                if (home.isShareHome) {
                    "共享家庭"
                } else {
                    "%d个房间｜%d个设备".format(home.rooms.size, home.dids.size)
                }
            ) {
                viewModel.setActiveHome(home)
                dropdownMenuState.collapse()
            }
        }
    }
}

@Composable
fun HomeTitle(name: String, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Title {
            Text(name, modifier = Modifier.normalClickable(onClick))
        }
        Icon(
            painterResource(Res.drawable.ic_dropdown),
            null,
            Modifier.size(20.dp),
            tint = MiwuTheme.colors.onBackground.copy(0.6f)
        )
    }

}