package tel.jeelpa.otterlib.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface UserStorage {
    suspend fun saveData(data: String)

    suspend fun loadData(): Flow<String?>

    suspend fun clearData()
}


class UserStore(private val context: Context) : UserStorage {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("userData")
        private val SERIALIZED_DATA_KEY = stringPreferencesKey("tracker_tokens")
    }

    override suspend fun loadData(): Flow<String?> = context.dataStore.data.map { pref ->
        pref[SERIALIZED_DATA_KEY]
    }

    override suspend fun saveData(data: String) {
        context.dataStore.edit { preferences ->
            preferences[SERIALIZED_DATA_KEY] = data
        }
    }

    override suspend fun clearData() {
        context.dataStore.edit { pref ->
            pref.remove(SERIALIZED_DATA_KEY)
        }
    }
}
