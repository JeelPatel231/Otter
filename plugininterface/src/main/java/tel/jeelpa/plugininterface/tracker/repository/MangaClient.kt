package tel.jeelpa.plugininterface.tracker.repository

import tel.jeelpa.plugininterface.tracker.models.MediaCardData
import tel.jeelpa.plugininterface.tracker.models.MediaDetailsFull

interface MangaClient {
    suspend fun search(query: String) : List<MediaCardData>

    suspend fun getTrendingManga(): List<MediaCardData>

    suspend fun getPopularManga(): List<MediaCardData>

    suspend fun getTrendingNovel(): List<MediaCardData>

    suspend fun getMangaDetails(id: Int) : MediaDetailsFull
}