package tel.jeelpa.otterlib.data

import com.apollographql.apollo3.ApolloClient
import tel.jeelpa.otter.type.MediaFormat
import tel.jeelpa.otter.type.MediaSort
import tel.jeelpa.otter.type.MediaType
import tel.jeelpa.otterlib.models.AppMediaType
import tel.jeelpa.otterlib.models.MediaCardData
import tel.jeelpa.otterlib.models.MediaDetailsFull
import tel.jeelpa.otterlib.repository.MangaClient

class MangaClientImpl(
    private val anilistApolloClient: ApolloClient
) : BaseClient(anilistApolloClient),MangaClient {

    override suspend fun search(query: String): List<MediaCardData> =
        super.search(query, null, null, AppMediaType.MANGA)

    override suspend fun getTrendingManga(): List<MediaCardData> =
        executeBaselineMediaQuery(
            page = 1,
            perPage = 30,
            sort = listOf(MediaSort.TRENDING_DESC),
            type = MediaType.MANGA
        )

    override suspend fun getPopularManga(): List<MediaCardData> =
        executeBaselineMediaQuery(
            page = 1,
            perPage = 30,
            sort = listOf(MediaSort.POPULARITY_DESC),
            type = MediaType.MANGA
        )


    override suspend fun getTrendingNovel(): List<MediaCardData> =
        executeBaselineMediaQuery(
            page = 1,
            perPage = 30,
            sort = listOf(MediaSort.TRENDING_DESC),
            type = MediaType.MANGA,
            format = MediaFormat.NOVEL
        )

    override suspend fun getMangaDetails(id: Int): MediaDetailsFull = getMediaDetails(id)
}