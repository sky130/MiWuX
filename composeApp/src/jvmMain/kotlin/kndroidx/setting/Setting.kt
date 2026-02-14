package kndroidx.setting

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.datastore.DataStoreSettings
import okio.Path.Companion.toPath
import java.io.File
import java.nio.file.Paths

@OptIn(ExperimentalSettingsApi::class, ExperimentalSettingsImplementation::class)
open class Setting(dataStore: () -> DataStore<Preferences>) {
    val dataStore by lazy { dataStore() }
    val setting by lazy { DataStoreSettings(this.dataStore) }

    fun int(name: String, defaultValue: Int) = SettingItem(setting, name, defaultValue)

    fun string(name: String, defaultValue: String) = SettingItem(setting, name, defaultValue)

    fun long(name: String, defaultValue: Long) = SettingItem(setting, name, defaultValue)

    fun float(name: String, defaultValue: Float) = SettingItem(setting, name, defaultValue)

    fun boolean(name: String, defaultValue: Boolean) = SettingItem(setting, name, defaultValue)

    fun <T> enum(
        name: String,
        defaultValue: T,
        encode: (T) -> String,
        decode: (String) -> T,
    ) = SettingEnumItem(setting, name, defaultValue, encode, decode)
}

fun getAppDataDir(appName: String): String {
    val os = System.getProperty("os.name").lowercase()
    val userHome = System.getProperty("user.home")
    return when {
        os.contains("win") -> Paths.get(System.getenv("APPDATA"), appName).toString()
        os.contains("mac") -> Paths.get(userHome, "Library", "Application Support", appName).toString()
        else -> Paths.get(userHome, ".config", appName).toString()
    }.apply {
        mkdir()
    }
}

internal fun String.mkdir() {
    val file = File(this)
    if (!file.isDirectory) file.mkdir()
}

internal fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(produceFile = { producePath().toPath() })
