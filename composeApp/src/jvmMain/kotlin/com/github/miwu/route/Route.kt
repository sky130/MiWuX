package com.github.miwu.route

import androidx.compose.runtime.Composable
import com.github.miwu.screen.device.DeviceControlScreen
import com.github.miwu.screen.login.LoginScreen
import com.github.miwu.screen.main.screen.DeviceScreen
import com.github.miwu.screen.main.screen.HomeScreen
import com.github.miwu.screen.main.MainScreen
import com.github.miwu.screen.main.screen.SceneScreen
import com.github.miwu.screen.main.screen.SettingScreen
import miwu.miot.model.MiotUser
import miwu.miot.model.miot.MiotDevice

sealed interface Route {
    @Composable
    fun Content() = Unit

    data object Blank : Route

    data class Error(val msg: String, val cause: Throwable? = null): Route {
        @Composable
        override fun Content() {
            // TODO
        }
    }

    data object Login : Route {
        @Composable
        override fun Content() {
            LoginScreen()
        }
    }

    data object Main: Route {

        val entities = listOf(Home, Device, Scene, Setting)

        @Composable
        override fun Content() {
            MainScreen()
        }

        data object Home : Route {
            @Composable
            override fun Content() {
                HomeScreen()
            }
        }

        data object Device : Route {
            @Composable
            override fun Content() {
                DeviceScreen()
            }
        }

        data object Scene : Route {
            @Composable
            override fun Content() {
                SceneScreen()
            }
        }

        data object Setting : Route {
            @Composable
            override fun Content() {
                SettingScreen()
            }
        }
    }

    data class Device(val miotUser: MiotUser, val miotDevice: MiotDevice) : Route {
        @Composable
        override fun Content() {
            DeviceControlScreen(miotUser, miotDevice)
        }
    }
}