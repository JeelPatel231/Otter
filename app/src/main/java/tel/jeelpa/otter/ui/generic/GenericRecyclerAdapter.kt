package tel.jeelpa.otter.ui.generic

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.facebook.shimmer.ShimmerFrameLayout
import kotlinx.coroutines.flow.Flow

abstract class GenericRecyclerAdapter<TData, TBindingType: ViewBinding>(
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
        notifyItemRangeInserted(data.size-1, data.size)
    }

    fun setData(newData: Collection<TData>) {
        _data = newData.toMutableList()
        notifyDataSetChanged()
    }

    override fun getItemCount() = data.size


    class ViewHolder<TData, TBindingType: ViewBinding>(
        private val binding: TBindingType,
        private val callback: (TBindingType, TData, Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root){

        fun bind(entry: TData, position: Int){
            callback(binding, entry, position)
        }
    }

    abstract fun onBind(binding: TBindingType, entry: TData, position: Int)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder<TData, TBindingType> {
        val binding = inflateCallback(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding, this::onBind)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder<TData, TBindingType>, position: Int) {
        viewHolder.bind(data[position], position)
    }
}

fun <TFlowSource, TData, TBinding, TAdapter: GenericRecyclerAdapter<TData, TBinding>> Fragment.initRecycler(
    adapter: TAdapter,
    recyclerView: RecyclerView,
    shimmerFrameLayout: ShimmerFrameLayout,
    flowSource: Flow<TFlowSource?>,
    flowData: (TFlowSource) -> Collection<TData>
){
    if(adapter.data.isNotEmpty())
        shimmerFrameLayout.visibilityGone()

    recyclerView.adapter = adapter
    recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

    flowSource.observeFlow(viewLifecycleOwner){
        it?.let {
            adapter.addAll(flowData(it))
            crossfadeViews(recyclerView, shimmerFrameLayout)
        }
    }
}

