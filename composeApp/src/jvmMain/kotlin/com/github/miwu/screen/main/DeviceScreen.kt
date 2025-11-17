package com.github.miwu.screen.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.github.miwu.LocalRootNavController
import com.github.miwu.screen.device.Device
import miwu.ui.MiwuTheme
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import miwu.miot.helper.MiotIconHelper

@OptIn(
    ExperimentalSettingsApi::class,
    ExperimentalSettingsImplementation::class,
    ExperimentalFoundationApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun DeviceScreen(viewModel: MainViewModel) {
    val navController = LocalRootNavController.current
    LazyVerticalGrid(
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier.padding(start = 10.dp, end = 10.dp),
        columns = GridCells.Adaptive(minSize = 150.dp)
    ) {
        items(viewModel.deviceList) { device ->
            Surface(
                onClick = {
                    navController.navigate(Device(device))
                },
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp)),
                color = MiwuTheme.colors.surface
            ) {
                Column(modifier = Modifier.padding(15.dp)) {
                    var iconUrl by remember { mutableStateOf("") }
                    LaunchedEffect(Unit) {
                        iconUrl = MiotIconHelper.getIconUrl(device.model).toString()
                    }
                    AsyncImage(model = iconUrl, contentDescription = null, modifier = Modifier.size(35.dp))
                    CardTitle(device.name)
                    CardSubtitle("设备在线")
                }
            }
        }
    }
}
