package tel.jeelpa.otter.maltrackerplugin.data

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import tel.jeelpa.otter.maltrackerplugin.models.FullMediaMalDetails
import tel.jeelpa.plugininterface.helpers.parsed
import tel.jeelpa.plugininterface.tracker.models.AppMediaType
import tel.jeelpa.plugininterface.tracker.models.MediaCardData
import tel.jeelpa.plugininterface.tracker.models.MediaDetailsFull
import tel.jeelpa.plugininterface.tracker.repository.MangaClient

class MalMangaClient(
    private val okHttpClient: OkHttpClient,
) : BaseMalClient(okHttpClient), MangaClient {

    private val mediaCardDataFields: HttpUrl.Builder.() -> Unit = {
        addQueryParameter("fields", "mean,num_chapters,nsfw,media_type")
    }

    override fun search(query: String): Flow<PagingData<MediaCardData>> {
        return makePagedQuery {
            addPathSegment("manga")
            addQueryParameter("q", query)
            apply(mediaCardDataFields)
        }
    }

    override fun getTrendingManga(): Flow<PagingData<MediaCardData>> {
        return makePagedQuery {
            addPathSegment("manga")
            addPathSegment("ranking")
            addQueryParameter("ranking_type", "manga")
            apply(mediaCardDataFields)
        }
    }

    override fun getPopularManga(): Flow<PagingData<MediaCardData>> {
        return makePagedQuery {
            addPathSegment("manga")
            addPathSegment("ranking")
            addQueryParameter("ranking_type", "bypopularity")
            apply(mediaCardDataFields)
        }
    }

    override fun getTrendingNovel(): Flow<PagingData<MediaCardData>> {
        return makePagedQuery {
            addPathSegment("manga")
            addPathSegment("ranking")
            addQueryParameter("ranking_type", "novels")
            apply(mediaCardDataFields)
        }
    }

    private val cacheLock = Mutex(false)
    private var fullMangaDetailsCache: FullMediaMalDetails? = null
    private suspend fun getCache(id: Int) : FullMediaMalDetails {
        cacheLock.withLock {
            if (fullMangaDetailsCache == null) {
                fullMangaDetailsCache = makeMalQuery {
                    addPathSegment("manga")
                    addPathSegment(id.toString())
                    addQueryParameter(
                        "fields",
                        "end_date,mean,nsfw,num_chapters,alternative_titles,opening_themes,ending_themes,start_date,end_date,synopsis,start_season,rank,popularity,num_list_users,num_scoring_users,created_at,updated_at,media_type,status,genres,my_list_status,average_episode_duration,pictures,related_anime{node{id, title, main_picture, mean, nsfw,num_episodes,media_type}},related_manga{node{id, title, main_picture, mean, nsfw,num_chapters,media_type}},recommendations{node{id, title, main_picture, mean, nsfw,num_episodes,media_type}},studios"
                    )
                }.parsed<FullMediaMalDetails>()
            }

            return fullMangaDetailsCache!!
        }
    }

    override suspend fun getMangaDetails(id: Int): MediaDetailsFull {
        return getCache(id).toMediaFullDetails(AppMediaType.MANGA)
    }

}
