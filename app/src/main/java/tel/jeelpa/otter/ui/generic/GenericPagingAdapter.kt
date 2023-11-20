package tel.jeelpa.otter.ui.generic

import android.net.http.HttpException
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresExtension
import androidx.paging.PagingDataAdapter
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import okio.IOException
import tel.jeelpa.plugininterface.tracker.models.Equitable


abstract class GenericPagingAdapter<TComparable, TData: Equitable, TBindingType: ViewBinding>(
    primaryKey: TData.() -> TComparable
): PagingDataAdapter<TData, GenericPagingAdapter.ViewHolder<TData, TBindingType>>(
    DataClassDiffCallback(primaryKey)
) {
    class ViewHolder<TData, TBindingType : ViewBinding>(
        private val binding: TBindingType,
        private val callback: (TBindingType, TData, Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(entry: TData, position: Int) {
            callback(binding, entry, position)
        }
    }

    abstract fun inflateCallback(layoutInflator: LayoutInflater, viewGroup: ViewGroup?, attachToParent: Boolean): TBindingType

    abstract fun onBind(binding: TBindingType, entry: TData, position: Int)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<TData, TBindingType> {
        val binding = inflateCallback(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, this::onBind)
    }

    override fun onBindViewHolder(holder: ViewHolder<TData, TBindingType>, position: Int) {
        val currItem = getItem(position) ?: return
        holder.bind(currItem, position)
    }

}


class GenericPagingSource<TData : Any>(
    private val dataRequest: suspend (Int, Int) -> List<TData>,
    private val startPage: Int = 1,
    private val pageSize: Int = 30
) : PagingSource<Int, TData>() {

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
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
        } catch (exception: HttpException) {
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
