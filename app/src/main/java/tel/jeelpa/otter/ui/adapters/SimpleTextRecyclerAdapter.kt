package tel.jeelpa.otter.ui.adapters

import tel.jeelpa.otter.databinding.SimpleTextViewBinding
import tel.jeelpa.otter.ui.generic.GenericRecyclerAdapter

class SimpleTextRecyclerAdapter(
    private val onItemClick : (String) -> Unit = {},
    private val onItemLongClick : (String) -> Unit = {},
    listData: List<String> = emptyList()
): GenericRecyclerAdapter<String, SimpleTextViewBinding>(
    SimpleTextViewBinding::inflate, listData
){
    override fun onBind(binding: SimpleTextViewBinding, entry: String, position: Int) {
        binding.simpleTextView.text = entry
        binding.simpleTextView.setOnClickListener {
            onItemClick(entry)
        }
        binding.simpleTextView.setOnLongClickListener {
            onItemLongClick(entry)
            true
        }
    }
}