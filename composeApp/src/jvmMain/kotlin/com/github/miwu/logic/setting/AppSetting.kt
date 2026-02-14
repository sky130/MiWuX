package com.github.miwu.logic.setting

import kndroidx.setting.Setting
import kndroidx.setting.createDataStore
import kndroidx.setting.getAppDataDir
import java.io.File

class AppSetting : Setting(
    {
        createDataStore { File(getAppDataDir("MiWuX"), "miwu.preferences_pb").absolutePath }
    }
) {

    val homeId = long("homeId", 0L)

    val ownerId = long("ownerId", 0L)

    val deviceId = string("deviceId", "")
}