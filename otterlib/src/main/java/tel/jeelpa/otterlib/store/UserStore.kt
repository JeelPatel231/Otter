package tel.jeelpa.otterlib.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


enum class TrackerService {
    ANILIST,
//    MAL
}

class UserStore(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("userData")
        private val SERIALIZED_DATA_KEY = stringPreferencesKey("tracker_tokens")
        private val SERVICE = stringPreferencesKey("service_name")
    }

    val trackerData: Flow<String?> = context.dataStore.data.map { pref ->
        pref[SERIALIZED_DATA_KEY]
    }

    val getServiceName: Flow<TrackerService> = context.dataStore.data.map { pref ->
        val serviceName = pref[SERVICE] ?: TrackerService.ANILIST.name
        TrackerService.valueOf(serviceName)
    }

    suspend fun changeService(service: TrackerService) {
        logout()
        context.dataStore.edit { pref ->
            pref[SERVICE] = service.name
        }
    }

    suspend fun saveTrackerData(serialized: String) {
        context.dataStore.edit { preferences ->
            preferences[SERIALIZED_DATA_KEY] = serialized
        }
    }

    suspend fun logout() {
        context.dataStore.edit { pref ->
            pref.remove(SERIALIZED_DATA_KEY)
        }
    }
}