package tel.jeelpa.otter.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import coil.load
import coil.size.Scale
import tel.jeelpa.otter.databinding.CharacterSmallLayoutBinding
import tel.jeelpa.otter.ui.generic.GenericListAdapter
import tel.jeelpa.plugininterface.tracker.models.CharacterCardData

class CharacterCardAdapter(
    private val onItemClick : (CharacterCardData) -> Unit
): GenericListAdapter<Int, CharacterCardData, CharacterSmallLayoutBinding>(
    primaryKey = { id }
) {
    override fun onBind(binding: CharacterSmallLayoutBinding, entry: CharacterCardData, position: Int) {
        binding.root.setOnClickListener {
            onItemClick(entry)
        }
        binding.avatar.load(entry.avatar){
            scale(Scale.FILL)
        }

        binding.name.text = entry.name
        binding.role.text = entry.role
    }

    override fun inflateCallback(
        layoutInflator: LayoutInflater,
        viewGroup: ViewGroup?,
        attachToParent: Boolean
    ): CharacterSmallLayoutBinding {
        return CharacterSmallLayoutBinding.inflate(layoutInflator, viewGroup, attachToParent)
    }
}