package tel.jeelpa.otterlib.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import tel.jeelpa.otterlib.models.AppMediaListStatus
import tel.jeelpa.otterlib.models.MediaCardData
import tel.jeelpa.otterlib.models.User

interface TrackerClient {
    val uniqueId: String

    fun isLoggedIn() : Flow<Boolean>

    suspend fun login(callbackUri: Uri)

    suspend fun logout()

    suspend fun getUser(): User

    suspend fun updateMedia(id: Int, score: Int, mediaListStatus: AppMediaListStatus)

    suspend fun getCurrentAnime(): List<MediaCardData>

    suspend fun getCurrentManga(): List<MediaCardData>

    suspend fun getRecommendations(): List<MediaCardData>
}

interface LoginProcedure {
    operator fun invoke()
}