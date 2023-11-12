package tel.jeelpa.otter.ui.adapters

import coil.load
import coil.size.Scale
import tel.jeelpa.otter.R
import tel.jeelpa.otter.databinding.MediaRelationSmallLayoutBinding
import tel.jeelpa.otter.ui.generic.GenericListAdapter
import tel.jeelpa.otterlib.models.AppMediaType
import tel.jeelpa.otterlib.models.MediaCardData
import tel.jeelpa.otterlib.models.MediaRelationCardData
import tel.jeelpa.otterlib.models.MediaRelationType

class RelationsAdapter(
    private val onItemClick : (MediaCardData) -> Unit
): GenericListAdapter<Int, MediaRelationCardData, MediaRelationSmallLayoutBinding>(
    MediaRelationSmallLayoutBinding::inflate,
    { id }
) {

    private fun getRelationDrawable(relation: MediaRelationType): Int {
        return when(relation){
            // TODO: switch to enum and add more cases
            MediaRelationType.SOURCE -> R.drawable.round_menu_book_24
            else -> R.drawable.round_star_24
        }
    }

    override fun onBind(binding: MediaRelationSmallLayoutBinding, entry: MediaRelationCardData, position: Int) {
        binding.root.setOnClickListener {
            onItemClick(entry.toMediaCardData())
        }
        binding.coverImage.load(entry.coverImage){
            scale(Scale.FILL)
        }

        binding.relationIcon.load(getRelationDrawable(entry.relation))
        binding.relationHolder.text = entry.relation.value

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