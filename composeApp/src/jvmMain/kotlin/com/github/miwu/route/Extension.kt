package com.github.miwu.route

import androidx.compose.runtime.snapshots.SnapshotStateList

fun SnapshotStateList<Route>.replaceCurrent(next: Route) {
    if (size == 1 && last() == next) return
    clear()
    add(next)
}
