package com.github.miwu.route

import androidx.compose.runtime.snapshots.SnapshotStateList

fun SnapshotStateList<Route>.replaceCurrent(next: Route) {
    runCatching {
        removeLast()
        add(next)
    }.onFailure {
        it.printStackTrace()
    }
}