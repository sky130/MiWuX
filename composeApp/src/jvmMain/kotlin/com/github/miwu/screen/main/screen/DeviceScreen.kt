package com.github.miwu.screen.main.screen

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.github.miwu.LocalRootNavBackStack
import com.github.miwu.route.Route
import com.github.miwu.screen.main.CardSubtitle
import com.github.miwu.screen.main.CardTitle
import com.github.miwu.screen.main.viewModel.MainViewModel
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import fr.haan.resultat.Resultat
import miwu.compose.basic.MiwuTheme
import org.koin.compose.viewmodel.koinViewModel

@OptIn(
    ExperimentalSettingsApi::class,
    ExperimentalSettingsImplementation::class,
    ExperimentalFoundationApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun DeviceScreen(viewModel: MainViewModel = koinViewModel()) {
    val backStack = LocalRootNavBackStack.current
    val deviceList by viewModel.deviceList.collectAsState()
    val miotUser by viewModel.miotUser.collectAsState()
    when (deviceList) {
        is Resultat.Failure -> {
            /** TODO **/
            return
        }

        is Resultat.Loading -> {
            /** TODO **/
            return
        }

        else -> Unit
    }
    LazyVerticalGrid(
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier.padding(start = 10.dp, end = 10.dp),
        columns = GridCells.Adaptive(minSize = 150.dp)
    ) {
        items(deviceList.getOrNull().orEmpty()) { (device, _, icon) ->
            Surface(
                onClick = {
                    backStack.add(Route.Device(miotUser, device))
                },
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp)),
                color = MiwuTheme.colors.surface
            ) {
                Column(modifier = Modifier.padding(15.dp)) {
                    AsyncImage(model = icon, contentDescription = null, modifier = Modifier.size(35.dp))
                    CardTitle(device.name)
                    CardSubtitle("设备在线")
                }
            }
        }
    }
}
