package tel.jeelpa.otterlib.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserStore(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("userData")
        private val ACCESS_KEY = stringPreferencesKey("access_key")
        private val REFRESH_KEY = stringPreferencesKey("refresh_key")
        private val EXPIRES = longPreferencesKey("expires_in")
    }

    val getAccessToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[ACCESS_KEY]
    }

    suspend fun saveAccessToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_KEY] = token
        }
    }

    suspend fun saveRefreshToken(token:String){
        context.dataStore.edit { preferences ->
            preferences[REFRESH_KEY] = token
        }
    }

    suspend fun saveExpiresIn(expires: Long){
        context.dataStore.edit { preferences ->
            preferences[EXPIRES] = expires
        }
    }

    suspend fun logout() {
        context.dataStore.edit { it.clear() }
    }
}