package tel.jeelpa.otter.trackerinterface.repository

import kotlinx.coroutines.flow.Flow
import tel.jeelpa.otter.trackerinterface.models.AppMediaListStatus
import tel.jeelpa.otter.trackerinterface.models.MediaCardData
import tel.jeelpa.otter.trackerinterface.models.User

interface UserClient {
    val loginUri: String

    fun isLoggedIn() : Flow<Boolean>

    suspend fun login(callbackUri: String)

    suspend fun logout()

    suspend fun getUser(): User

    suspend fun updateMedia(id: Int, score: Int, mediaListStatus: AppMediaListStatus)

    suspend fun getCurrentAnime(): List<MediaCardData>

    suspend fun getCurrentManga(): List<MediaCardData>

    suspend fun getRecommendations(): List<MediaCardData>
}

abstract class ClientHolder {
    abstract val uniqueId: String
    abstract val userClient: UserClient
    abstract val animeClient: AnimeClient
    abstract val mangaClient: MangaClient
    abstract val characterClient: CharacterClient

    override fun toString() : String {
        return this.uniqueId
    }
}