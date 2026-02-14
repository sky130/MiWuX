package kndroidx.setting

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.datastore.DataStoreSettings
import kotlinx.coroutines.flow.Flow

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