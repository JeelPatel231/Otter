package tel.jeelpa.otterlib.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


//enum class TrackerService {
//    ANILIST,
//    MAL
//}

interface TrackerStore {
    suspend fun saveTracker(trackerId: String)

    fun getTracker(): Flow<String?>
}

class TrackerStoreImpl(private val context: Context) : TrackerStore {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("trackerData")
        private val TRACKER_ID_KEY = stringPreferencesKey("tracker_id")
    }

    override fun getTracker(): Flow<String?> = context.dataStore.data.map { pref ->
        pref[TRACKER_ID_KEY]
    }

    override suspend fun saveTracker(trackerId: String) {
        context.dataStore.edit { preferences ->
            preferences[TRACKER_ID_KEY] = trackerId
        }
    }

}