package tel.jeelpa.anilisttrackerplugin.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import java.io.IOException

class AnilistPagingSource<TData : Any>(
    private val dataRequest: suspend (Int, Int) -> List<TData>,
    private val startPage: Int = 1,
    private val pageSize: Int = 30
) : PagingSource<Int, TData>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TData> {
        val pageNumber = params.key ?: startPage
        return try {
            val items = dataRequest(pageNumber, params.loadSize)
            val nextKey = if (items.isEmpty()) {
                null
            } else {
                // initial load size = 3 * NETWORK_PAGE_SIZE
                // ensure we're not requesting duplicating items, at the 2nd request
                pageNumber + (params.loadSize / pageSize)
            }
            LoadResult.Page(
                data = items,
                prevKey = if (pageNumber == startPage) null else pageNumber - 1,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: Throwable){ //HttpException) {
            return LoadResult.Error(exception)
        }
    }
    // The refresh key is used for subsequent refresh calls to PagingSource.load after the initial load
    override fun getRefreshKey(state: PagingState<Int, TData>): Int? {
        // We need to get the previous key (or next key if previous is null) of the page
        // that was closest to the most recently accessed index.
        // Anchor position is the most recently accessed index
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
