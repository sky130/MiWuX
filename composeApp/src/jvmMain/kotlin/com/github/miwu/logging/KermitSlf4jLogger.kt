package com.github.miwu.logging

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import org.slf4j.Marker
import org.slf4j.event.Level
import org.slf4j.helpers.LegacyAbstractLogger
import org.slf4j.helpers.MessageFormatter

internal class KermitSlf4jLogger(
    loggerName: String,
    private val kermitLogger: Logger = Logging.kermitLogger(loggerName),
) : LegacyAbstractLogger() {
    init {
        name = loggerName
    }

    override fun getFullyQualifiedCallerName(): String = javaClass.name

    override fun isTraceEnabled(): Boolean = isEnabled(Severity.Verbose)

    override fun isDebugEnabled(): Boolean = isEnabled(Severity.Debug)

    override fun isInfoEnabled(): Boolean = isEnabled(Severity.Info)

    override fun isWarnEnabled(): Boolean = isEnabled(Severity.Warn)

    override fun isErrorEnabled(): Boolean = isEnabled(Severity.Error)

    override fun handleNormalizedLoggingCall(
        level: Level,
        marker: Marker?,
        messagePattern: String?,
        arguments: Array<out Any?>?,
        throwable: Throwable?,
    ) {
        val message = arguments
            ?.let { MessageFormatter.basicArrayFormat(messagePattern, it) }
            ?: messagePattern.orEmpty()
        val severity = level.toKermitSeverity()
        kermitLogger.log(severity, name, throwable, message)
    }

    private fun isEnabled(severity: Severity): Boolean =
        kermitLogger.config.minSeverity <= severity &&
            kermitLogger.config.logWriterList.any { it.isLoggable(name, severity) }

    private fun Level.toKermitSeverity(): Severity = when (this) {
        Level.TRACE -> Severity.Verbose
        Level.DEBUG -> Severity.Debug
        Level.INFO -> Severity.Info
        Level.WARN -> Severity.Warn
        Level.ERROR -> Severity.Error
    }
}
