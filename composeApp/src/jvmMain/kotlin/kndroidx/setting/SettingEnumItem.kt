package kndroidx.setting

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.datastore.DataStoreSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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