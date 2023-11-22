package tel.jeelpa.anilisttrackerplugin.data

import androidx.paging.PagingData
import com.apollographql.apollo3.ApolloClient
import kotlinx.coroutines.flow.Flow
import tel.jeelpa.otter.anilisttrackerplugin.models.type.MediaFormat
import tel.jeelpa.otter.anilisttrackerplugin.models.type.MediaSort
import tel.jeelpa.otter.anilisttrackerplugin.models.type.MediaType
import tel.jeelpa.plugininterface.tracker.models.AppMediaType
import tel.jeelpa.plugininterface.tracker.models.MediaCardData
import tel.jeelpa.plugininterface.tracker.models.MediaDetailsFull
import tel.jeelpa.plugininterface.tracker.repository.MangaClient

class MangaClientImpl(
    anilistApolloClient: ApolloClient
) : BaseClient(anilistApolloClient), MangaClient {

    override fun search(query: String): Flow<PagingData<MediaCardData>> =
        delegateToPagingSource { page, perPage ->
            super.search(
                query = query,
                page = page,
                perPage = perPage,
                mediaType = AppMediaType.MANGA
            )
        }

    override fun getTrendingManga(): Flow<PagingData<MediaCardData>> =
        delegateToPagingSource { page, perPage ->
            executeBaselineMediaQuery(
                page = page,
                perPage = perPage,
                sort = listOf(MediaSort.TRENDING_DESC),
                type = MediaType.MANGA
            )
        }

    override fun getPopularManga(): Flow<PagingData<MediaCardData>> =
        delegateToPagingSource { page, perPage ->
            executeBaselineMediaQuery(
                page = page,
                perPage = perPage,
                sort = listOf(MediaSort.POPULARITY_DESC),
                type = MediaType.MANGA
            )
        }


    override fun getTrendingNovel(): Flow<PagingData<MediaCardData>> =
        delegateToPagingSource { page, perPage ->
            executeBaselineMediaQuery(
                page = page,
                perPage = perPage,
                sort = listOf(MediaSort.POPULARITY_DESC),
                type = MediaType.MANGA,
                format = MediaFormat.NOVEL
            )
        }

    override suspend fun getMangaDetails(id: Int): MediaDetailsFull = getMediaDetails(id)
}