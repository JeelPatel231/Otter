package tel.jeelpa.otter.factories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import tel.jeelpa.otterlib.repository.TrackerClient
import tel.jeelpa.otterlib.store.TrackerStore

class TrackerManager(
    private val trackerStore: TrackerStore
) {
    private val _registeredTrackers = mutableListOf<TrackerClient>()

    val trackers get() = _registeredTrackers.toList()

    fun getCurrentTracker(): Flow<TrackerClient?> = flow {
        trackerStore.getTracker().collect { trackerId ->
            emit(trackers.find { it.uniqueId == trackerId })
        }
    }

    suspend fun setCurrentTracker(trackerClient: TrackerClient) {
        trackerStore.saveTracker(trackerClient.uniqueId)
    }

    fun registerTracker(trackerClient: TrackerClient){
        _registeredTrackers.add(trackerClient)
    }
}
