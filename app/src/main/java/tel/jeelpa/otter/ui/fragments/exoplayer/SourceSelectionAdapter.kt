package tel.jeelpa.otter.ui.fragments.exoplayer

import tel.jeelpa.otter.databinding.SimpleTextViewBinding
import tel.jeelpa.otter.ui.generic.GenericRecyclerAdapter
import tel.jeelpa.plugininterface.models.Video

class SourceSelectionAdapter(
    private val onItemClick : (Video) -> Unit
): GenericRecyclerAdapter<Video, SimpleTextViewBinding>(SimpleTextViewBinding::inflate) {
    override fun onBind(binding: SimpleTextViewBinding, entry: Video, position: Int) {
        binding.root.setOnClickListener {
            onItemClick(entry)
        }
        binding.simpleTextView.text = entry.title
    }
}
