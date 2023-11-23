package tel.jeelpa.anilisttrackerplugin.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import kotlinx.coroutines.flow.Flow
import tel.jeelpa.anilisttrackerplugin.models.toApp
import tel.jeelpa.anilisttrackerplugin.paging.AnilistPagingSource
import tel.jeelpa.otter.anilisttrackerplugin.models.GetMediaDetailsQuery
import tel.jeelpa.otter.anilisttrackerplugin.models.MediaBaselineQuery
import tel.jeelpa.otter.anilisttrackerplugin.models.MediaSearchQuery
import tel.jeelpa.otter.anilisttrackerplugin.models.type.MediaFormat
import tel.jeelpa.otter.anilisttrackerplugin.models.type.MediaSort
import tel.jeelpa.otter.anilisttrackerplugin.models.type.MediaType
import tel.jeelpa.plugininterface.tracker.models.AppDate
import tel.jeelpa.plugininterface.tracker.models.AppMediaType
import tel.jeelpa.plugininterface.tracker.models.AppTrailer
import tel.jeelpa.plugininterface.tracker.models.MediaCardData
import tel.jeelpa.plugininterface.tracker.models.MediaDetailsFull
import tel.jeelpa.plugininterface.tracker.models.MediaRelationCardData
import tel.jeelpa.plugininterface.tracker.models.MediaRelationType

abstract class BaseClient (
    private val anilistClient: ApolloClient
) {

    protected fun <TData : Any> delegateToPagingSource(dataCallback: suspend (Int, Int) -> List<TData>): Flow<PagingData<TData>> {
        return Pager(
            config = PagingConfig(
                30,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { AnilistPagingSource(dataCallback) }
        ).flow
    }

    suspend fun search(query: String, page: Int?, perPage: Int?, mediaType: AppMediaType) : List<MediaCardData> {
        val mMediaType = when(mediaType) {
            AppMediaType.ANIME -> MediaType.ANIME
            AppMediaType.MANGA -> MediaType.MANGA
            else -> MediaType.UNKNOWN__
        }
        return anilistClient.query(
            MediaSearchQuery(
            query,
            Optional.presentIfNotNull(page),
            Optional.presentIfNotNull(perPage),
            mMediaType
        )
        ).execute().data?.Page?.media?.mapNotNull { it?.let {
            MediaCardData(
                id = it.id,
                title = it.title?.english ?: it.title?.romaji ?: it.title?.userPreferred!!,
                status = it.status!!.toApp(),
                type = it.type!!.toApp(),
                isAdult = it.isAdult ?: false,
                meanScore = (it.meanScore ?: 0) / 10f,
                coverImage = it.coverImage?.large!!,
                nextAiringEpisode = it.nextAiringEpisode?.episode,
                episodes = it.episodes,
                chapters = it.chapters,
                userWatched = it.mediaListEntry?.progress
            )
        } } ?: emptyList()
    }

    suspend fun executeBaselineMediaQuery(
        page: Int = 1,
        perPage: Int = 30,
        sort: List<MediaSort>,
        type: MediaType,
        format: MediaFormat? = null,
    ) : List<MediaCardData> {
        return anilistClient.query(
            MediaBaselineQuery(
                page = page,
                perPage = perPage,
                sort = sort,
                type = type,
                format = format?.let { Optional.present(format) } ?: Optional.absent()
            )
        ).execute().data?.Page?.media?.mapNotNull {
            it?.let {
                MediaCardData(
                    id = it.id,
                    title = it.title?.english ?: it.title?.romaji ?: it.title?.userPreferred!!,
                    status = it.status!!.toApp(),
                    type = it.type!!.toApp(),
                    isAdult = it.isAdult ?: false,
                    meanScore = (it.meanScore ?: 0) / 10f,
                    coverImage = it.coverImage?.large!!,
                    nextAiringEpisode = it.nextAiringEpisode?.episode,
                    episodes = it.episodes,
                    chapters = it.chapters,
                    userWatched = it.mediaListEntry?.progress
                )
            }
        } ?: emptyList()
    }

    suspend fun getMediaDetails(id: Int): MediaDetailsFull {
        val media = anilistClient.query(GetMediaDetailsQuery(id)).execute().data?.Media
            ?: throw IllegalStateException("ID data not available on anilist")

        with(media) {
            return MediaDetailsFull(
                title = title?.english ?: title?.romaji ?: title?.userPreferred ?: "Unknown Title",
                bannerImage = bannerImage ?: coverImage?.large!!,
                coverImage = coverImage?.extraLarge ?: coverImage?.large!!,
                description = description ?: "No Description",
                meanScore = (meanScore ?: 0) / 10f,
                episodes = episodes,
                chapters = chapters,
                countryOfOrigin = (countryOfOrigin ?: "Unknown").toString(),
                nextAiringEpisode = nextAiringEpisode?.episode,
                isAdult = isAdult ?: false,
                isFavourite = isFavourite,
                id = id,
                type = type.toApp(),
                status = status.toApp(),
                idMal = idMal,
                duration = duration,
                format = format?.name ?: "Unknown",
//                characters = characters?.edges?.map { edge ->
//                    CharacterCardData(
//                        id = edge?.node?.id!!,
//                        name = edge.node.name?.full!!,
//                        avatar = edge.node.image?.medium!!,
//                        role = edge.role?.name!!,
//                    )
//                } ?: emptyList(),
                endDate = AppDate(endDate?.day, endDate?.month, endDate?.year),
                startDate = AppDate(startDate?.day, startDate?.month, startDate?.year),
                genres = genres?.mapNotNull { it } ?: emptyList(),
                recommendation = recommendations?.edges?.mapNotNull { it?.node?.mediaRecommendation }
                    ?.map { edge ->
                        MediaCardData(
                            id = edge.id,
                            title = edge.title?.english ?: edge.title?.romaji ?: edge.title?.userPreferred!!,
                            status = edge.status!!.toApp(),
                            type = edge.type!!.toApp(),
                            isAdult = edge.isAdult ?: false,
                            meanScore = (edge.meanScore ?: 0) / 10f,
                            coverImage = edge.coverImage?.large!!,
                            nextAiringEpisode = edge.nextAiringEpisode?.episode,
                            episodes = edge.episodes,
                            chapters = edge.chapters,
                        )
                    } ?: emptyList(),
                relations = relations?.edges?.mapNotNull { edge ->
                    edge?.node?.let { node ->
                        MediaRelationCardData(
                            id = node.id,
                            title = node.title?.english ?: node.title?.romaji
                            ?: node.title?.userPreferred!!,
                            status = node.status!!.toApp(),
                            type = node.type!!.toApp(),
                            isAdult = node.isAdult ?: false,
                            meanScore = (node.meanScore ?: 0) / 10f,
                            coverImage = node.coverImage?.large!!,
                            nextAiringEpisode = node.nextAiringEpisode?.episode,
                            episodes = node.episodes,
                            chapters = node.chapters,
                            relation = MediaRelationType[edge.relationType!!.name]
                        )
                    }
                } ?: emptyList(),
                season = season?.name ?: "Unknown",
                source = source?.name ?: "Unknown",
                trailer = trailer?.let { AppTrailer(it.id!!, it.site!!, it.thumbnail!!) },
                studios = studios?.nodes?.mapNotNull { node -> node?.name } ?: emptyList(),
                synonyms = synonyms?.mapNotNull { it } ?: emptyList(),
                tags = tags?.mapNotNull { it?.name } ?: emptyList(),
            )
        }
    }

}