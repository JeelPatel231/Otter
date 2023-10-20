package tel.jeelpa.otter.ui.adapters

import coil.load
import coil.size.Scale
import tel.jeelpa.otter.databinding.CharacterSmallLayoutBinding
import tel.jeelpa.otter.ui.generic.GenericRecyclerAdapter
import tel.jeelpa.otterlib.models.CharacterCardData

class CharacterCardAdapter(
    private val onItemClick : (Int) -> Unit
): GenericRecyclerAdapter<CharacterCardData, CharacterSmallLayoutBinding>(CharacterSmallLayoutBinding::inflate) {
    override fun onBind(binding: CharacterSmallLayoutBinding, entry: CharacterCardData, position: Int) {
        binding.root.setOnClickListener {
            onItemClick(entry.id)
        }
        binding.avatar.load(entry.avatar){
            scale(Scale.FILL)
        }

        binding.name.text = entry.name
        binding.role.text = entry.role
    }
}