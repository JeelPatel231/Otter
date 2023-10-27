package tel.jeelpa.otter.ui.fragments.mangadetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.ui.setupWithNavController
import coil.load
import coil.size.Scale
import dagger.hilt.android.AndroidEntryPoint
import tel.jeelpa.otter.databinding.FragmentMangaDetailsBinding
import tel.jeelpa.otter.ui.generic.autoCleared
import tel.jeelpa.otter.ui.generic.getNavControllerFromHost
import tel.jeelpa.otter.ui.generic.observeFlow

@AndroidEntryPoint
class MangaDetailsFragment : Fragment() {
    private val mangaDetailsViewModel: MangaDetailsViewModel by activityViewModels()
    private var binding: FragmentMangaDetailsBinding by autoCleared()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentMangaDetailsBinding.inflate(inflater, container, false)

        val navController = getNavControllerFromHost(binding.mediaDetailsFragmentContainer.id)
        binding.bottomNavigationBar.setupWithNavController(navController)
        binding.mediaTitle.text = mangaDetailsViewModel.navArgs.title

        mangaDetailsViewModel.mangaDetails.observeFlow(viewLifecycleOwner){
            it?.let {
                binding.coverImage.load(it.coverImage){
                    scale(Scale.FILL)
                }
            }
        }

        return binding.root
    }
}
