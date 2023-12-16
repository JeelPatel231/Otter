package tel.jeelpa.anilisttrackerplugin.data

import androidx.paging.PagingData
import com.apollographql.apollo3.ApolloClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import tel.jeelpa.anilisttrackerplugin.models.toApp
import tel.jeelpa.otter.anilisttrackerplugin.models.GetCharacterDataQuery
import tel.jeelpa.otter.anilisttrackerplugin.models.GetCharactersFromMediaQuery
import tel.jeelpa.plugininterface.tracker.models.CharacterCardData
import tel.jeelpa.plugininterface.tracker.models.CharacterDataFull
import tel.jeelpa.plugininterface.tracker.models.MediaCardData
import tel.jeelpa.plugininterface.tracker.repository.CharacterClient

class CharacterClientImpl(
    private val apolloClient: ApolloClient
) : CharacterClient {

    private fun getCharacterFromMedia(id:Int): Flow<PagingData<CharacterCardData>> = flow {
        val characters = apolloClient.query(GetCharactersFromMediaQuery(id)).execute()
            .data?.Media?.characters?.edges
            ?.map { Pair(it?.role, it?.node) }
            ?.mapNotNull { (role, char) ->
                role ?: return@mapNotNull null
                char ?: return@mapNotNull null

                CharacterCardData(
                    id = char.id,
                    name = char.name?.full!!,
                    avatar = char.image?.medium!!,
                    role = role.name
                )
        } ?: emptyList()
        emit(PagingData.from(characters))
    }

    override fun getCharactersFromAnime(id: Int) = getCharacterFromMedia(id)

    override fun getCharactersFromManga(id: Int) = getCharacterFromMedia(id)

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
                    episodesAired = m.nextAiringEpisode?.episode?.minus(1),
                    episodes = m.episodes,
                    chapters = m.chapters,
                    userWatched = m.mediaListEntry?.progress,
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