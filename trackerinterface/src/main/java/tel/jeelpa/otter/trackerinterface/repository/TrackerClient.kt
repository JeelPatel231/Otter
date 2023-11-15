package tel.jeelpa.otter.trackerinterface.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import tel.jeelpa.otter.trackerinterface.models.AppMediaListStatus
import tel.jeelpa.otter.trackerinterface.models.MediaCardData
import tel.jeelpa.otter.trackerinterface.models.User

interface TrackerClient {
    val loginUri: String

    fun isLoggedIn() : Flow<Boolean>

    suspend fun login(callbackUri: Uri)

    suspend fun logout()

    suspend fun getUser(): User

    suspend fun updateMedia(id: Int, score: Int, mediaListStatus: AppMediaListStatus)

    suspend fun getCurrentAnime(): List<MediaCardData>

    suspend fun getCurrentManga(): List<MediaCardData>

    suspend fun getRecommendations(): List<MediaCardData>
}

interface ClientHolder {
    val uniqueId: String
    val userClient: TrackerClient
    val animeClient: AnimeClient
    val mangaClient: MangaClient
    val characterClient: CharacterClient
}
