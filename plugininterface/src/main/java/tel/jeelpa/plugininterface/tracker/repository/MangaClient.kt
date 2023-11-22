package tel.jeelpa.plugininterface.tracker.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import tel.jeelpa.plugininterface.tracker.models.MediaCardData
import tel.jeelpa.plugininterface.tracker.models.MediaDetailsFull

interface MangaClient {
    fun search(query: String) : Flow<PagingData<MediaCardData>>
    fun getTrendingManga() : Flow<PagingData<MediaCardData>>

    fun getPopularManga() : Flow<PagingData<MediaCardData>>

    fun getTrendingNovel() : Flow<PagingData<MediaCardData>>

    suspend fun getMangaDetails(id: Int) : MediaDetailsFull
}