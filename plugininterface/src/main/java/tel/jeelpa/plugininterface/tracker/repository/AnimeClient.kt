package tel.jeelpa.plugininterface.tracker.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import tel.jeelpa.plugininterface.tracker.models.MediaCardData
import tel.jeelpa.plugininterface.tracker.models.MediaDetailsFull

interface AnimeClient {
    fun search(query: String) : Flow<PagingData<MediaCardData>>
    fun getTrendingAnime() : Flow<PagingData<MediaCardData>>

    fun getPopularAnime() : Flow<PagingData<MediaCardData>>

    fun getRecentlyUpdated() : Flow<PagingData<MediaCardData>>

    suspend fun getAnimeDetails(id: Int) : MediaDetailsFull

    suspend fun getOpenings(id: Int): List<String>

    suspend fun getEndings(id: Int): List<String>
}
