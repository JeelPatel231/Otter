package tel.jeelpa.otter.maltrackerplugin.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import tel.jeelpa.otter.maltrackerplugin.models.CharacterData
import tel.jeelpa.otter.maltrackerplugin.models.CharacterNode
import tel.jeelpa.otter.maltrackerplugin.models.MalResponse
import tel.jeelpa.otter.maltrackerplugin.paging.MalCharacterPagingSource
import tel.jeelpa.plugininterface.helpers.parsed
import tel.jeelpa.plugininterface.tracker.models.CharacterCardData
import tel.jeelpa.plugininterface.tracker.models.CharacterDataFull
import tel.jeelpa.plugininterface.tracker.repository.CharacterClient

class MalCharacterClient(
    okHttpClient: OkHttpClient
):BaseMalClient(okHttpClient), CharacterClient {

    ///////////////// THESE ARE REDUNDANT CODE COPIES, ADD GENERICS TO THE BASE CLIENT TO REMOVE THIS
    private fun delegateToCharacterPagingSource(
        dataCallback: suspend (Int, Int) -> MalResponse<CharacterNode>,
    ): Flow<PagingData<CharacterCardData>> {
        return Pager(
            config = PagingConfig(
                30,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MalCharacterPagingSource(dataCallback) }
        ).flow
    }

    private fun makePagedCharacterQuery(builderParams: HttpUrl.Builder.() -> Unit) =
        delegateToCharacterPagingSource { offset, limit ->
            val pagedQueryParam: HttpUrl.Builder.() -> Unit = {
                apply(builderParams)
                addQueryParameter("limit", limit.toString())
                addQueryParameter("offset", offset.toString())
            }
            makeMalQuery(pagedQueryParam).parsed()
        }

    ////////////////////////////

    private fun getCharacterFromMedia(id: Int, mediaType: String): Flow<PagingData<CharacterCardData>>{
        return makePagedCharacterQuery {
            addPathSegment(mediaType)
            addPathSegment(id.toString())
            addPathSegment("characters")
            addQueryParameter("fields", "id,first_name,last_name,main_picture")
        }
    }
    override fun getCharactersFromAnime(id: Int) = getCharacterFromMedia(id, "anime")

    override fun getCharactersFromManga(id: Int) = getCharacterFromMedia(id, "manga")

    override suspend fun getCharacterDetails(id: Int): CharacterDataFull {
        return makeMalQuery {
            addPathSegment("characters")
            addPathSegment(id.toString())
            addQueryParameter("fields", "id,first_name,last_name,alternative_name,main_picture,biography")
        }.parsed<CharacterData>().toCharacterFullData()
    }
}