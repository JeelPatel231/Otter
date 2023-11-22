package tel.jeelpa.otter.ui.generic

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
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
