package com.github.miwu.logging

import co.touchlab.kermit.ExperimentalKermitApi
import co.touchlab.kermit.Severity
import co.touchlab.kermit.SystemWriter
import co.touchlab.kermit.TestLogWriter
import java.nio.file.Files
import java.util.ServiceLoader
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.test.assertTrue
import org.slf4j.LoggerFactory
import org.slf4j.spi.SLF4JServiceProvider

@OptIn(ExperimentalKermitApi::class)
class LoggingTest {
    @AfterTest
    fun restoreLogging() {
        Logging.configure(Severity.Info, listOf(SystemWriter()))
    }

    @Test
    fun slf4jDiscoversKermitProviderAndCachesLoggers() {
        val providers = ServiceLoader.load(SLF4JServiceProvider::class.java).toList()
        val first = LoggerFactory.getLogger("provider-test")
        val second = LoggerFactory.getLogger("provider-test")

        assertTrue(providers.any { it is KermitSlf4jServiceProvider })
        assertIs<KermitSlf4jLogger>(first)
        assertSame(first, second)
    }

    @Test
    fun slf4jFormatsArgumentsEscapesAndTrailingThrowable() {
        val writer = TestLogWriter(Severity.Verbose)
        Logging.configure(Severity.Verbose, listOf(writer))
        val logger = LoggerFactory.getLogger("format-test")
        val error = IllegalStateException("failed")

        logger.info("Loaded {} of {} homes", 2, 3)
        logger.info("Escaped \\{} placeholder and {}", "value")
        logger.error("Refresh {} failed", "homes", error)

        assertEquals("Loaded 2 of 3 homes", writer.logs[0].message)
        assertEquals("Escaped {} placeholder and value", writer.logs[1].message)
        assertEquals("Refresh homes failed", writer.logs[2].message)
        assertSame(error, writer.logs[2].throwable)
    }

    @Test
    fun mapsEverySlf4jLevelToKermit() {
        val writer = TestLogWriter(Severity.Verbose)
        Logging.configure(Severity.Verbose, listOf(writer))
        val logger = LoggerFactory.getLogger("level-test")

        logger.trace("trace")
        logger.debug("debug")
        logger.info("info")
        logger.warn("warn")
        logger.error("error")

        assertEquals(
            listOf(Severity.Verbose, Severity.Debug, Severity.Info, Severity.Warn, Severity.Error),
            writer.logs.map { it.severity },
        )
    }

    @Test
    fun honorsMinimumSeverity() {
        val writer = TestLogWriter(Severity.Verbose)
        Logging.configure(Severity.Warn, listOf(writer))
        val logger = LoggerFactory.getLogger("minimum-level-test")

        logger.info("ignored")
        logger.warn("included")

        assertEquals(listOf(Severity.Warn), writer.logs.map { it.severity })
    }

    @Test
    fun parsesConfiguredLevelAndFallsBackToInfo() {
        assertEquals(Severity.Info, Logging.resolveMinimumSeverity(null))
        assertEquals(Severity.Verbose, Logging.resolveMinimumSeverity("trace"))
        assertEquals(Severity.Debug, Logging.resolveMinimumSeverity("DEBUG"))
        assertEquals(Severity.Info, Logging.resolveMinimumSeverity("INFO"))
        assertEquals(Severity.Warn, Logging.resolveMinimumSeverity("WARN"))
        assertEquals(Severity.Error, Logging.resolveMinimumSeverity("ERROR"))
        assertEquals(Severity.Info, Logging.resolveMinimumSeverity("invalid"))
    }

    @Test
    fun readsMinimumSeverityFromSystemProperty() {
        val original = System.getProperty(Logging.LOG_LEVEL_PROPERTY)
        try {
            System.setProperty(Logging.LOG_LEVEL_PROPERTY, "TRACE")
            assertEquals(Severity.Verbose, Logging.configuredMinimumSeverity())

            System.setProperty(Logging.LOG_LEVEL_PROPERTY, "not-a-level")
            assertEquals(Severity.Info, Logging.configuredMinimumSeverity())
        } finally {
            if (original == null) {
                System.clearProperty(Logging.LOG_LEVEL_PROPERTY)
            } else {
                System.setProperty(Logging.LOG_LEVEL_PROPERTY, original)
            }
        }
    }

    @Test
    fun createsAndRotatesLogFiles() {
        val directory = Files.createTempDirectory("miwux-logging-test").toFile()
        try {
            val writer = Logging.createFileLogWriter(directory, rollOnSize = 128, maxLogFiles = 3)

            repeat(20) { index ->
                writer.log(Severity.Info, "entry-$index ${"x".repeat(160)}", "test", null)
            }

            waitUntil {
                directory.resolve("miwux.log").isFile && directory.resolve("miwux-2.log").isFile
            }
            val files = directory.listFiles { file -> file.extension == "log" }.orEmpty()
            assertTrue(directory.resolve("miwux.log").isFile)
            assertTrue(files.size <= 3)
            assertTrue(files.all { it.name != "miwux.log.log" })
        } finally {
            directory.deleteRecursively()
        }
    }

    private fun waitUntil(condition: () -> Boolean) {
        val deadline = System.nanoTime() + 5_000_000_000
        while (!condition() && System.nanoTime() < deadline) {
            Thread.sleep(20)
        }
        assertTrue(condition())
    }
}
