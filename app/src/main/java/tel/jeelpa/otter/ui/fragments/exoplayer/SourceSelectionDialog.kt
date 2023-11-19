package tel.jeelpa.otter.ui.fragments.exoplayer

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.Flow
import tel.jeelpa.otter.databinding.SourceSelectionBottomSheetBinding
import tel.jeelpa.otter.ui.generic.autoCleared
import tel.jeelpa.otter.ui.generic.observeFlow
import tel.jeelpa.plugininterface.models.Video

class SourceSelectionDialog(
    private val liveData: Flow<List<Video>>,
    private val onItemClick: (Video) -> Unit,
) : DialogFragment() {
    private var binding: SourceSelectionBottomSheetBinding by autoCleared()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = SourceSelectionBottomSheetBinding.inflate(inflater, container, false)

        // set animation to dialog
        dialog?.window?.attributes?.windowAnimations = com.google.android.material.R.style.MaterialAlertDialog_Material3_Animation

        binding.dismissButton.setOnClickListener {
            dismiss()
        }

        val sourcesAdapter = SourceSelectionAdapter {
            onItemClick(it)
            dismiss()
        }

        liveData.observeFlow(viewLifecycleOwner) {
            sourcesAdapter.setData(it)
        }

        binding.sourceSelectionRecyclerView.apply {
            adapter = sourcesAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return ComponentDialog(requireContext(), android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen)
    }

}
