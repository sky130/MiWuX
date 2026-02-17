@file:OptIn(ExperimentalMaterialApi::class)

package com.github.miwu.screen.main


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.github.miwu.LocalGlobalSnackState
import com.github.miwu.LocalMiotUser
import com.github.miwu.LocalRootNavBackStack
import com.github.miwu.logic.repository.entity.MiotHomeData
import com.github.miwu.route.Route
import com.github.miwu.screen.main.viewModel.MainViewModel
import fr.haan.resultat.Resultat.*
import miwu.common.resources.Res
import miwu.common.resources.ic_dropdown
import miwu.common.resources.ic_menu
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
import miwu.miot.model.miot.MiotDevice
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen(viewModel: MainViewModel = koinViewModel()) {
    val currentHome by viewModel.currentHome.collectAsState()
    var currentHomeData by rememberSaveable { mutableStateOf<MiotHomeData?>(null) }
    var currentRoom by rememberSaveable { mutableStateOf("") }
    val iconMap by viewModel.iconMap.collectAsState()
    LaunchedEffect(currentHome) {
        if (currentHomeData != currentHome.getOrNull()) {
            currentHomeData = currentHome.getOrNull()
            currentRoom = currentHome.getOrNull()?.rooms?.keys?.firstOrNull() ?: ""
        }
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
                    LaunchedEffect(pageState.targetPage) {
                        currentRoom = rooms[pageState.targetPage]
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
    val snackState = LocalGlobalSnackState.current
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
                    if (device.isOnline)
                        backStack.add(Route.Device(miotUser, device))
                    else
                        snackState.showMessage("设备离线")
                },
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape)
                    .alpha(if (device.isOnline) 1f else 0.6f)
                    .border(1.dp, MiwuTheme.colors.border, shape),
                color = MiwuTheme.colors.surface
            ) {
                Column(modifier = Modifier.padding(15.dp)) {
                    AsyncImage(
                        model = icons[device.model],
                        contentDescription = device.name,
                        modifier = Modifier.size(45.dp)
                    )
                    Spacer(Modifier.height(5.dp))
                    Title {
                        Text(device.name, fontSize = 17.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Label {
                        if (device.isOnline)
                            Text("设备在线")
                        else
                            Text("设备离线", color = MiwuTheme.colors.onSurface.copy(0.5f))
                    }
                }
            }
        }
    }
}

@Composable
fun RoomRow(currentRoom: String, rooms: List<String>, onRoomClick: (String) -> Unit) {
    val lazyListState = rememberLazyListState()
    val dropdownMenuState = rememberDropdownMenuState()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 15.dp)
    ) {
        LazyRow(
            state = lazyListState,
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier
                .weight(1f)
                .fadeEdge(
                    20.dp,
                    start = lazyListState.canScrollBackward,
                    end = lazyListState.canScrollForward
                ),
        ) {
            items(rooms) {
                val color = if (it == currentRoom) LocalColor.current.onSurface
                else LocalColor.current.onSurface.copy(alpha = 0.4f)
                val weight = if (it == currentRoom) FontWeight.Medium else FontWeight.Normal
                Text(
                    it,
                    color = color,
                    fontWeight = weight,
                    fontSize = 18.sp,
                    modifier = Modifier.normalClickable { onRoomClick(it) }
                )
            }
        }
        Spacer(Modifier.width(5.dp))
        Box(
            Modifier
                .clip(RoundedCornerShape(100.dp))
                .background(MiwuTheme.colors.onBackground.copy(0.05f))
                .normalClickable {
                    dropdownMenuState.expand()
                }
        ) {
            Box(Modifier.padding(vertical = 5.dp, horizontal = 10.dp)) {
                Icon(
                    painterResource(Res.drawable.ic_menu),
                    null,
                    tint = MiwuTheme.colors.onBackground.copy(0.8f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
    DropdownMenu(
        dropdownMenuState,
        Modifier.padding(top = 70.dp, end = 10.dp),
        origin = TransformOrigin(1f, 0f),
        contentAlignment = Alignment.TopEnd
    ) {
        for (room in rooms) {
            DropdownMenuItem(
                room == currentRoom,
                room,
            ) {
                onRoomClick(room)
                dropdownMenuState.collapse()
            }
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