package tel.jeelpa.otter.ui.generic

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter


// https://rmirabelle.medium.com/there-is-no-material-design-spinner-for-android-3261b7c77da8

/**
 * Almost unbelievably, if we want to create a Material Spinner,
 * we're forced to subclass ArrayAdapter.  That statement alone
 * is maddening.  The issue is that there's no such thing as a Material
 * Spinner. Instead, there's an Exposed Dropdown Menu, which is really
 * an AutoCompleteTextView wrapped in a TextInputLayout, which replaces a
 * Spinner. The reason we have to subclass ArrayAdapter is because we need
 * the AutoCompleteTextView to act like a proper Spinner.  Thus we have to
 * override the AutoCompleteTextView's Filter so that it NEVER performs
 * filtering of the dropdown menu items.
 */
class MaterialSpinnerAdapter<String>(context: Context, layout: Int, var values: List<String>) :
    ArrayAdapter<String>(context, layout, values) {
    private val filterMock = object: Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()
            results.values = values
            results.count = values.size
            return results
        }
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            notifyDataSetChanged()
        }
    }

    override fun getFilter(): Filter {
        return filterMock
    }
}