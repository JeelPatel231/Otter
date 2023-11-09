package tel.jeelpa.otter.ui.customviews

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.parcelize.Parcelize
import tel.jeelpa.otter.R
import tel.jeelpa.otter.ui.generic.GenericRecyclerAdapter

@Parcelize
class TabbedRecyclerViewState(
    private val superSavedState: Parcelable?,
    val selectedChipIndex: Int
) : View.BaseSavedState(superSavedState), Parcelable


class TabbedRecyclerView<TData, TBinding : ViewBinding>(
    context: Context,
    attrSet: AttributeSet? = null,
    defStyleAttr: Int,
) : LinearLayout(context, attrSet, defStyleAttr) {
    constructor(context: Context, attrSet: AttributeSet?) : this(context, attrSet, 0)

    private lateinit var adapter: GenericRecyclerAdapter<TData, TBinding>

    fun setAdapter(adapter: GenericRecyclerAdapter<TData, TBinding>) {
        this.adapter = adapter
        recyclerView.adapter = adapter
    }

    fun setLayoutManager(llm: LinearLayoutManager) {
        recyclerView.layoutManager = llm
    }

    private var selectedChipIndex = 0
    private var recyclerView: RecyclerView
    private var episodesChipGroup: ChipGroup
    private var currentChunkedData: List<List<TData>> = emptyList()


    private fun selectChip(idx: Int) {
        selectedChipIndex = idx
        adapter.setData(currentChunkedData[idx])
    }

    private fun clearChips() {
        episodesChipGroup.removeAllViews() // children.forEach { removeView(it) }
    }

    fun setData(allData: List<TData>) {
        // reset the index
        selectedChipIndex = 0
        clearChips()
        // chunk this into parts, 25 for now, TODO: auto-calculate the chunk size
        val chunkedData = allData.chunked(25)
        when (val first = chunkedData.firstOrNull()) {
            null -> adapter.setData(emptyList())
            else -> adapter.setData(first)
        }
        currentChunkedData = chunkedData

        if (chunkedData.size <= 1) return
        chunkedData
            .mapIndexed { idx, item ->
                Chip(context).apply {
                    isChecked = true
                    text = "${item.first()} - ${item.last()}"
                    setOnClickListener {
                        // change the data in recycler view adapter
                        selectChip(idx)
                    }
                }
            }.forEach {
                episodesChipGroup.addView(it)
            }

    }


    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        return TabbedRecyclerViewState(superState, selectedChipIndex)
    }


    override fun onRestoreInstanceState(state: Parcelable?) {
        val myState = state as? TabbedRecyclerViewState
        super.onRestoreInstanceState(myState?.superState ?: state)

        selectedChipIndex = myState?.selectedChipIndex ?: 0
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.tabbed_recycler_layout, this, true)
        episodesChipGroup = findViewById(R.id.episodes_chip_group)
        recyclerView = findViewById(R.id.episodes_recycler_view)
    }
}