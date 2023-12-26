package tel.jeelpa.anilisttrackerplugin.data

import androidx.paging.PagingData
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.api.http.HttpRequest
import com.apollographql.apollo3.api.http.HttpResponse
import com.apollographql.apollo3.network.http.HttpInterceptor
import com.apollographql.apollo3.network.http.HttpInterceptorChain
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import tel.jeelpa.anilisttrackerplugin.models.AnilistData
import tel.jeelpa.anilisttrackerplugin.models.AnilistRequestBody
import tel.jeelpa.anilisttrackerplugin.models.AnilistResponseBody
import tel.jeelpa.anilisttrackerplugin.models.toApp
import tel.jeelpa.otter.anilisttrackerplugin.models.CurrentUserMediaQuery
import tel.jeelpa.otter.anilisttrackerplugin.models.DeleteMediaFromListMutation
import tel.jeelpa.otter.anilisttrackerplugin.models.GetUserRecommendationsQuery
import tel.jeelpa.otter.anilisttrackerplugin.models.GetViewerDataQuery
import tel.jeelpa.otter.anilisttrackerplugin.models.UpdateMediaDetailsMutation
import tel.jeelpa.otter.anilisttrackerplugin.models.type.FuzzyDateInput
import tel.jeelpa.otter.anilisttrackerplugin.models.type.MediaListStatus
import tel.jeelpa.otter.anilisttrackerplugin.models.type.MediaType
import tel.jeelpa.plugininterface.storage.UserStorage
import tel.jeelpa.plugininterface.tracker.models.AppMediaListStatus
import tel.jeelpa.plugininterface.tracker.models.MediaCardData
import tel.jeelpa.plugininterface.tracker.models.User
import tel.jeelpa.plugininterface.tracker.models.UserMediaAnime
import tel.jeelpa.plugininterface.tracker.models.UserMediaManga
import tel.jeelpa.plugininterface.tracker.repository.UserClient
import java.net.URI
import java.time.LocalDate


class AuthorizationInterceptor(
    private val userStore: UserStorage,
//    val refreshCallback: () -> Unit,
) : HttpInterceptor {
    private fun HttpRequest.addTokenHeader(token: String) =
        newBuilder().addHeader("Authorization", "Bearer $token").build()

    override suspend fun intercept(
        request: HttpRequest,
        chain: HttpInterceptorChain
    ): HttpResponse {
        val stringData = userStore.loadData().firstOrNull()

        val newReq = if (stringData != null) {
            // get the token and use it
            val token = Json.decodeFromString<AnilistResponseBody>(stringData).access_token
            request.addTokenHeader(token)
        } else request

        // the anilist token wont expire i think

        // TODO : check body for cause of 401, is it really auth token?
//        // if 401, refresh the token and use it
//        if (response.statusCode == 401) {
//            refreshCallback()
//            return chain.proceed(request.addTokenHeader(token))
//       }
        return chain.proceed(newReq)
    }
}

