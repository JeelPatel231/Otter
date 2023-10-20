package tel.jeelpa.otter.ui.fragments.animedetails

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import coil.load
import coil.size.Scale
import dagger.hilt.android.AndroidEntryPoint
import tel.jeelpa.otter.databinding.FragmentAnimeDetailsBinding
import tel.jeelpa.otter.ui.generic.ViewBindingFragment
import tel.jeelpa.otter.ui.generic.observeFlow

@AndroidEntryPoint
class AnimeDetailsFragment : ViewBindingFragment<FragmentAnimeDetailsBinding>(FragmentAnimeDetailsBinding::inflate) {
    private val animeDetailsViewModel: AnimeDetailsViewModel by viewModels()

    override fun onCreateBindingView() {
        val navHostFragment = childFragmentManager.findFragmentById(binding.mediaDetailsFragmentContainer.id) as NavHostFragment
        binding.bottomNavigationBar.setupWithNavController(navHostFragment.navController)


        animeDetailsViewModel.animeDetails.observeFlow(viewLifecycleOwner){
            it?.let {
                binding.coverImage.load(it.coverImage){
                    scale(Scale.FILL)
                }
                binding.mediaTitle.text = it.title
            }
        }
    }
}