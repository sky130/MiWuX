package com.github.miwu

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.github.miwu.domain.model.LoginState
import com.github.miwu.domain.repository.AccountRepository
import com.github.miwu.route.Route
import com.github.miwu.route.replaceCurrent
import com.github.miwu.screen.device.DeviceControlScreen
import com.github.miwu.screen.device.support.ComposeTranslateHelper
import com.github.miwu.screen.error.ErrorScreen
import com.github.miwu.screen.login.LoginScreen
import com.github.miwu.screen.main.MainScreen
import miwu.compose.SnackHost
import miwu.compose.SnackState
import miwu.compose.basic.MiwuTheme
import miwu.compose.rememberSnackState
import org.koin.compose.koinInject

val LocalRootNavBackStack = compositionLocalOf<SnapshotStateList<Route>> {
    error("No root navigation back stack provided")
}

val LocalGlobalSnackState = compositionLocalOf<SnackState> {
    error("No global snack state provided")
}

@Composable
fun App(accountRepository: AccountRepository = koinInject()) {
    LaunchedEffect(Unit) {
        ComposeTranslateHelper.init()
    }
    MiwuTheme(isDarkMode = false) {
        val snackState = rememberSnackState()
        val backStack = remember { mutableStateListOf<Route>(Route.Blank) }
        LaunchedEffect(accountRepository) {
            accountRepository.loginState.collect { loginState ->
                val route = when (loginState) {
                    LoginState.Loading -> Route.Blank
                    LoginState.Success -> Route.Main
                    LoginState.LoggedOut, is LoginState.Failure -> Route.Login
                    is LoginState.NetworkError -> Route.Error(loginState.message)
                }
                backStack.replaceCurrent(route)
            }
        }

        CompositionLocalProvider(
            LocalRootNavBackStack provides backStack,
            LocalGlobalSnackState provides snackState,
        ) {
            Box(Modifier.fillMaxSize().background(MiwuTheme.colors.background)) {
                NavDisplay(
                    backStack = backStack,
                    onBack = { if (backStack.size > 1) backStack.removeLast() },
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator(),
                    ),
                    entryProvider = entryProvider {
                        entry(Route.Blank) { Box(Modifier.fillMaxSize()) }
                        entry(Route.Login) { LoginScreen() }
                        entry(Route.Main) { MainScreen() }
                        entry<Route.Error> { ErrorScreen(it.message) }
                        entry<Route.Device> { DeviceControlScreen(it.did) }
                    },
                    transitionSpec = {
                        slideInHorizontally(initialOffsetX = { it }) + fadeIn() togetherWith
                            slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
                    },
                    popTransitionSpec = {
                        slideInHorizontally(initialOffsetX = { -it }) + fadeIn() togetherWith
                            slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
                    },
                    modifier = Modifier.fillMaxSize(),
                )
                SnackHost(snackState, Modifier.fillMaxSize())
            }
        }
    }
}
