package tel.jeelpa.otter.trackerinterface

import tel.jeelpa.otter.trackerinterface.repository.ClientHolder

class RegisterTrackerUseCase(
    private val trackerManager: TrackerManager,
) {
    operator fun invoke(trackerClient: ClientHolder) {
        return trackerManager.registerTracker(trackerClient)
    }

}