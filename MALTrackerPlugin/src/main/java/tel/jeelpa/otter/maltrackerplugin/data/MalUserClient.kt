package tel.jeelpa.otter.maltrackerplugin.data

import androidx.paging.PagingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import tel.jeelpa.otter.maltrackerplugin.models.MalUser
import tel.jeelpa.plugininterface.helpers.parsed
import tel.jeelpa.plugininterface.storage.UserStorage
import tel.jeelpa.plugininterface.tracker.models.MediaCardData
import tel.jeelpa.plugininterface.tracker.models.User
import tel.jeelpa.plugininterface.tracker.models.UserMediaAnime
import tel.jeelpa.plugininterface.tracker.models.UserMediaManga
import tel.jeelpa.plugininterface.tracker.repository.UserClient
import java.net.URI
import java.security.SecureRandom
import java.util.Base64


// TODO, handle lists like name[]
val URI.queryParams: Map<String, String> get() =
    query.split("&") // split the query params
        .map { it.split("=") } // split the key-value pairs
        .associate { (key, value) -> key to value }


class MalUserClient(
    private val okHttpClient: OkHttpClient,
    private val clientId: String,
    private val userStorage: UserStorage
): BaseMalClient(okHttpClient), UserClient {

    override val loginUri =
        "https://myanimelist.net/v1/oauth2/authorize?response_type=code&client_id=$clientId&code_challenge=$codeChallenge"

    override fun isLoggedIn(): Flow<Boolean> = flow {
        userStorage.loadData().collect { emit(it != null ) }
    }

    override suspend fun login(callbackUri: String) {
        val code = URI.create(callbackUri).queryParams["code"]!!
        val reqBody = FormBody.Builder()
            .add("client_id", clientId)
            .add("code", code)
            .add("code_verifier", codeChallenge)
            .add("grant_type","authorization_code")
            .build()

        val request = Request.Builder()
            .url("https://myanimelist.net/v1/oauth2/token")
            .post(reqBody)
            .build()

        val res = withContext(Dispatchers.IO){ okHttpClient.newCall(request).execute().body!!.string() }
        userStorage.saveData(res)
    }

    override suspend fun logout() {
        userStorage.clearData()
    }

    override suspend fun getUser(): User {
        val malUser = makeMalQuery {
            addPathSegment("users")
            addPathSegment("@me")
            addQueryParameter("fields", "anime_statistics,picture")
        }.parsed<MalUser>()
        return User(
            userId = malUser.id,
            bannerImage = null,
            chapterCount = 0,
            profileImage = malUser.picture,
            episodeCount = malUser.anime_statistics.num_episodes,
            username = malUser.name
        )
    }

    override suspend fun updateAnime(id: Int, status: UserMediaAnime) {
        TODO("Not yet implemented")
    }

    override suspend fun updateManga(id: Int, status: UserMediaManga) {
        TODO("Not yet implemented")
    }

    override fun getCurrentAnime(): Flow<PagingData<MediaCardData>> {
        return makePagedQuery {
            addPathSegment("users")
            addPathSegment("@me")
            addPathSegment("animelist")
            addQueryParameter("status", "watching")
            addQueryParameter("fields", "list_status,mean,num_episodes,nsfw,media_type")
        }
    }

    override fun getCurrentManga(): Flow<PagingData<MediaCardData>> {
        return makePagedQuery {
            addPathSegment("users")
            addPathSegment("@me")
            addPathSegment("mangalist")
            addQueryParameter("status", "reading")
            addQueryParameter("fields", "list_status,mean,num_episodes,nsfw,media_type")
        }
    }

    override fun getRecommendations(): Flow<PagingData<MediaCardData>> {
        return makePagedQuery {
            addPathSegment("anime")
            addPathSegment("suggestions")
            addQueryParameter("fields", "list_status,mean,num_episodes,nsfw,media_type")
        }
    }

    companion object {
        private val codeVerifierBytes = ByteArray(96).apply {
            SecureRandom().nextBytes(this)
        }

        val codeChallenge = Base64.getEncoder().encodeToString(codeVerifierBytes).trimEnd('=')
            .replace("+", "-")
            .replace("/", "_")
            .replace("\n", "")

    }

}