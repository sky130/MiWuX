package com.github.miwu.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DeviceMetadataTest {
    @Test
    fun resolvesKnownMetadataAndFallsBackForUnknownRoom() {
        val metadata = DeviceMetadata(
            icons = mapOf("model" to "https://example.test/icon.png"),
            rooms = mapOf("did" to "客厅"),
        )

        assertEquals("https://example.test/icon.png", metadata.getIcon("model"))
        assertNull(metadata.getIcon("missing"))
        assertEquals("客厅", metadata.getRoom("did"))
        assertEquals("未知位置", metadata.getRoom("missing"))
    }
}
