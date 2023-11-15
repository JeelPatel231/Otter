
import tel.jeelpa.anilisttrackerplugin.data.ClientHolderImpl
import tel.jeelpa.otter.trackerinterface.RegisterTrackerUseCase

class TrackerMetadata(
    registerTrackerUseCase: RegisterTrackerUseCase,
) {
    init {
        val anilistTrackerClient = ClientHolderImpl(registerTrackerUseCase.userStorage)
        registerTrackerUseCase.invoke(anilistTrackerClient)
    }
}