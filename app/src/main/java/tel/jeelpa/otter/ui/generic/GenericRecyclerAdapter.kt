package tel.jeelpa.otter.ui.generic

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.flow.Flow
import tel.jeelpa.otter.ui.customviews.ShimmerRecyclerView

abstract class GenericRecyclerAdapter<TData, TBindingType : ViewBinding>(
    private val inflateCallback: (LayoutInflater, ViewGroup?, Boolean) -> TBindingType,
    initialData: Collection<TData> = emptyList(),
) : RecyclerView.Adapter<GenericRecyclerAdapter.ViewHolder<TData, TBindingType>>() {

    private var _data: MutableList<TData> = initialData.toMutableList()

    val data: List<TData>
        get() = _data

    fun addAll(newData: Collection<TData>) {
        val oldDataSize = data.size
        _data.addAll(newData)
        notifyItemRangeInserted(oldDataSize, newData.size)
    }

    fun add(newData: TData) {
        _data.add(newData)
        notifyItemRangeInserted(data.size - 1, data.size)
    }

    fun setData(newData: Collection<TData>) {
        val oldSize = data.size
        _data = newData.toMutableList()
        notifyItemRangeRemoved(0, oldSize)
        notifyItemRangeInserted(0, data.size)
    }

    override fun getItemCount() = data.size


    class ViewHolder<TData, TBindingType : ViewBinding>(
        private val binding: TBindingType,
        private val callback: (TBindingType, TData, Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(entry: TData, position: Int) {
            callback(binding, entry, position)
        }
    }

    abstract fun onBind(binding: TBindingType, entry: TData, position: Int)
    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ViewHolder<TData, TBindingType> {
        val binding = inflateCallback(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding, this::onBind)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder<TData, TBindingType>, position: Int) {
        viewHolder.bind(data[position], position)
    }
}

class DataClassDiffCallback<TComparable, TData: tel.jeelpa.plugininterface.tracker.models.Equitable>(
    private val getPk: TData.() -> TComparable
): DiffUtil.ItemCallback<TData>() {
    override fun areItemsTheSame(oldItem: TData, newItem: TData): Boolean {
        return oldItem.getPk() == newItem.getPk()
    }

    override fun areContentsTheSame(oldItem: TData, newItem: TData): Boolean {
        return oldItem == newItem
    }
}

abstract class GenericListAdapter<TComparable, TData: tel.jeelpa.plugininterface.tracker.models.Equitable, TBindingType: ViewBinding>(
    primaryKey: TData.() -> TComparable
): ListAdapter<TData, GenericListAdapter.ViewHolder<TData, TBindingType>>(
    DataClassDiffCallback<TComparable, TData>(primaryKey)
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
        holder.bind(currentList[position], position)
    }

}


fun <TFlowSource, TData, TPrimitive, TBinding, TAdapter : GenericListAdapter<TPrimitive, TData, TBinding>>
Fragment.initRecycler(
    adapter: TAdapter,
    shimmerRecycler : ShimmerRecyclerView,
    flowSource: Flow<TFlowSource?>,
    flowData: (TFlowSource) -> List<TData>
) {
    shimmerRecycler.setAdapter(adapter)

    flowSource.observeFlow(viewLifecycleOwner) {
        it?.let {
            adapter.submitList(flowData(it))
        }
    }
}

// when the list itself is the source of recycler view items
fun <TData, TFlowSource : List<TData>, TPrimitive, TBinding, TAdapter : GenericListAdapter<TPrimitive, TData, TBinding>>
Fragment.initRecycler(
    adapter: TAdapter,
    shimmerRecycler: ShimmerRecyclerView,
    flowSource: Flow<TFlowSource?>,
) = initRecycler(adapter, shimmerRecycler, flowSource) { it }



fun <TData: Any, TFlowSource, TPrimitive, TBinding, TAdapter : GenericPagingAdapter<TPrimitive, TData, TBinding>>
Fragment.initPagedRecycler(
    adapter: TAdapter,
    view: ShimmerRecyclerView,
    flowSource: Flow<TFlowSource>,
    flowData: (TFlowSource) -> PagingData<TData>
) {
    view.setAdapter(adapter)

    flowSource.observeFlow(viewLifecycleOwner) {
        adapter.submitData(flowData(it))
    }

    adapter.loadStateFlow.observeFlow(viewLifecycleOwner) {
        when(val currentState = it.refresh) {
            is LoadState.Error -> showToast(currentState.error.message ?: "Error Loading Paged Data")
            else -> {}
        }
    }
}

fun <TData : Any, TFlowSource : PagingData<TData>, TPrimitive, TBinding, TAdapter : GenericPagingAdapter<TPrimitive, TData, TBinding>>
Fragment.initPagedRecycler(
    adapter: TAdapter,
    view: ShimmerRecyclerView,
    flowSource: Flow<TFlowSource>,
) = initPagedRecycler(adapter, view, flowSource) { it }
