package com.github.miwu.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.miwu.LocalGlobalSnackState
import fr.haan.resultat.onSuccess
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import miwu.common.resources.Res
import miwu.common.resources.ic_miwu_round
import miwu.compose.Text
import miwu.compose.Title
import miwu.compose.basic.MiwuTheme
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = koinViewModel()
) {
    val snackState = LocalGlobalSnackState.current
    LaunchedEffect(Unit) {
        loginViewModel.loadQrCode()
    }
    LaunchedEffect(Unit) {
        loginViewModel.event.collect { event ->
            when (event) {
                is LoginViewModel.Event.LoginFailure -> {
                    snackState.showMessage(event.e.message ?: "登录失败")
                }

                is LoginViewModel.Event.LoginSuccess -> {
                    snackState.showMessage("登录成功")
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
            var counter by remember { mutableStateOf(0) }
            Image(
                painterResource(Res.drawable.ic_miwu_round),
                null,
                modifier = Modifier.size(72.dp).clickable {
                    counter++
                    snackState.showMessage("Hello $counter")
                }
            )
            Title {
                Text("扫码登录")
            }
            Text("使用小米账号登录至米屋")
            loginViewModel.qrcode.onSuccess {
                Box(Modifier.border(2.dp, MiwuTheme.colors.border, RoundedCornerShape(10.dp))) {
                    Image(
                        painter = rememberQrCodePainter(it),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(15.dp)
                            .size(200.dp)
                    )
                }
            }
            FlowRow {
                Text("登录后默认已同意")
                Text("免责声明", color = MiwuTheme.colors.primary)
            }
            Spacer(Modifier.height(20.dp))
        }

    }


}