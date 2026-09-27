package com.github.miwu.data.settings

import java.io.File
import kndroidx.setting.Setting
import kndroidx.setting.createDataStore
import kndroidx.setting.getAppDataDir
import org.koin.core.annotation.Singleton

@Singleton
class AppSettings : Setting(
    {
        createDataStore {
            File(getAppDataDir("MiWuX"), "miwu.preferences_pb").absolutePath
        }
    }
) {
    val homeId = long("homeId", 0L)
    val ownerId = long("ownerId", 0L)
    val deviceId = string("deviceId", "")
}
