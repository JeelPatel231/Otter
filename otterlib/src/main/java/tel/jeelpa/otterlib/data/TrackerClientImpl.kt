package tel.jeelpa.otterlib.data

import android.net.Uri
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.api.http.HttpRequest
import com.apollographql.apollo3.api.http.HttpResponse
import com.apollographql.apollo3.network.http.HttpInterceptor
import com.apollographql.apollo3.network.http.HttpInterceptorChain
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import tel.jeelpa.otter.CurrentUserMediaQuery
import tel.jeelpa.otter.GetUserRecommendationsQuery
import tel.jeelpa.otter.GetViewerDataQuery
import tel.jeelpa.otter.type.MediaListStatus
import tel.jeelpa.otter.type.MediaType
import tel.jeelpa.otterlib.models.AnilistData
import tel.jeelpa.otterlib.models.AnilistRequestBody
import tel.jeelpa.otterlib.models.AnilistResponseBody
import tel.jeelpa.otterlib.models.AppMediaListStatus
import tel.jeelpa.otterlib.models.MediaCardData
import tel.jeelpa.otterlib.models.User
import tel.jeelpa.otterlib.models.toApp
import tel.jeelpa.otterlib.repository.TrackerClient
import tel.jeelpa.otterlib.store.UserStorage


class AuthorizationInterceptor(
    private val userStore: UserStorage,
    val refreshCallback: () -> Unit,
) : HttpInterceptor {
    override suspend fun intercept(
        request: HttpRequest,
        chain: HttpInterceptorChain
    ): HttpResponse {
        val stringData = userStore.loadData().first()
            ?: throw IllegalStateException("User Not Logged In")
        val token = Json.decodeFromString<AnilistResponseBody>(stringData).access_token

        val response =
            chain.proceed(request.newBuilder().addHeader("Authorization", "Bearer $token").build())

        return if (response.statusCode == 401) {
            refreshCallback()
            chain.proceed(request.newBuilder().addHeader("Authorization", "Bearer $token").build())
        } else {
            response
        }
    }
}

class TrackerClientImpl(
    private val anilistData: AnilistData,
    private val httpClient: OkHttpClient,
    private val userStore: UserStorage,
): TrackerClient {
    override val uniqueId = "ANILIST"

    private var loggedInUserCache: User? = null

    private fun refreshToken(){
        TODO("Anilist Token Expired :scull: no way")
    }

    private val anilistApolloClient = ApolloClient.Builder()
        .serverUrl("https://graphql.anilist.co")
        .addHttpInterceptor(AuthorizationInterceptor(userStore, ::refreshToken))
        .build()

    override fun isLoggedIn(): Flow<Boolean> = flow {
        userStore.loadData().collect {
            emit(it != null)
        }
    }

    override suspend fun login(callbackUri: Uri) = coroutineScope {
        val code = callbackUri.getQueryParameter("code")
            ?: throw IllegalStateException("Code not found!")


        val request = Request.Builder()
            .url("https://anilist.co/api/v2/oauth/token")
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .post(
                Json.encodeToString(
                    AnilistRequestBody(
                        grant_type = "authorization_code",
                        client_id = anilistData.id,
                        client_secret = anilistData.secret,
                        redirect_uri = anilistData.redirectUri,
                        code = code
                    )
                ).toRequestBody()
            ).build()

        val response = httpClient.newCall(request).execute()
        userStore.saveData(response.body!!.string())
    }

    override suspend fun logout() {
        userStore.clearData()
        loggedInUserCache = null
    }

    override suspend fun getUser(): User {
        if (loggedInUserCache != null){
            return loggedInUserCache!! // ??? why doesnt smart type cast work here
        }

        val viewer = anilistApolloClient
            .query(GetViewerDataQuery()).execute()
            .data?.Viewer ?: throw IllegalStateException("Network Succ")

        loggedInUserCache = User(
            userId = viewer.id,
            username = viewer.name,
            bannerImage = viewer.bannerImage,
            chapterCount = viewer.statistics?.manga?.chaptersRead ?: 0,
            episodeCount = viewer.statistics?.anime?.episodesWatched ?: 0,
            profileImage = viewer.avatar?.medium
        )

        return loggedInUserCache!!
    }

    override suspend fun updateMedia(id: Int, score: Int, mediaListStatus: AppMediaListStatus) {
        TODO("Not yet implemented")
    }

    private suspend fun getUserMedia(
        userId: Int,
        type: MediaType? = null,
        status: MediaListStatus
    ): List<CurrentUserMediaQuery.Media> {
        return anilistApolloClient.query(
            CurrentUserMediaQuery(
                userId = userId,
                type = Optional.present(type),
                status = Optional.present(status)
            )
        ).execute()
            .data?.MediaListCollection?.lists?.flatMap { it?.entries ?: emptyList() }
            ?.mapNotNull { it?.media } ?: emptyList()
    }

    override suspend fun getCurrentAnime(): List<MediaCardData> {
        val userId = getUser().userId

        return getUserMedia(userId, MediaType.ANIME, MediaListStatus.CURRENT).map {
            MediaCardData(
                id = it.id,
                title = it.title?.english ?: it.title?.romaji ?: it.title?.userPreferred!!,
                status = it.status!!.toApp(),
                type = it.type!!.toApp(),
                isAdult = it.isAdult ?: false,
                meanScore = (it.meanScore ?: 0)/10f,
                coverImage = it.coverImage?.large!!,
                nextAiringEpisode = it.nextAiringEpisode?.episode,
                episodes = it.episodes,
                chapters = it.chapters,
            )
        }
    }

    override suspend fun getCurrentManga() : List<MediaCardData> {
        val userId = getUser().userId

        return getUserMedia(userId, MediaType.MANGA, MediaListStatus.CURRENT).map {
            MediaCardData(
                id = it.id,
                title = it.title?.english ?: it.title?.romaji ?: it.title?.userPreferred!!,
                status = it.status!!.toApp(),
                type = it.type!!.toApp(),
                isAdult = it.isAdult ?: false,
                meanScore = (it.meanScore ?: 0)/10f,
                coverImage = it.coverImage?.large!!,
                nextAiringEpisode = it.nextAiringEpisode?.episode,
                episodes = it.episodes,
                chapters = it.chapters,
            )
        }
    }


    override suspend fun getRecommendations(): List<MediaCardData> {
        return anilistApolloClient.query(GetUserRecommendationsQuery())
            .execute()
            .data
            ?.Page
            ?.recommendations
            ?.mapNotNull { it?.mediaRecommendation }
            ?.map {
                MediaCardData(
                    id = it.id,
                    title = it.title?.english ?: it.title?.romaji ?: it.title?.userPreferred!!,
                    status = it.status!!.toApp(),
                    type = it.type!!.toApp(),
                    isAdult = it.isAdult ?: false,
                    meanScore = (it.meanScore ?: 0)/10f,
                    coverImage = it.coverImage?.large!!,
                    nextAiringEpisode = it.nextAiringEpisode?.episode,
                    episodes = it.episodes,
                    chapters = it.chapters,
                )
            } ?: emptyList()
    }
}