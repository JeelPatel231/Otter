package tel.jeelpa.otter.models

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


open class StoreEntry<TData>(
    protected val dataStore: DataStore<Preferences>,
    protected val key: Preferences.Key<TData>,
) {
    open fun get(): Flow<TData?> =
        dataStore.data.map { pref ->
            pref[key]
        }

    suspend fun set(data: TData) =
        dataStore.edit { preferences ->
            preferences[key] = data
        }

    suspend fun clear() = dataStore.edit { it.clear() }
}

class StoreEntryDefault<TData>(
    dataStore: DataStore<Preferences>,
    key: Preferences.Key<TData>,
    private val defaultValue: TData,
) : StoreEntry<TData>(dataStore, key){

    override fun get(): Flow<TData> =
        dataStore.data.map { pref -> pref[key] ?: defaultValue }
}


class SettingsStore(
    private val context: Context
) {
    companion object {
        private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore("SettingsStore")
        // keys
        private val LIVE_REFRESH_KEY = booleanPreferencesKey("live_refresh")
    }

    val liveRefresh = StoreEntryDefault(context.settingsDataStore, LIVE_REFRESH_KEY, false)

}