class TrackerClientImpl(
    private val anilistData: AnilistData,
    private val httpClient: OkHttpClient,
    private val userStore: UserStorage,
    private val anilistApolloClient: ApolloClient
): UserClient {

    override val loginUri = "https://anilist.co/api/v2/oauth/authorize?client_id=${anilistData.id}&redirect_uri=${anilistData.redirectUri}&response_type=code"
    private var loggedInUserCache: User? = null

    override fun isLoggedIn(): Flow<Boolean> = flow {
        emitAll(userStore.loadData().map { it != null })
    }

    override suspend fun login(callbackUri: String) = coroutineScope {
        val code = URI.create(callbackUri).query.split("&").find { it.startsWith("code=") }
            ?.removePrefix("code=")
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

    private fun LocalDate.toAnilist() : FuzzyDateInput {
        return FuzzyDateInput(
            year = Optional.presentIfNotNull(year),
            month = Optional.presentIfNotNull(monthValue),
            day = Optional.presentIfNotNull(dayOfMonth)
        )
    }

    private fun AppMediaListStatus.toAnilist(): MediaListStatus {
        return MediaListStatus.safeValueOf(this.name)
    }

    override suspend fun updateAnime(id: Int, status: UserMediaAnime) {
        anilistApolloClient.mutation(UpdateMediaDetailsMutation(
            mediaId = id,
            status = status.status.toAnilist(),
            progress = status.watched,
            startedAt = Optional.presentIfNotNull(status.startDate?.toAnilist())
        )).execute()
    }

    override suspend fun updateManga(id: Int, status: UserMediaManga) {
        anilistApolloClient.mutation(UpdateMediaDetailsMutation(
            mediaId = id,
            status = status.status.toAnilist(),
            progress = status.chapters,
            startedAt = Optional.presentIfNotNull(status.startDate?.toAnilist())
        )).execute()
    }

    private suspend fun deleteMedia(id: Int) {
        anilistApolloClient.mutation(DeleteMediaFromListMutation(id)).execute()
    }

    override suspend fun deleteAnime(id: Int) = deleteMedia(id)

    override suspend fun deleteManga(id: Int) = deleteMedia(id)

    private suspend fun getUserMedia(
        userId: Int,
        type: MediaType? = null,
        statusIn: List<MediaListStatus>
    ): List<CurrentUserMediaQuery.Media> {
        return anilistApolloClient.query(
            CurrentUserMediaQuery(
                userId = userId,
                type = Optional.present(type),
                statusIn = Optional.present(statusIn)
            )
        ).execute()
            .data?.MediaListCollection?.lists?.flatMap { it?.entries ?: emptyList() }
            ?.mapNotNull { it?.media } ?: emptyList()
    }

    override fun getCurrentAnime(): Flow<PagingData<MediaCardData>> = flow {
        val userId = getUser().userId
        val listData = getUserMedia(userId, MediaType.ANIME, listOf(MediaListStatus.CURRENT, MediaListStatus.PLANNING)).map {
            MediaCardData(
                id = it.id,
                title = it.title?.english ?: it.title?.romaji ?: it.title?.userPreferred!!,
                status = it.status!!.toApp(),
                type = it.type!!.toApp(),
                isAdult = it.isAdult ?: false,
                meanScore = (it.meanScore ?: 0) / 10f,
                coverImage = it.coverImage?.large!!,
                episodesAired = it.nextAiringEpisode?.episode?.minus(1),
                episodes = it.episodes,
                chapters = it.chapters,
                userWatched = it.mediaListEntry?.progress,
                userListStatus = it.mediaListEntry?.status.toApp(),
                userScore = it.mediaListEntry?.score,
            )
        }

        emit(PagingData.from(listData))
    }

    override fun getCurrentManga(): Flow<PagingData<MediaCardData>> = flow {
        val userId = getUser().userId
        // anilist is not paged for user list
        val listData = getUserMedia(userId, MediaType.MANGA, listOf(MediaListStatus.CURRENT,MediaListStatus.PLANNING)).map {
            MediaCardData(
                id = it.id,
                title = it.title?.english ?: it.title?.romaji ?: it.title?.userPreferred!!,
                status = it.status!!.toApp(),
                type = it.type!!.toApp(),
                isAdult = it.isAdult ?: false,
                meanScore = (it.meanScore ?: 0)/10f,
                coverImage = it.coverImage?.large!!,
                episodesAired = it.nextAiringEpisode?.episode?.minus(1),
                episodes = it.episodes,
                chapters = it.chapters,
                userWatched = it.mediaListEntry?.progress,
                userListStatus = it.mediaListEntry?.status.toApp(),
                userScore = it.mediaListEntry?.score,
            )
        }

        emit(PagingData.from(listData))
    }


    override fun getRecommendations(): Flow<PagingData<MediaCardData>> = flow {
        val listData = anilistApolloClient.query(GetUserRecommendationsQuery())
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
                    episodesAired = it.nextAiringEpisode?.episode?.minus(1),
                    episodes = it.episodes,
                    chapters = it.chapters,
                    userWatched = it.mediaListEntry?.progress,
                    userListStatus = it.mediaListEntry?.status.toApp(),
                    userScore = it.mediaListEntry?.score,
                )
            } ?: emptyList()

        emit(PagingData.from(listData))
    }
}