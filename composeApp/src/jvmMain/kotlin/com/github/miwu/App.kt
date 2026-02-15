package com.github.miwu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.github.miwu.logic.datastore.MiotUserDataStore
import com.github.miwu.logic.repository.AppRepository
import com.github.miwu.logic.state.LoginState
import com.github.miwu.route.Route
import com.github.miwu.route.replaceCurrent
import com.github.miwu.screen.device.ComposeTranslateHelper
import miwu.compose.SnackHost
import miwu.compose.SnackState
import miwu.compose.rememberSnackState
import miwu.compose.basic.MiwuTheme
import org.koin.compose.koinInject

val LocalRootNavBackStack = compositionLocalOf<SnapshotStateList<Route>> {
    error("No LocalNavController provided")
}

val LocalGlobalSnackState = compositionLocalOf<SnackState> {
    error("No SnackState provided")
}

@Composable
fun App(
    miotUserDataStore: MiotUserDataStore = koinInject(),
    appRepository: AppRepository = koinInject(),
) {
    LaunchedEffect(Unit) {
        ComposeTranslateHelper.init()
    }
    MiwuTheme(isDarkMode = false) {
        val snackState = rememberSnackState()
        val backStack: SnapshotStateList<Route> = remember { mutableStateListOf(Route.Blank) }
        LaunchedEffect(Unit) {
            appRepository.loginStatus.collect { loginState ->
                when (loginState) {
                    LoginState.Loading -> Route.Blank
                    LoginState.Success -> Route.Main
                    is LoginState.Failure -> Route.Login
                    is LoginState.NetworkError -> Route.Error(loginState.message)
                }.also {
                    backStack.replaceCurrent(it)
                }
            }
        }
        CompositionLocalProvider(
            LocalRootNavBackStack provides backStack,
            LocalGlobalSnackState provides snackState
        ) {
            Box(Modifier.fillMaxSize().background(MiwuTheme.colors.background)) {
                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLast() },
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator()
                    ),
                    modifier = Modifier.fillMaxSize()
                ) { key ->
                    NavEntry(key) { key.Content() }
                }
                SnackHost(snackState, Modifier.fillMaxSize())
            }
        }
    }
}