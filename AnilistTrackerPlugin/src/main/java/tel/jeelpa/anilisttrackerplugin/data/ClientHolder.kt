package tel.jeelpa.anilisttrackerplugin.data

import com.apollographql.apollo3.ApolloClient
import okhttp3.OkHttpClient
import tel.jeelpa.anilisttrackerplugin.models.AnilistData
import tel.jeelpa.otter.trackerinterface.repository.ClientHolder
import tel.jeelpa.otter.trackerinterface.repository.UserStorage

class ClientHolderImpl(
    userStorage: UserStorage,
    anilistClient: ApolloClient = buildAnilistApolloClient(),
    okHttpClient: OkHttpClient = buildOkHttpClient(),
) : ClientHolder {
    override val uniqueId = "ANILIST"

    companion object {
        private fun buildAnilistApolloClient()  = ApolloClient.Builder()
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

    override val userClient = TrackerClientImpl(anilistData, okHttpClient, userStorage)
    override val animeClient = AnimeClientImpl(anilistClient)
    override val mangaClient = MangaClientImpl(anilistClient)
    override val characterClient = CharacterClientImpl(anilistClient)
}