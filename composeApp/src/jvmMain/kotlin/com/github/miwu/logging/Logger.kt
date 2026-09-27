@file:Suppress("FunctionName")

package com.github.miwu.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory

inline fun <reified T> Logger(): Logger = LoggerFactory.getLogger(T::class.java)

fun Any.Logger(): Logger = LoggerFactory.getLogger(this::class.java)

fun Any.LazyLogger(): Lazy<Logger> = lazy { Logger() }
