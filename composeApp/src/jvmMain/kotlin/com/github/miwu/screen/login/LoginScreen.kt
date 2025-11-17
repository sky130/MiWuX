package com.github.miwu.screen.login

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.github.miwu.AppViewModel
import fr.haan.resultat.onSuccess
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import miwu.miot.MiotManager
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen() {
    val appViewModel: AppViewModel = koinViewModel()
    val loginViewModel: LoginViewModel = koinViewModel()
    val miotManager: MiotManager = koinInject()
    LaunchedEffect(Unit) {
        loginViewModel.loadQrCode()
    }
    loginViewModel.qrcode.onSuccess {
        Image(
            painter = rememberQrCodePainter(it),
            contentDescription = null
        )
    }
    loginViewModel.miotUser?.let {
        appViewModel.updateMiotUser(it)
    }
}