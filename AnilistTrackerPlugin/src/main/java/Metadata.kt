
import tel.jeelpa.anilisttrackerplugin.data.ClientHolderImpl
import tel.jeelpa.otter.trackerinterface.RegisterTrackerUseCase
import tel.jeelpa.otter.trackerinterface.repository.UserStorage

class TrackerMetadata(
    userStorage: UserStorage,
    registerTrackerUseCase: RegisterTrackerUseCase,
) {
    init {
        registerTrackerUseCase(ClientHolderImpl(userStorage))
    }
}