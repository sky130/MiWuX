package com.github.miwu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.github.miwu.koin.settingModule
import com.github.miwu.koin.viewModelModule
import com.github.miwu.logic.setting.AppSetting
import com.github.miwu.screen.device.ComposeTranslateHelper
import com.github.miwu.screen.device.Device
import com.github.miwu.screen.device.DeviceControlScreen
import com.github.miwu.screen.login.LoginScreen
import com.github.miwu.screen.main.MainScreen
import miwu.ui.MiwuTheme
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import miwu.miot.model.MiotUser
import miwu.miot.utils.getRandomDeviceId
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

val LocalRootNavController = compositionLocalOf<NavHostController> {
    error("No LocalNavController provided")
}

@OptIn(ExperimentalSettingsApi::class, ExperimentalSettingsImplementation::class)
fun main() = application {
    KoinApplication(application = {
        modules(
            miwu.miot.normalModule,
            miwu.miot.singleModule,
            viewModelModule,
            settingModule
        )
    }) {
        Window(
            onCloseRequest = ::exitApplication,
            title = "MiWuX",
        ) {
            val appViewModel: AppViewModel = koinViewModel()
            val appSetting: AppSetting = koinInject()
            LaunchedEffect(Unit) {
                ComposeTranslateHelper.init()
                val miotUser = MiotUser(
                    userId = appSetting.userId.getValue(),
                    securityToken = appSetting.securityToken.getValue(),
                    serviceToken = appSetting.serviceToken.getValue(),
                    deviceId = getRandomDeviceId()
                )
                appViewModel.updateMiotUser(miotUser)
            }
            val miotUser = appViewModel.miotUser
            val navController = rememberNavController()
            MiwuTheme(isDarkMode = false) {
                CompositionLocalProvider(LocalRootNavController provides navController) {
                    val controller = LocalRootNavController.current
                    Box(modifier = Modifier.fillMaxSize()) {
                        NavHost(
                            navController = controller,
                            startDestination = "blank",
                            modifier = Modifier.fillMaxSize()
                        ) {
                            composable("login") { LoginScreen() }
                            composable("main") { MainScreen() }
                            composable<Device> { device ->
                                DeviceControlScreen(device.toRoute<Device>().device)
                            }
                            composable("blank") { }
                        }
                    }
                }
                if (miotUser != null) {
                    if (miotUser.userId.isBlank() || !appViewModel.isTokenExpired) {
                        navController.navigate("login")
                    } else {
                        navController.navigate("main")
                    }
                }
            }
        }
    }
}