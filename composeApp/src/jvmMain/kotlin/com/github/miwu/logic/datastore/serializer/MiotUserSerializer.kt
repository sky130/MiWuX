package com.github.miwu.logic.datastore.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import miwu.miot.kmp.utils.json
import miwu.miot.model.MiotUser
import java.io.InputStream
import java.io.OutputStream
import java.security.SecureRandom

object MiotUserSerializer : Serializer<MiotUser> {

    override val defaultValue: MiotUser = MiotUser("", "", -1L, "", "", "", "", "")

    override suspend fun readFrom(input: InputStream) = runCatching {
        input.readBytes()
            .decodeToString()
            .let(::decode)
            .injectDeviceID()
    }.getOrElse {
        throw CorruptionException("Unable to read MiotUser", it)
    }

    override suspend fun writeTo(t: MiotUser, output: OutputStream) {
        runCatching {
            t.let(::encode)
                .encodeToByteArray()
                .let(output::write)
        }.onFailure {
            throw CorruptionException("Unable to write MiotUser", it)
        }
    }

    private fun MiotUser.injectDeviceID() = copy(deviceId = "androidId") // TODO

    private fun decode(str: String) = json.decodeFromString<MiotUser>(str)

    private fun encode(user: MiotUser) = json.encodeToString(user)
}