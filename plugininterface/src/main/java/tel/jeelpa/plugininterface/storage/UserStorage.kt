package tel.jeelpa.plugininterface.storage

import kotlinx.coroutines.flow.Flow

interface UserStorage {
    suspend fun saveData(data: String)

    suspend fun loadData(): Flow<String?>

    suspend fun clearData()
}
