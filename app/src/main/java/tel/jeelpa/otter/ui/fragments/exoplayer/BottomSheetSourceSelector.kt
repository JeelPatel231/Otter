package tel.jeelpa.otter.ui.fragments.exoplayer

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import tel.jeelpa.otter.databinding.SourceSelectionBottomSheetBinding
import tel.jeelpa.otter.ui.generic.autoCleared

class BottomSheetSourceSelector(
    private val sourcesAdapter: SourceSelectionAdapter
) : BottomSheetDialogFragment() {
    private var binding: SourceSelectionBottomSheetBinding by autoCleared()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val createdDialog = super.onCreateDialog(savedInstanceState)
        // Make it fully expand when shown
        createdDialog.setOnShowListener {
            val bottomSheet = createdDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
        }

        return createdDialog
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SourceSelectionBottomSheetBinding.inflate(inflater, container, false)

        binding.sourceSelectionRecyclerView.apply {
            adapter = sourcesAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        return binding.root
    }
}
