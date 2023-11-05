package tel.jeelpa.otter.ui.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import coil.load
import coil.size.Scale
import com.google.android.material.color.DynamicColors
import tel.jeelpa.otter.R
import tel.jeelpa.otter.databinding.MediaRelationSmallLayoutBinding
import tel.jeelpa.otter.ui.generic.GenericRecyclerAdapter
import tel.jeelpa.otterlib.models.AppMediaType
import tel.jeelpa.otterlib.models.MediaCardData
import tel.jeelpa.otterlib.models.MediaRelationCardData
import tel.jeelpa.otterlib.models.MediaRelationType

class RelationsAdapter(
    private val onItemClick : (MediaCardData) -> Unit
): GenericRecyclerAdapter<MediaRelationCardData, MediaRelationSmallLayoutBinding>(MediaRelationSmallLayoutBinding::inflate) {

    private fun getRelationDrawable(context: Context, relation: MediaRelationType): Drawable {
        // get the material dynamic color in kotlin
        val ta = DynamicColors.wrapContextIfAvailable(context)
                .obtainStyledAttributes(intArrayOf(com.google.android.material.R.attr.colorOnBackground))
        val tintColor = ta.getColor(0, 0)
        ta.recycle()

        val idRes = when(relation){
            // TODO: switch to enum and add more cases
            MediaRelationType.SOURCE -> R.drawable.round_menu_book_24
            else -> R.drawable.round_star_24
        }

        // tint it with the color and return
        return AppCompatResources.getDrawable(context, idRes)!!.apply {
            setTint(tintColor)
            DrawableCompat.setTint(this, tintColor)
        }

    }

    override fun onBind(binding: MediaRelationSmallLayoutBinding, entry: MediaRelationCardData, position: Int) {
        binding.root.setOnClickListener {
            onItemClick(entry.toMediaCardData())
        }
        binding.coverImage.load(entry.coverImage){
            scale(Scale.FILL)
        }

        binding.relationIcon.load(getRelationDrawable(binding.root.context, entry.relation))
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