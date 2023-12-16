package tel.jeelpa.anilisttrackerplugin.data

import com.apollographql.apollo3.ApolloClient
import okhttp3.OkHttpClient
import tel.jeelpa.anilisttrackerplugin.models.AnilistData
import tel.jeelpa.plugininterface.storage.UserStorage
import tel.jeelpa.plugininterface.tracker.repository.ClientHolder

class ClientHolderImpl(
    private val userStorage: UserStorage,
    okHttpClient: OkHttpClient = buildOkHttpClient(),
) : ClientHolder() {
    override val uniqueId = "ANILIST"

    private val anilistClient = ApolloClient.Builder()
        .serverUrl("https://graphql.anilist.co")
        .addHttpInterceptor(AuthorizationInterceptor(userStorage))
        .build()

    companion object {

        private fun buildOkHttpClient() = OkHttpClient.Builder()
            .build()

        private val anilistData = AnilistData(
            "id",
            "secret",
            "redirect"
        )
    }

    // can be static, it doesn't hold any cache
    override val userClient = TrackerClientImpl(anilistData, okHttpClient, userStorage, anilistClient)
    override val mangaClient = MangaClientImpl(anilistClient)
    override val characterClient = CharacterClientImpl(anilistClient)

    // these clients hold cache and need to get Re-Instantiated on every use to delete cache
    override val animeClient
        get() = AnimeClientImpl(anilistClient)
}