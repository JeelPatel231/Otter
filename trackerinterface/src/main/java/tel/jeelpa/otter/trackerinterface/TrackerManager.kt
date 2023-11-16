package tel.jeelpa.otter.trackerinterface

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import tel.jeelpa.otter.trackerinterface.repository.ClientHolder


interface TrackerStore {
    suspend fun saveTracker(trackerId: String)

    fun getTracker(): Flow<String?>
}

class TrackerManager(
    private val trackerStore: TrackerStore
) {
    private val _registeredTrackers = mutableListOf<ClientHolder>()

    val trackers get() = _registeredTrackers.toList()

    // SHOULD ONLY BE CALLED IN HILT MODULE, ONCE
    suspend fun getCurrentTracker(): ClientHolder {
        val trackerId = trackerStore.getTracker().first()
        return trackers.find { it.uniqueId == trackerId }
                ?: throw IllegalStateException("The Preferred client is not Registered.")
        }

    suspend fun setCurrentTracker(trackerClient: ClientHolder) {
        trackerStore.saveTracker(trackerClient.uniqueId)
    }

    fun registerTracker(trackerClient: ClientHolder){
        // TODO : remove this when app has a setup screen
        if (trackers.isEmpty()) runBlocking { setCurrentTracker(trackerClient) }
        _registeredTrackers.add(trackerClient)
    }
}
