package tel.jeelpa.otter.trackerinterface.repository

import tel.jeelpa.otter.trackerinterface.models.MediaCardData
import tel.jeelpa.otter.trackerinterface.models.MediaDetailsFull

interface AnimeClient {
    suspend fun search(query: String) : List<MediaCardData>
    suspend fun getTrendingAnime() : List<MediaCardData>

    suspend fun getPopularAnime() : List<MediaCardData>

    suspend fun getRecentlyUpdated() : List<MediaCardData>

    suspend fun getAnimeDetails(id: Int) : MediaDetailsFull

    suspend fun getOpenings(id: Int): List<String>

    suspend fun getEndings(id: Int): List<String>
}
