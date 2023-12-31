package tel.jeelpa.otter.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import coil.load
import coil.size.Scale
import com.google.android.material.color.MaterialColors
import tel.jeelpa.otter.R
import tel.jeelpa.otter.databinding.MediaRelationSmallLayoutBinding
import tel.jeelpa.otter.ui.generic.GenericListAdapter
import tel.jeelpa.plugininterface.tracker.models.AppMediaType
import tel.jeelpa.plugininterface.tracker.models.MediaCardData
import tel.jeelpa.plugininterface.tracker.models.MediaRelationCardData
import tel.jeelpa.plugininterface.tracker.models.MediaRelationType


class RelationsAdapter(
    private val onItemClick : (MediaCardData) -> Unit
): GenericListAdapter<Int, MediaRelationCardData, MediaRelationSmallLayoutBinding>(
    primaryKey = { id }
) {

    private fun getRelationDrawable(relation: MediaRelationType, tintColor: Int): Int {
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

        val context = binding.root.context
        val color = MaterialColors.getColor(binding.root, com.google.android.material.R.attr.colorOnSurface, Color.GRAY)
        val drawableId = getRelationDrawable(entry.relation, color)
        val tintedDrawable = ResourcesCompat.getDrawable(context.resources, drawableId, context.theme)?.apply {
            setTint(color)
        }
        binding.relationIcon.load(tintedDrawable)
        binding.relationHolder.text = entry.relation.value.replace('_', ' ')

        binding.score.text = entry.meanScore.toString()
        binding.title.text = entry.title
        binding.totalCount.text =
            when(entry.type){

                AppMediaType.ANIME -> binding.root.context.getString(
                    R.string.media_release_data,
                    "~",
                    (entry.episodesAired ?: entry.episodes ?: "~").toString() ,
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

    override fun inflateCallback(
        layoutInflator: LayoutInflater,
        viewGroup: ViewGroup?,
        attachToParent: Boolean
    ): MediaRelationSmallLayoutBinding {
        return MediaRelationSmallLayoutBinding.inflate(layoutInflator, viewGroup, attachToParent)
    }
}