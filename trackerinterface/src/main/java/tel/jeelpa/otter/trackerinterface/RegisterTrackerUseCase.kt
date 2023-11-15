package tel.jeelpa.otter.trackerinterface

import tel.jeelpa.otter.trackerinterface.repository.ClientHolder
import tel.jeelpa.otter.trackerinterface.repository.UserStorage

class RegisterTrackerUseCase(
    private val trackerManager: TrackerManager,
    val userStorage: UserStorage
) {
    operator fun invoke(trackerClient: ClientHolder) {
        return trackerManager.registerTracker(trackerClient)
    }

}