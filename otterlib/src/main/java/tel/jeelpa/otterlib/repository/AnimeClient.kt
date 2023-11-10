package tel.jeelpa.otterlib.repository

import tel.jeelpa.otterlib.models.MediaCardData
import tel.jeelpa.otterlib.models.MediaDetailsFull

interface AnimeClient {
    suspend fun search(query: String) : List<MediaCardData>
    suspend fun getTrendingAnime() : List<MediaCardData>

    suspend fun getPopularAnime() : List<MediaCardData>

    suspend fun getRecentlyUpdated() : List<MediaCardData>

    suspend fun getAnimeDetails(id: Int) : MediaDetailsFull

    suspend fun getOpenings(id: Int): List<String>

    suspend fun getEndings(id: Int): List<String>
}
