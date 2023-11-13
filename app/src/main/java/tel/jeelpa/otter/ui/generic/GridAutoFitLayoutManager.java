package tel.jeelpa.otter.ui.generic;

import android.content.Context;
import android.util.TypedValue;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


//https://gist.github.com/omidraha/af3aa017d4ec06342bdc03c49d4b83b1#file-gridautofitlayoutmanager-java
public class GridAutoFitLayoutManager extends GridLayoutManager {
    private int mColumnWidth;
    private boolean mColumnWidthChanged = true;
    private boolean mWidthChanged = true;
    private int mWidth;

    // columnWidth is IN DP
    public GridAutoFitLayoutManager(Context context, int columnWidth) throws Exception {
        /* Initially set spanCount to 1, will be changed automatically later. */
        super(context, 1);
        setColumnWidth(checkedColumnWidth(context, columnWidth));
    }

    public GridAutoFitLayoutManager(Context context, int columnWidth, int orientation, boolean reverseLayout) throws Exception {
        /* Initially set spanCount to 1, will be changed automatically later. */
        super(context, 1, orientation, reverseLayout);
        setColumnWidth(checkedColumnWidth(context, columnWidth));
    }

    private int checkedColumnWidth(Context context, int columnWidth) throws Exception {
        if (columnWidth <= 0) {
            throw new Exception("Column Width less than 0");
        }

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, columnWidth,
                context.getResources().getDisplayMetrics());
    }

    private void setColumnWidth(int newColumnWidth) {
        if (newColumnWidth > 0 && newColumnWidth != mColumnWidth) {
            mColumnWidth = newColumnWidth;
            mColumnWidthChanged = true;
        }
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        int width = getWidth();
        int height = getHeight();

        if (width != mWidth) {
            mWidthChanged = true;
            mWidth = width;
        }

        if (mColumnWidthChanged
                && mColumnWidth > 0
                && width > 0
                && height > 0
                || mWidthChanged) {
            int totalSpace;
            if (getOrientation() == VERTICAL) {
                totalSpace = width - getPaddingRight() - getPaddingLeft();
            } else {
                totalSpace = height - getPaddingTop() - getPaddingBottom();
            }
            int spanCount = Math.max(1, totalSpace / mColumnWidth);
            setSpanCount(spanCount);
            mColumnWidthChanged = false;
            mWidthChanged = false;
        }
        super.onLayoutChildren(recycler, state);
    }
}