package tel.jeelpa.otter.ui.fragments.mangadetails

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
import tel.jeelpa.otter.databinding.FragmentMangaDetailsBinding
import tel.jeelpa.otter.ui.generic.autoCleared
import tel.jeelpa.otter.ui.generic.observeFlow

@AndroidEntryPoint
class MangaDetailsFragment : Fragment() {
    private var binding: FragmentMangaDetailsBinding by autoCleared()
    private val mangaDetailsViewModel: MangaDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMangaDetailsBinding.inflate(inflater, container, false)
        val navHostFragment = childFragmentManager.findFragmentById(binding.mediaDetailsFragmentContainer.id) as NavHostFragment
        binding.bottomNavigationBar.setupWithNavController(navHostFragment.navController)
        binding.mediaTitle.text = mangaDetailsViewModel.navArgs.media.title

        mangaDetailsViewModel.mangaDetails.observeFlow(viewLifecycleOwner) {
            it?.let {
                binding.coverImage.load(it.coverImage) {
                    scale(Scale.FILL)
                }
            }
        }

        return binding.root
    }
}