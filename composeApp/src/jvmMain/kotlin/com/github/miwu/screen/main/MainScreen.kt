package com.github.miwu.screen.main


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.miwu.screen.main.viewModel.MainViewModel
import miwu.compose.DropdownMenu
import miwu.compose.DropdownMenuItem
import miwu.compose.basic.MiwuTheme
import miwu.compose.rememberDropdownMenuState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen(viewModel: MainViewModel = koinViewModel()) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MiwuTheme.colors.background)
    ) {
        val dropdownMenuState = rememberDropdownMenuState()
        Button({ dropdownMenuState.toggle() }) {}
        DropdownMenu(dropdownMenuState, Modifier.padding(top = 10.dp, start = 5.dp)) {
            DropdownMenuItem(true, "Option 1", "Option 1") {
                dropdownMenuState.collapse()
            }
            DropdownMenuItem(false, "Option 1", "Option 1") {
                dropdownMenuState.collapse()
            }
            DropdownMenuItem(false, "Option 1", "Option 1") {
                dropdownMenuState.collapse()
            }
        }
    }
}