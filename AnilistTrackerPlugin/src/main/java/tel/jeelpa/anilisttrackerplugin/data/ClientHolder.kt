package tel.jeelpa.anilisttrackerplugin.data

import com.apollographql.apollo3.ApolloClient
import okhttp3.OkHttpClient
import tel.jeelpa.anilisttrackerplugin.models.AnilistData
import tel.jeelpa.plugininterface.storage.UserStorage
import tel.jeelpa.plugininterface.tracker.repository.ClientHolder

class ClientHolderImpl(
    userStorage: UserStorage,
    private val anilistClient: ApolloClient = buildAnilistApolloClient(),
    okHttpClient: OkHttpClient = buildOkHttpClient(),
) : ClientHolder() {
    override val uniqueId = "ANILIST"

    companion object {
        private fun buildAnilistApolloClient() = ApolloClient.Builder()
            .serverUrl("https://graphql.anilist.co")
            .build()

        private fun buildOkHttpClient() = OkHttpClient.Builder()
            .build()

        private val anilistData = AnilistData(
            "id",
            "secret",
            "redirect"
        )
    }

    // can be static, it doesn't hold any cache
    override val userClient = TrackerClientImpl(anilistData, okHttpClient, userStorage)
    override val mangaClient = MangaClientImpl(anilistClient)
    override val characterClient = CharacterClientImpl(anilistClient)

    // these clients hold cache and need to get Re-Instantiated on every use to delete cache
    override val animeClient
        get() = AnimeClientImpl(anilistClient)
}