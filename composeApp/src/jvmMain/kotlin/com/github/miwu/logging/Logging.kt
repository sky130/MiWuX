package com.github.miwu.logging

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger as KermitLogger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.SystemWriter
import co.touchlab.kermit.io.RollingFileLogWriter
import co.touchlab.kermit.io.RollingFileLogWriterConfig
import java.io.File
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean
import kndroidx.setting.getAppDataDir
import kotlinx.io.files.Path

object Logging {
    private const val APP_NAME = "MiWuX"
    private const val LOG_DIRECTORY_NAME = "logs"
    internal const val LOG_FILE_NAME = "miwux"
    internal const val LOG_LEVEL_PROPERTY = "miwux.log.level"
    internal const val DEFAULT_ROLL_SIZE = 10L * 1024 * 1024
    internal const val DEFAULT_MAX_LOG_FILES = 5

    private val initialized = AtomicBoolean(false)

    fun initialize() {
        if (!initialized.compareAndSet(false, true)) return

        val writers = mutableListOf<LogWriter>(SystemWriter())
        var fileLoggingFailure: Throwable? = null

        try {
            val logDirectory = File(getAppDataDir(APP_NAME), LOG_DIRECTORY_NAME)
            check(logDirectory.mkdirs() || logDirectory.isDirectory) {
                "Unable to create the application log directory"
            }
            writers += createFileLogWriter(logDirectory)
        } catch (error: Exception) {
            fileLoggingFailure = error
        }

        configure(configuredMinimumSeverity(), writers)

        val logger = kermitLogger("Logging")
        fileLoggingFailure?.let { logger.w("File logging is unavailable", it) }
        logger.i("Logging initialized; fileLogging=${fileLoggingFailure == null}")
    }

    fun kermitLogger(tag: String): KermitLogger = KermitLogger.withTag(tag)

    internal fun configure(minSeverity: Severity, writers: List<LogWriter>) {
        KermitLogger.setLogWriters(writers)
        KermitLogger.setMinSeverity(minSeverity)
    }

    internal fun configuredMinimumSeverity(): Severity =
        resolveMinimumSeverity(System.getProperty(LOG_LEVEL_PROPERTY))

    internal fun resolveMinimumSeverity(value: String?): Severity = when (value?.uppercase(Locale.ROOT)) {
        "TRACE" -> Severity.Verbose
        "DEBUG" -> Severity.Debug
        "WARN" -> Severity.Warn
        "ERROR" -> Severity.Error
        "INFO", null -> Severity.Info
        else -> Severity.Info
    }

    internal fun createFileLogWriter(
        logDirectory: File,
        rollOnSize: Long = DEFAULT_ROLL_SIZE,
        maxLogFiles: Int = DEFAULT_MAX_LOG_FILES,
    ): RollingFileLogWriter = RollingFileLogWriter(
        RollingFileLogWriterConfig(
            logFileName = LOG_FILE_NAME,
            logFilePath = Path(logDirectory.absolutePath),
            rollOnSize = rollOnSize,
            maxLogFiles = maxLogFiles,
        ),
    )
}
