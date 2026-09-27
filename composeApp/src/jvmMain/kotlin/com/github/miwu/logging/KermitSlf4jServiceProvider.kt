package com.github.miwu.logging

import java.util.concurrent.ConcurrentHashMap
import org.slf4j.ILoggerFactory
import org.slf4j.IMarkerFactory
import org.slf4j.Logger
import org.slf4j.helpers.BasicMDCAdapter
import org.slf4j.helpers.BasicMarkerFactory
import org.slf4j.spi.MDCAdapter
import org.slf4j.spi.SLF4JServiceProvider

class KermitSlf4jServiceProvider : SLF4JServiceProvider {
    private val loggerFactory = KermitLoggerFactory()
    private val markerFactory = BasicMarkerFactory()
    private val mdcAdapter = BasicMDCAdapter()

    override fun getLoggerFactory(): ILoggerFactory = loggerFactory

    override fun getMarkerFactory(): IMarkerFactory = markerFactory

    override fun getMDCAdapter(): MDCAdapter = mdcAdapter

    override fun getRequestedApiVersion(): String = "2.0.99"

    override fun initialize() = Unit
}

private class KermitLoggerFactory : ILoggerFactory {
    private val loggers = ConcurrentHashMap<String, Logger>()

    override fun getLogger(name: String): Logger = loggers.computeIfAbsent(name, ::KermitSlf4jLogger)
}
