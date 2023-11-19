package tel.jeelpa.anilisttrackerplugin.data

import com.apollographql.apollo3.ApolloClient
import tel.jeelpa.anilisttrackerplugin.models.toApp
import tel.jeelpa.otter.anilisttrackerplugin.models.GetCharacterDataQuery
import tel.jeelpa.plugininterface.tracker.models.CharacterDataFull
import tel.jeelpa.plugininterface.tracker.models.MediaCardData
import tel.jeelpa.plugininterface.tracker.repository.CharacterClient

class CharacterClientImpl(
    private val apolloClient: ApolloClient
) : CharacterClient {
    override suspend fun getCharacterDetails(id: Int): CharacterDataFull {
        return apolloClient.query(GetCharacterDataQuery(id)).execute().data?.Character?.let {
            val mediaRoles = it.media?.edges?.mapNotNull { media ->
                val m = media?.node ?: return@mapNotNull null
                MediaCardData(
                    id = m.id,
                    title = m.title?.english ?: m.title?.romaji ?: m.title?.userPreferred!!,
                    status = m.status!!.toApp(),
                    type = m.type!!.toApp(),
                    isAdult = m.isAdult ?: false,
                    meanScore = (m.meanScore ?: 0) / 10f,
                    coverImage = m.coverImage?.large!!,
                    nextAiringEpisode = m.nextAiringEpisode?.episode,
                    episodes = m.episodes,
                    chapters = m.chapters,
                )
            } ?: emptyList()

            CharacterDataFull(
                id = it.id,
                name = it.name?.full!!,
                avatar = it.image?.large!!,
                age = it.age ?: "Unknown",
                gender = it.gender ?: "Unknown",
                description = it.description ?: "",
                dateOfBirth = it.dateOfBirth?.toApp(),
                media = mediaRoles
            )
        } ?: throw Exception("Failed to get Character Information")
    }
}