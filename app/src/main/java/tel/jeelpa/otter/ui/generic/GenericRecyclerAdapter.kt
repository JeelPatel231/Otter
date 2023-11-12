package tel.jeelpa.otter.ui.generic

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.facebook.shimmer.ShimmerFrameLayout
import kotlinx.coroutines.flow.Flow
import tel.jeelpa.otterlib.models.Equitable

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
        _data = newData.toMutableList()
        notifyDataSetChanged()
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

class DataClassDiffCallback<TComparable, TData: Equitable>(
    private val getPk: TData.() -> TComparable
): DiffUtil.ItemCallback<TData>() {
    override fun areItemsTheSame(oldItem: TData, newItem: TData): Boolean {
        return oldItem.getPk() == newItem.getPk()
    }

    override fun areContentsTheSame(oldItem: TData, newItem: TData): Boolean {
        return oldItem == newItem
    }
}

abstract class GenericListAdapter<TComparable, TData: Equitable, TBindingType: ViewBinding>(
    private val inflateCallback: (LayoutInflater, ViewGroup?, Boolean) -> TBindingType,
    getPk: TData.() -> TComparable
): ListAdapter<TData, GenericListAdapter.ViewHolder<TData, TBindingType>>(
    DataClassDiffCallback<TComparable, TData>(getPk)
) {
    class ViewHolder<TData, TBindingType : ViewBinding>(
        private val binding: TBindingType,
        private val callback: (TBindingType, TData, Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(entry: TData, position: Int) {
            callback(binding, entry, position)
        }
    }

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
    recyclerView: RecyclerView,
    shimmerFrameLayout: ShimmerFrameLayout,
    flowSource: Flow<TFlowSource?>,
    flowData: (TFlowSource) -> List<TData>
) {
    if (adapter.getCurrentList().isNotEmpty())
        shimmerFrameLayout.visibilityGone()

    recyclerView.adapter = adapter
    recyclerView.layoutManager =
        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

    flowSource.observeFlow(viewLifecycleOwner) {
        it?.let {
            adapter.submitList(flowData(it))
            crossfadeViews(recyclerView, shimmerFrameLayout)
        }
    }
}

// when the list itself is the source of recycler view items
fun <TData, TFlowSource : List<TData>, TPrimitive, TBinding, TAdapter : GenericListAdapter<TPrimitive, TData, TBinding>> Fragment.initRecycler(
    adapter: TAdapter,
    recyclerView: RecyclerView,
    shimmerFrameLayout: ShimmerFrameLayout,
    flowSource: Flow<TFlowSource?>,
) = initRecycler(adapter, recyclerView, shimmerFrameLayout, flowSource) { it }
