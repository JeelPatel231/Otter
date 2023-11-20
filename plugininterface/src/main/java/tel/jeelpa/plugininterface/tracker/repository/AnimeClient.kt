package tel.jeelpa.plugininterface.tracker.repository

import tel.jeelpa.plugininterface.tracker.models.MediaCardData
import tel.jeelpa.plugininterface.tracker.models.MediaDetailsFull

interface AnimeClient {
    suspend fun search(query: String, page: Int, itemsInPage: Int) : List<MediaCardData>
    suspend fun getTrendingAnime(page:Int, itemsInPage: Int) : List<MediaCardData>

    suspend fun getPopularAnime(page:Int, itemsInPage: Int) : List<MediaCardData>

    suspend fun getRecentlyUpdated(page: Int, itemsInPage: Int) : List<MediaCardData>

    suspend fun getAnimeDetails(id: Int) : MediaDetailsFull

    suspend fun getOpenings(id: Int): List<String>

    suspend fun getEndings(id: Int): List<String>
}
