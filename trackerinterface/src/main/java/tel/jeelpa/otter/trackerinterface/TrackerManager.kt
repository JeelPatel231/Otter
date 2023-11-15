package tel.jeelpa.otter.trackerinterface

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

    fun getCurrentTracker(): Flow<ClientHolder?> = flow {
        trackerStore.getTracker().collect { trackerId ->
            emit(trackers.find { it.uniqueId == trackerId })
        }
    }

    suspend fun setCurrentTracker(trackerClient: ClientHolder) {
        trackerStore.saveTracker(trackerClient.uniqueId)
    }

    fun registerTracker(trackerClient: ClientHolder){
        _registeredTrackers.add(trackerClient)
    }
}
