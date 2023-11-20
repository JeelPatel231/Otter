package tel.jeelpa.anilisttrackerplugin.data

import com.apollographql.apollo3.ApolloClient
import tel.jeelpa.otter.anilisttrackerplugin.models.type.MediaFormat
import tel.jeelpa.otter.anilisttrackerplugin.models.type.MediaSort
import tel.jeelpa.otter.anilisttrackerplugin.models.type.MediaType
import tel.jeelpa.plugininterface.tracker.models.AppMediaType
import tel.jeelpa.plugininterface.tracker.models.MediaCardData
import tel.jeelpa.plugininterface.tracker.models.MediaDetailsFull
import tel.jeelpa.plugininterface.tracker.repository.MangaClient

class MangaClientImpl(
    private val anilistApolloClient: ApolloClient
) : BaseClient(anilistApolloClient), MangaClient {

    override suspend fun search(query: String, page: Int, itemsInPage: Int): List<MediaCardData> =
        super.search(query, page, itemsInPage, AppMediaType.MANGA)

    override suspend fun getTrendingManga(page: Int, itemsInPage: Int): List<MediaCardData> =
        executeBaselineMediaQuery(
            page = page,
            perPage = itemsInPage,
            sort = listOf(MediaSort.TRENDING_DESC),
            type = MediaType.MANGA
        )

    override suspend fun getPopularManga(page: Int, itemsInPage: Int): List<MediaCardData> =
        executeBaselineMediaQuery(
            page = page,
            perPage = itemsInPage,
            sort = listOf(MediaSort.POPULARITY_DESC),
            type = MediaType.MANGA
        )


    override suspend fun getTrendingNovel(page: Int, itemsInPage: Int): List<MediaCardData> =
        executeBaselineMediaQuery(
            page = page,
            perPage = itemsInPage,
            sort = listOf(MediaSort.TRENDING_DESC),
            type = MediaType.MANGA,
            format = MediaFormat.NOVEL
        )

    override suspend fun getMangaDetails(id: Int): MediaDetailsFull = getMediaDetails(id)
}