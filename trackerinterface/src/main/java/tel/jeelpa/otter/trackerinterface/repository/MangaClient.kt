package tel.jeelpa.otter.trackerinterface.repository

import tel.jeelpa.otter.trackerinterface.models.MediaCardData
import tel.jeelpa.otter.trackerinterface.models.MediaDetailsFull

interface MangaClient {
    suspend fun search(query: String) : List<MediaCardData>

    suspend fun getTrendingManga(): List<MediaCardData>

    suspend fun getPopularManga(): List<MediaCardData>

    suspend fun getTrendingNovel(): List<MediaCardData>

    suspend fun getMangaDetails(id: Int) : MediaDetailsFull
}