package com.github.miwu.di

import androidx.datastore.core.DataStoreFactory
import com.github.miwu.data.account.local.MiotUserDataStore
import com.github.miwu.data.account.local.serializer.MiotUserSerializer
import java.io.File
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import miwu.dispatchers.IoDispatcher
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton
import kndroidx.setting.getAppDataDir

@Module
class DataModule {
    @Singleton
    fun miotUserDataStore(
        @AppScope applicationScope: CoroutineScope,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): MiotUserDataStore = DataStoreFactory.create(
        MiotUserSerializer,
        null,
        emptyList(),
        CoroutineScope(applicationScope.coroutineContext + ioDispatcher),
    ) {
        File(getAppDataDir(APP_NAME), USER_DATA_FILE)
    }

    private companion object {
        const val APP_NAME = "MiWuX"
        const val USER_DATA_FILE = "miwu_user_info"
    }
}
