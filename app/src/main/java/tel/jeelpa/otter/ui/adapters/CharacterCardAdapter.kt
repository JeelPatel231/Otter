package tel.jeelpa.otter.ui.adapters

import coil.load
import coil.size.Scale
import tel.jeelpa.otter.databinding.CharacterSmallLayoutBinding
import tel.jeelpa.otter.ui.generic.GenericListAdapter
import tel.jeelpa.otterlib.models.CharacterCardData

class CharacterCardAdapter(
    private val onItemClick : (CharacterCardData) -> Unit
): GenericListAdapter<Int, CharacterCardData, CharacterSmallLayoutBinding>(
    CharacterSmallLayoutBinding::inflate,
    { id }
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
}