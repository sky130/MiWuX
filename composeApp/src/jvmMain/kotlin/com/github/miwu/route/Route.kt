package com.github.miwu.route

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {
    @Serializable
    data object Blank : Route

    @Serializable
    data object Login : Route

    @Serializable
    data object Main : Route

    @Serializable
    data class Error(val message: String) : Route

    @Serializable
    data class Device(val did: String) : Route
}
