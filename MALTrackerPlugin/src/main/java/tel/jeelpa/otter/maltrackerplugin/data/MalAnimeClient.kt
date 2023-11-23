package tel.jeelpa.otter.maltrackerplugin.data

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import tel.jeelpa.otter.maltrackerplugin.models.FullMediaMalDetails
import tel.jeelpa.plugininterface.helpers.parsed
import tel.jeelpa.plugininterface.tracker.models.AppMediaType
import tel.jeelpa.plugininterface.tracker.models.MediaCardData
import tel.jeelpa.plugininterface.tracker.models.MediaDetailsFull
import tel.jeelpa.plugininterface.tracker.repository.AnimeClient

class MalAnimeClient(
    private val okHttpClient: OkHttpClient,
) : BaseMalClient(okHttpClient), AnimeClient {

    private val mediaCardDataFields: HttpUrl.Builder.() -> Unit = {
        addQueryParameter("fields", "mean,num_episodes,nsfw,media_type")
    }

    override fun search(query: String): Flow<PagingData<MediaCardData>> {
        return makePagedQuery {
            addPathSegment("anime")
            addQueryParameter("q", query)
            apply(mediaCardDataFields)
        }
    }

    override fun getTrendingAnime(): Flow<PagingData<MediaCardData>> {
        return makePagedQuery {
            addPathSegment("anime")
            addPathSegment("ranking")
            addQueryParameter("ranking_type", "airing")
            apply(mediaCardDataFields)
        }
    }

    override fun getPopularAnime(): Flow<PagingData<MediaCardData>> {
        return makePagedQuery {
            addPathSegment("anime")
            addPathSegment("ranking")
            addQueryParameter("ranking_type", "bypopularity")
            apply(mediaCardDataFields)
        }
    }

    override fun getRecentlyUpdated(): Flow<PagingData<MediaCardData>> = flow {
        // TODO: get recently updated in MAL
        emit(PagingData.from(emptyList()))
    }

    private val cacheLock = Mutex(false)
    private var fullMediaMalDetailsCache: FullMediaMalDetails? = null
    private suspend fun getCache(id: Int) : FullMediaMalDetails {
        cacheLock.withLock {
            if (fullMediaMalDetailsCache == null) {
                fullMediaMalDetailsCache = makeMalQuery {
                    addPathSegment("anime")
                    addPathSegment(id.toString())
                    addQueryParameter(
                        "fields",
                        "end_date,mean,nsfw,num_episodes,alternative_titles,opening_themes,ending_themes,start_date,end_date,synopsis,start_season,rank,popularity,num_list_users,num_scoring_users,created_at,updated_at,media_type,status,genres,my_list_status,average_episode_duration,pictures,related_anime{node{id,title,main_picture,mean,nsfw,num_episodes,media_type}},related_manga{node{id, title, main_picture, mean, nsfw,num_chapters,media_type}},recommendations{node{id, title, main_picture, mean, nsfw,num_episodes,media_type}},studios"
                    )
                }.parsed<FullMediaMalDetails>()
            }

            return fullMediaMalDetailsCache!!
        }
    }
    override suspend fun getAnimeDetails(id: Int): MediaDetailsFull {
        return getCache(id).toMediaFullDetails(AppMediaType.ANIME)
    }

    override suspend fun getOpenings(id: Int): List<String> {
        return getCache(id).opening_themes.map { it.text }
    }

    override suspend fun getEndings(id: Int): List<String> {
        return getCache(id).ending_themes.map { it.text }
    }
}