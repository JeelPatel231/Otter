package tel.jeelpa.otter.trackerinterface

import kotlinx.coroutines.flow.Flow
import tel.jeelpa.otter.trackerinterface.repository.ClientHolder


interface TrackerStore {
    suspend fun clearTracker()

    suspend fun saveTracker(trackerId: String)

    fun getTracker(): Flow<String?>
}

class TrackerManager {
    private val _registeredTrackers = mutableListOf<ClientHolder>()

    val trackers get() = _registeredTrackers.toList()

    // SHOULD ONLY BE CALLED IN HILT MODULE, ONCE
    fun getTracker(id: String): ClientHolder {
//        val trackerId = trackerStore.getTracker().first()
        return trackers.find { it.uniqueId.equals(id, true) }
                ?: throw IllegalStateException("The Preferred client is not Registered.")
        }

//    suspend fun setCurrentTracker(trackerClient: ClientHolder) {
//        trackerStore.saveTracker(trackerClient.uniqueId)
//    }

    fun registerTracker(trackerClient: ClientHolder){
        // TODO : remove this when app has a setup screen
        _registeredTrackers.add(trackerClient)
    }
}
