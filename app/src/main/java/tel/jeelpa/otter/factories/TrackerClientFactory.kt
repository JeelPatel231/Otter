package tel.jeelpa.otter.factories

import kotlinx.coroutines.flow.first
import okhttp3.OkHttpClient
import tel.jeelpa.otterlib.data.TrackerClientImpl
import tel.jeelpa.otterlib.models.AnilistData
import tel.jeelpa.otterlib.repository.TrackerClient
import tel.jeelpa.otterlib.store.TrackerService
import tel.jeelpa.otterlib.store.UserStore

class TrackerClientFactory(
    private val userStore: UserStore,
    httpClient: OkHttpClient,
    anilistData: AnilistData,
){
    private val anilistClient = TrackerClientImpl(
        anilistData,
        httpClient,
        userStore,
    )

    suspend operator fun invoke() : TrackerClient {
        return when(userStore.getServiceName.first()) {
            TrackerService.ANILIST -> anilistClient
            else -> throw IllegalStateException()
        }
    }
}
