package tel.jeelpa.otter.ui.fragments.animedetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import coil.load
import coil.size.Scale
import dagger.hilt.android.AndroidEntryPoint
import tel.jeelpa.otter.databinding.FragmentAnimeDetailsBinding
import tel.jeelpa.otter.ui.generic.autoCleared
import tel.jeelpa.otter.ui.generic.observeFlow

@AndroidEntryPoint
class AnimeDetailsFragment : Fragment(){

    private var binding: FragmentAnimeDetailsBinding by autoCleared()
    private val animeDetailsViewModel: AnimeDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnimeDetailsBinding.inflate(inflater, container, false)

        val navHostFragment = childFragmentManager.findFragmentById(binding.mediaDetailsFragmentContainer.id) as NavHostFragment
        binding.bottomNavigationBar.setupWithNavController(navHostFragment.navController)
        binding.mediaTitle.text = animeDetailsViewModel.navArgs.media.title

        animeDetailsViewModel.animeDetails.observeFlow(viewLifecycleOwner){
            it?.let {
                binding.coverImage.load(it.coverImage){
                    scale(Scale.FILL)
                }
            }
        }

        return binding.root
    }
}