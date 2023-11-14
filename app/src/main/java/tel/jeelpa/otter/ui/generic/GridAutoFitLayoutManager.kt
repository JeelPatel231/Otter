package tel.jeelpa.otter.ui.generic

import android.content.Context
import android.util.TypedValue
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

//https://gist.github.com/omidraha/af3aa017d4ec06342bdc03c49d4b83b1#file-gridautofitlayoutmanager-java
class GridAutoFitLayoutManager : GridLayoutManager {
    private var mColumnWidth = 0
    private var mColumnWidthChanged = true
    private var mWidthChanged = true
    private var mWidth = 0

    // columnWidth is IN DP
    constructor(context: Context, columnWidth: Int) : super(context, 1) {
        /* Initially set spanCount to 1, will be changed automatically later. */
        setColumnWidth(checkedColumnWidth(context, columnWidth))
    }

    constructor(
        context: Context,
        columnWidth: Int,
        orientation: Int,
        reverseLayout: Boolean
    ) : super(context, 1, orientation, reverseLayout) {
        /* Initially set spanCount to 1, will be changed automatically later. */
        setColumnWidth(checkedColumnWidth(context, columnWidth))
    }

    private fun checkedColumnWidth(context: Context, columnWidth: Int): Int {
        if (columnWidth <= 0) {
            throw Exception("Column Width less than 0")
        }
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, columnWidth.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }

    private fun setColumnWidth(newColumnWidth: Int) {
        if (newColumnWidth > 0 && newColumnWidth != mColumnWidth) {
            mColumnWidth = newColumnWidth
            mColumnWidthChanged = true
        }
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        val width = width
        val height = height
        if (width != mWidth) {
            mWidthChanged = true
            mWidth = width
        }
        if (mColumnWidthChanged
            && mColumnWidth > 0
            && width > 0
            && height > 0
            || mWidthChanged
        ) {
            val totalSpace = if (orientation == VERTICAL) {
                width - paddingRight - paddingLeft
            } else {
                height - paddingTop - paddingBottom
            }
            val spanCount = (totalSpace / mColumnWidth).coerceAtLeast(1)
            setSpanCount(spanCount)
            mColumnWidthChanged = false
            mWidthChanged = false
        }
        super.onLayoutChildren(recycler, state)
    }
}