package com.github.miwu.route

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RouteTest {
    @Test
    fun routesRoundTripThroughSerialization() {
        val routes = listOf<Route>(
            Route.Blank,
            Route.Login,
            Route.Main,
            Route.Error("network unavailable"),
            Route.Device("123456"),
        )

        routes.forEach { route ->
            val encoded = Json.encodeToString(Route.serializer(), route)
            assertEquals(route, Json.decodeFromString(Route.serializer(), encoded))
        }
    }

    @Test
    fun deviceRouteContainsOnlyDeviceId() {
        assertEquals(Route.Device("device-id"), Route.Device("device-id"))
    }
}
