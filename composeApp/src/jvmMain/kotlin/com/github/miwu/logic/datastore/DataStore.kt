package com.github.miwu.logic.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import com.github.miwu.logic.datastore.serializer.MiotUserSerializer
import kndroidx.setting.getAppDataDir
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import miwu.miot.model.MiotUser
import org.koin.dsl.module
import java.io.File

typealias MiotUserDataStore = DataStore<MiotUser>

private val miotUserDataStore: MiotUserDataStore by lazy {
    DataStoreFactory.create(
        MiotUserSerializer,
        null,
        emptyList(),
        CoroutineScope(Dispatchers.IO + SupervisorJob())
    ) {
        File(getAppDataDir("MiWuX"), "miwu_user_info")
    }
}

suspend fun MiotUserDataStore.isLogin(): Boolean = runCatching {
    data.first().isLogin()
}.getOrNull() ?: false

fun MiotUser.isLogin(): Boolean = run {
    listOf(
        userId,
        cUserId,
        passToken,
    ).all(String::isNotEmpty)
}

val dataStoreModule = module {
    single<MiotUserDataStore> { miotUserDataStore }
}