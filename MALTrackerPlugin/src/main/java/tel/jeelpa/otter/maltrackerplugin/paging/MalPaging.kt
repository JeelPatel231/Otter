package tel.jeelpa.otter.maltrackerplugin.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import tel.jeelpa.otter.maltrackerplugin.models.CharacterNode
import tel.jeelpa.otter.maltrackerplugin.models.MalResponse
import tel.jeelpa.otter.maltrackerplugin.models.MediaListNode
import tel.jeelpa.plugininterface.tracker.models.CharacterCardData
import tel.jeelpa.plugininterface.tracker.models.MediaCardData
import java.io.IOException

class MalPagingSource(
    private val dataRequest: suspend (Int, Int) -> MalResponse<MediaListNode>,
    private val startOffset: Int = 1,
    private val pageSize: Int = 30,
) : PagingSource<Int, MediaCardData>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaCardData> {
        // Start refresh at position 1 if undefined.
        val position = params.key ?: startOffset
        val offset = if (params.key != null) ((position - 1) * pageSize) + 1 else startOffset
        return try {
            val response = dataRequest(offset, params.loadSize)
            val nextKey = if(response.paging.next.isNullOrEmpty()){
                null
            } else {
                position + (params.loadSize / pageSize)
            }

            LoadResult.Page(
                data =  response.data.map { it.toMediaCardData() },
                prevKey = null, // Only paging forward.
                // assume that if a full page is not loaded, that means the end of the data
                nextKey = nextKey
            )
        } catch (e: IOException) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

    // The refresh key is used for subsequent refresh calls to PagingSource.load after the initial load
    override fun getRefreshKey(state: PagingState<Int, MediaCardData>): Int? {
        return null // i have no idea how to implement refresh key for offset-limit APIs, for now
    }
}


// REDUNDANT

class MalCharacterPagingSource(
    private val dataRequest: suspend (Int, Int) -> MalResponse<CharacterNode>,
    private val startOffset: Int = 1,
    private val pageSize: Int = 30,
) : PagingSource<Int, CharacterCardData>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharacterCardData> {
        // Start refresh at position 1 if undefined.
        val position = params.key ?: startOffset
        val offset = if (params.key != null) ((position - 1) * pageSize) + 1 else startOffset
        return try {
            val response = dataRequest(offset, params.loadSize)
            val nextKey = if(response.paging.next.isNullOrEmpty()){
                null
            } else {
                position + (params.loadSize / pageSize)
            }

            LoadResult.Page(
                data =  response.data.map { it.toCharacterCard() },
                prevKey = null, // Only paging forward.
                // assume that if a full page is not loaded, that means the end of the data
                nextKey = nextKey
            )
        } catch (e: IOException) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

    // The refresh key is used for subsequent refresh calls to PagingSource.load after the initial load
    override fun getRefreshKey(state: PagingState<Int, CharacterCardData>): Int? {
        return null // i have no idea how to implement refresh key for offset-limit APIs, for now
    }
}
