package tel.jeelpa.otter.ui.fragments.animedetails

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tel.jeelpa.otter.databinding.SimpleTextViewBinding
import tel.jeelpa.otter.reference.models.Episode
import tel.jeelpa.otter.ui.generic.GenericRecyclerAdapter

class EpisodeAdapter(
    private val lifecycleCoroutineScope: LifecycleCoroutineScope,
    private val onItemClick : suspend (Episode) -> Unit
): GenericRecyclerAdapter<Episode, SimpleTextViewBinding>(SimpleTextViewBinding::inflate) {
    private var scrapingJob: Job? = null
    override fun onBind(binding: SimpleTextViewBinding, entry: Episode, position: Int) {
        binding.root.setOnClickListener {
            scrapingJob?.cancel()
            scrapingJob = lifecycleCoroutineScope.launch {
                onItemClick(entry)
            }
        }
        binding.simpleTextView.text = "Episode ${position + 1} : ${entry.title}"
    }
}
