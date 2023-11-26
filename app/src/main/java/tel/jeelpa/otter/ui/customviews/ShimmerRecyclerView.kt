package tel.jeelpa.otter.ui.customviews

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import tel.jeelpa.otter.databinding.MediaCardShimmerListBinding
import tel.jeelpa.otter.ui.generic.crossfadeViews
import tel.jeelpa.otter.ui.generic.fadeIn
import tel.jeelpa.otter.ui.generic.fadeOut
import tel.jeelpa.otter.ui.generic.visibilityGone

class ShimmerRecyclerView(context: Context, attrSet: AttributeSet?, defStyleAttr: Int) :
    FrameLayout(context, attrSet, defStyleAttr) {

    val Int.dp: Int
        get() = (this / Resources.getSystem().displayMetrics.density).toInt()
    val Int.px: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()

    constructor(context: Context, attrSet: AttributeSet?) : this(context, attrSet, 0)

    private fun View.applyDefaultParams() = apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        setPadding(18.px, 0, 18.px, 0)
    }

    private fun handleOnChange(itemCount: Int) {
        if (!firstLoad) {
            crossfadeViews(recycler, shimmer.root)
            firstLoad = true
        }

        if (itemCount == 0) {
            onEmptyView?.fadeIn()
        } else if(onEmptyView?.visibility == View.VISIBLE){
            onEmptyView?.fadeOut()
        }
    }

    private val recycler: RecyclerView
    private val shimmer: MediaCardShimmerListBinding

    fun <T: Any, VH: ViewHolder>setAdapter(adapter: PagingDataAdapter<T, VH>) {
        recycler.adapter = adapter

        adapter.addOnPagesUpdatedListener { handleOnChange(adapter.itemCount) }
    }


    fun <VH: ViewHolder>setAdapter(adapter: RecyclerView.Adapter<VH>) {

        adapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            // NOTE: THIS ONLY WORKS WHEN WE USE Adapter#notifyItemRangeInserted
            // i cant seem to find any generic method that notifies about any data change
            // regardless of the method used
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                handleOnChange(adapter.itemCount)
            }
        })

        recycler.adapter = adapter
    }

    private var onEmptyView: View? = null
    fun setOnEmptyView(view: View) {
        view.visibilityGone()
        onEmptyView = view.applyDefaultParams()
        addView(view)
    }

    private var firstLoad = false

    init {
        val layoutInflator = LayoutInflater.from(context)

        recycler = RecyclerView(context).apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            clipToPadding = false
            isNestedScrollingEnabled = false
        }

        shimmer = MediaCardShimmerListBinding.inflate(layoutInflator)

        addView(recycler.applyDefaultParams())
        addView(shimmer.root.applyDefaultParams())
    }

}