package com.github.miwu.data.account.local.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import miwu.miot.model.MiotUser
import java.io.InputStream
import java.io.OutputStream

object MiotUserSerializer : Serializer<MiotUser> {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    override val defaultValue: MiotUser = MiotUser("", "", -1L, "", "", "", "", "")

    override suspend fun readFrom(input: InputStream): MiotUser = try {
        json.decodeFromString(input.readBytes().decodeToString())
    } catch (error: Throwable) {
        throw CorruptionException("Unable to read MiotUser", error)
    }

    override suspend fun writeTo(t: MiotUser, output: OutputStream): Unit =
        withContext(Dispatchers.IO) {
            runCatching {
                output.write(json.encodeToString(t).encodeToByteArray())
            }.recoverCatching {
                throw CorruptionException("Unable to write MiotUser", it)
            }
        }
}
