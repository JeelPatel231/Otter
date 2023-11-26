package tel.jeelpa.plugininterface.tracker

import kotlinx.coroutines.flow.Flow
import tel.jeelpa.plugininterface.tracker.repository.ClientHolder


interface TrackerStore {
    suspend fun clearTracker()

    suspend fun saveTracker(trackerId: String)

    fun getTracker(): Flow<String?>
}

class TrackerManager {
    private val _registeredTrackers = mutableSetOf<ClientHolder>()

    val trackers get() = _registeredTrackers.toSet()

    // SHOULD ONLY BE CALLED IN HILT MODULE, ONCE
    fun getTracker(id: String): ClientHolder {
        return trackers.find { it.uniqueId.equals(id, true) }
                ?: throw IllegalStateException("The Preferred client is not Registered.")
        }

    fun registerTracker(trackerClient: ClientHolder){
        _registeredTrackers.add(trackerClient)
    }
}
