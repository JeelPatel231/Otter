package tel.jeelpa.otter.ui.fragments.animedetails

import androidx.lifecycle.LifecycleCoroutineScope
import coil.load
import coil.size.Scale
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tel.jeelpa.otter.databinding.ItemEpisodeLayoutBinding
import tel.jeelpa.otter.reference.models.Episode
import tel.jeelpa.otter.ui.generic.GenericRecyclerAdapter
import tel.jeelpa.otter.ui.generic.nullOnBlank

class EpisodeAdapter(
    private val lifecycleCoroutineScope: LifecycleCoroutineScope,
    private val onItemClick : suspend (Episode) -> Unit
): GenericRecyclerAdapter<Episode, ItemEpisodeLayoutBinding>(ItemEpisodeLayoutBinding::inflate) {
    private var scrapingJob: Job? = null
    override fun onBind(binding: ItemEpisodeLayoutBinding, entry: Episode, position: Int) {
        binding.root.setOnClickListener {
            scrapingJob?.cancel()
            scrapingJob = lifecycleCoroutineScope.launch {
                onItemClick(entry)
            }
        }
        binding.titleHolder.text = entry.title?.nullOnBlank() ?: ""
        binding.numberHolder.text = entry.number.nullOnBlank()
        binding.coverHolder.load(entry.thumbnail){
            scale(Scale.FILL)
        }
    }
}
