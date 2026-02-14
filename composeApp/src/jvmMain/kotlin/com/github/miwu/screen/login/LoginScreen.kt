package com.github.miwu.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.haan.resultat.onSuccess
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import miwu.common.resources.Res
import miwu.common.resources.ic_miwu
import miwu.common.resources.ic_miwu_round
import miwu.common.resources.ic_miwu_with_bg
import miwu.ui.MiwuTheme
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = koinViewModel()
) {
    LaunchedEffect(Unit) {
        loginViewModel.loadQrCode()
    }
    LaunchedEffect(Unit) {
        loginViewModel.event.collect { event ->
            when (event) {
                is LoginViewModel.Event.LoginFailure -> {
                    // TODO 这里弹出一个 toast 之类的东西
                }

                is LoginViewModel.Event.LoginSuccess -> {
                    // App 入口那里会处理登录，这里弹出一个 toast
                }

                is LoginViewModel.Event.ShowLoading -> Unit
            }
        }
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Image(
                painterResource(Res.drawable.ic_miwu_round),
                null,
                modifier = Modifier.size(72.dp)
            )
            // Text("MiWuX", fontWeight = FontWeight(800), fontSize = 20.sp)
            Text("使用小米账号登录至米屋")
            loginViewModel.qrcode.onSuccess {
                Image(
                    painter = rememberQrCodePainter(it),
                    contentDescription = null
                )
            }
            Row {
                Text("登录后默认已同意")
                Text("免责声明", color = MiwuTheme.colors.primary)
            }
            Spacer(Modifier.height(20.dp))
        }

    }


}