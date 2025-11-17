package com.github.miwu.logic.setting

import kndroidx.setting.Setting
import kndroidx.setting.createDataStore
import kndroidx.setting.getAppDataDir
import org.koin.dsl.module
import java.io.File

class AppSetting : Setting(
    dataStore = {
        createDataStore(
            producePath = {
                File(getAppDataDir("MiWuX"), "dice.preferences_pb").absolutePath
            }
        )
    }
) {
    val securityToken = string("securityToken", "")
    val serviceToken = string("serviceToken", "")
    val userId = string("userId", "")
    val homeId = long("homeId", 0L)
    val homeUid = long("homeUid", 0L)

}