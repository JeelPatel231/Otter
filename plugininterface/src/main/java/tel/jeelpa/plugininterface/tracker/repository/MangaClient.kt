package tel.jeelpa.plugininterface.tracker.repository

import tel.jeelpa.plugininterface.tracker.models.MediaCardData
import tel.jeelpa.plugininterface.tracker.models.MediaDetailsFull

interface MangaClient {
    suspend fun search(query: String, page: Int, itemsInPage: Int) : List<MediaCardData>
    suspend fun getTrendingManga(page:Int, itemsInPage: Int) : List<MediaCardData>

    suspend fun getPopularManga(page:Int, itemsInPage: Int) : List<MediaCardData>

    suspend fun getTrendingNovel(page: Int, itemsInPage: Int) : List<MediaCardData>

    suspend fun getMangaDetails(id: Int) : MediaDetailsFull
}