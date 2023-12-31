package tel.jeelpa.otter.ui.fragments.mediaCommon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.load
import coil.size.Scale
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.Flow
import tel.jeelpa.otter.databinding.MediaSmallLayoutBinding
import tel.jeelpa.otter.databinding.WrongTitleSelectionBottomSheetBinding
import tel.jeelpa.otter.ui.generic.GenericRecyclerAdapter
import tel.jeelpa.otter.ui.generic.GridAutoFitLayoutManager
import tel.jeelpa.otter.ui.generic.autoCleared
import tel.jeelpa.otter.ui.generic.observeFlow
import tel.jeelpa.otter.ui.generic.visibilityGone
import tel.jeelpa.plugininterface.models.ShowResponse


class ShowResponseAdapter(
    private val onItemClick : (ShowResponse) -> Unit
): GenericRecyclerAdapter<ShowResponse, MediaSmallLayoutBinding>(MediaSmallLayoutBinding::inflate) {
    override fun onBind(binding: MediaSmallLayoutBinding, entry: ShowResponse, position: Int) {
        binding.root.setOnClickListener {
            onItemClick(entry)
        }

        binding.coverImage.load(entry.coverUrl.url){
            scale(Scale.FILL)
        }
        binding.title.text = entry.name
        binding.totalCount.visibilityGone()
        binding.metadataHolder.visibilityGone()
    }
}

class WrongMediaSelectionBottomSheetDialog(
    private val liveData: Flow<List<ShowResponse>>,
    private val onItemClick: (ShowResponse) -> Unit,
    private val onSearchClick: (String) -> Unit,
) : BottomSheetDialogFragment() {
    private var binding: WrongTitleSelectionBottomSheetBinding by autoCleared()
    override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = WrongTitleSelectionBottomSheetBinding.inflate(inflater, container, false)

        binding.dismissButton.setOnClickListener {
            dismiss()
        }

        binding.searchButton.setOnClickListener {
            onSearchClick(binding.searchQuery.text.toString())
        }

        val sourcesAdapter = ShowResponseAdapter {
            onItemClick(it)
            dismiss()
        }

        liveData.observeFlow(viewLifecycleOwner) {
            sourcesAdapter.setData(it)
        }

        binding.sourceSelectionRecyclerView.apply {
            adapter = sourcesAdapter
            layoutManager = GridAutoFitLayoutManager(requireContext(), 110)
        }

        return binding.root
    }
}
