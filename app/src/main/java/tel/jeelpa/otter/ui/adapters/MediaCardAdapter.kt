package tel.jeelpa.otter.ui.adapters

import coil.load
import coil.size.Scale
import tel.jeelpa.otter.R
import tel.jeelpa.otter.databinding.MediaSmallLayoutBinding
import tel.jeelpa.otter.ui.generic.GenericListAdapter
import tel.jeelpa.otterlib.models.AppMediaType
import tel.jeelpa.otterlib.models.MediaCardData

class MediaCardAdapter(
    private val onItemClick : (MediaCardData) -> Unit
): GenericListAdapter<Int, MediaCardData, MediaSmallLayoutBinding>(
    MediaSmallLayoutBinding::inflate,
    { id }
) {
    override fun onBind(binding: MediaSmallLayoutBinding, entry: MediaCardData, position: Int) {
        binding.root.setOnClickListener {
            onItemClick(entry)
        }
        binding.coverImage.load(entry.coverImage){
            scale(Scale.FILL)
        }
        binding.score.text = entry.meanScore.toString()
        binding.title.text = entry.title
        binding.totalCount.text =
            when(entry.type){

                AppMediaType.ANIME -> binding.root.context.getString(
                    R.string.media_release_data,
                    "~",
                    (entry.nextAiringEpisode ?: entry.episodes ?: "~").toString() ,
                    (entry.episodes ?: "~").toString()
                )

                AppMediaType.MANGA -> binding.root.context.getString(
                    R.string.media_release_data,
                    "~",
                    (entry.chapters ?: "~").toString(),
                    ""
                )

                else -> "??"
            }
    }
}