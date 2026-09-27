package com.github.miwu.data.account.local.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import java.io.InputStream
import java.io.OutputStream
import kotlinx.serialization.encodeToString
import miwu.miot.kmp.utils.json
import miwu.miot.model.MiotUser

object MiotUserSerializer : Serializer<MiotUser> {
    override val defaultValue: MiotUser = MiotUser("", "", -1L, "", "", "", "", "")

    override suspend fun readFrom(input: InputStream): MiotUser = try {
        json.decodeFromString(input.readBytes().decodeToString())
    } catch (error: Throwable) {
        throw CorruptionException("Unable to read MiotUser", error)
    }

    override suspend fun writeTo(t: MiotUser, output: OutputStream) {
        try {
            output.write(json.encodeToString(t).encodeToByteArray())
        } catch (error: Throwable) {
            throw CorruptionException("Unable to write MiotUser", error)
        }
    }
}
