package kndroidx.setting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.datastore.DataStoreSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import okio.Path.Companion.toPath
import java.io.File
import java.nio.file.Paths
import kotlin.reflect.KProperty

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

interface Item<T> {
    val flow: Flow<T>
    suspend fun getValue(): T
    suspend fun setValue(value: T)
}

@OptIn(ExperimentalSettingsApi::class, ExperimentalSettingsImplementation::class)
class SettingItem<T>(
    private val setting: DataStoreSettings,
    private val name: String,
    private val defaultValue: T,
) : Item<T> {

    override val flow: Flow<T> = getTypeFlow()

    @Suppress("UNCHECKED_CAST")
    private fun getTypeFlow(): Flow<T> {
        with(setting) {
            return when (defaultValue) {
                is Int -> getIntFlow(name, defaultValue)
                is String -> getStringFlow(name, defaultValue)
                is Float -> getFloatFlow(name, defaultValue)
                is Boolean -> getBooleanFlow(name, defaultValue)
                is Long -> getLongFlow(name, defaultValue)
                else -> throw IllegalArgumentException("Unsupported type")
            } as Flow<T>
        }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun getValue(): T {
        with(setting) {
            return when (defaultValue) {
                is Int -> getInt(name, defaultValue)
                is String -> getString(name, defaultValue)
                is Float -> getFloat(name, defaultValue)
                is Boolean -> getBoolean(name, defaultValue)
                is Long -> getLong(name, defaultValue)
                else -> throw IllegalArgumentException("Unsupported type")
            } as T
        }
    }

    override suspend fun setValue(value: T) {
        setting.apply {
            when (value) {
                is Int -> putInt(name, value)
                is String -> putString(name, value)
                is Float -> putFloat(name, value)
                is Boolean -> putBoolean(name, value)
                is Long -> putLong(name, value)
                else -> throw IllegalArgumentException("Unsupported type")
            }
        }
    }
}

@OptIn(ExperimentalSettingsApi::class, ExperimentalSettingsImplementation::class)
class SettingEnumItem<T>(
    private val setting: DataStoreSettings,
    private val name: String,
    defaultValue: T,
    private val encode: (T) -> String,
    private val decode: (String) -> T,
) : Item<T> {
    private val defaultValue = encode(defaultValue)
    override val flow: Flow<T> = setting
        .getStringFlow(name, this.defaultValue)
        .map {
            decode(it)
        }

    override suspend fun getValue(): T = decode(setting.getString(name, defaultValue))

    override suspend fun setValue(value: T) {
        setting.putString(name, encode(value))
    }
}


//val dataStore by lazy {
//    createDataStore(
//        producePath = {
//            val file = File(getAppDataDir("MiWuX"), dataStoreFileName)
//            file.absolutePath
//        }
//    )
//}

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

internal const val dataStoreFileName = "dice.preferences_pb"