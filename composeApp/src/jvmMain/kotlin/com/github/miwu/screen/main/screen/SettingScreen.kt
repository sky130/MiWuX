package com.github.miwu.screen.main.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.github.miwu.screen.main.viewModel.MainViewModel
import miwu.compose.Text
import miwu.compose.Title
import miwu.miot.model.miot.MiotUserInfo.UserInfo
import miwu.support.icon.Icon
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingScreen(viewModel: MainViewModel = koinViewModel()) {
//    val userInfo by viewModel.userInfo.collectAsState()
    Column {
//        ProfileColumn(userInfo)
    }
//    val homeList by viewModel.homeList.collectAsState()
//    when (homeList) {
//        is Resultat.Failure -> {
//            /** TODO **/
//            return
//        }
//
//        is Resultat.Loading -> {
//            /** TODO **/
//            return
//        }
//
//        else -> Unit
//    }
//
//    Box {
//        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
//            items(homeList.getOrNull().orEmpty()) { home ->
//                Surface(
//                    onClick = {
//                        viewModel.updateHome(home)
//                    },
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .clip(RoundedCornerShape(10.dp)),
//                    color = MiwuTheme.colors.surface
//                ) {
//                    Column(modifier = Modifier.padding(15.dp)) {
//                        Text(home.name)
//                    }
//                }
//            }
//        }
//    }
}

@Composable
fun ItemCard(title: String, icon: Icon, color: Color, onClick: () -> Unit) {

}

@Composable
fun ProfileColumn(user: UserInfo) {
    Row(
        modifier = Modifier.padding(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = user.avatar,
            contentDescription = null,
            modifier = Modifier
                .padding(10.dp)
                .clip(CircleShape)
                .size(64.dp)
        )
        Column {
            Title(fontWeight = FontWeight(700), fontSize = 20.sp) {
                Text(user.nickname)
            }
        }
    }
}