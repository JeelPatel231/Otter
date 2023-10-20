package tel.jeelpa.otterlib.repository

import tel.jeelpa.otterlib.models.MediaCardData
import tel.jeelpa.otterlib.models.MediaDetailsFull

interface MangaClient {
    suspend fun getTrendingManga(): List<MediaCardData>

    suspend fun getPopularManga(): List<MediaCardData>

    suspend fun getTrendingNovel(): List<MediaCardData>

    suspend fun getMangaDetails(id: Int) : MediaDetailsFull
}