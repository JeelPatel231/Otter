package tel.jeelpa.otter.maltrackerplugin.data

import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import tel.jeelpa.otter.maltrackerplugin.models.ResponseToken
import tel.jeelpa.plugininterface.storage.UserStorage
import tel.jeelpa.plugininterface.tracker.repository.ClientHolder
import tel.jeelpa.plugininterface.tracker.repository.MangaClient


class MalClientHolder(
    private val okHttpClient: OkHttpClient,
    private val userStorage: UserStorage
) : ClientHolder() {

    private val liveTokenData = flow {
        userStorage.loadData().collect {
            val data = it?.let { Json.decodeFromString<ResponseToken>(it) }
            emit(data)
        }
    }

    val tokenClient = okHttpClient.newBuilder().addInterceptor(
        MalTokenInterceptor(clientId, liveTokenData)
    ).build()

    override val uniqueId = "MyAnimeList"

    override val userClient = MalUserClient(tokenClient, clientId, userStorage)
    override val animeClient
        get() = MalAnimeClient(tokenClient)
    override val mangaClient: MangaClient
        get() = MalMangaClient(tokenClient)

    override val characterClient = MalCharacterClient(tokenClient)

    companion object {
        private const val clientId = "yourMalClientID"
    }

}
