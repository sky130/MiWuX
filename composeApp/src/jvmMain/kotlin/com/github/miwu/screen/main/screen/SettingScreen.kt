package com.github.miwu.screen.main.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.github.miwu.route.Route
import com.github.miwu.screen.main.CardSubtitle
import com.github.miwu.screen.main.CardTitle
import com.github.miwu.screen.main.viewModel.MainViewModel
import fr.haan.resultat.Resultat
import kotlinx.coroutines.launch
import miwu.ui.MiwuTheme
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingScreen(viewModel: MainViewModel = koinViewModel()) {
    val homeList by viewModel.homeList.collectAsState()
    when (homeList) {
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

    Box {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(homeList.getOrNull().orEmpty()) { home ->
                Surface(
                    onClick = {
                        viewModel.updateHome(home)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp)),
                    color = MiwuTheme.colors.surface
                ) {
                    Column(modifier = Modifier.padding(15.dp)) {
                        Text(home.name)
                    }
                }
            }
        }
    }
}