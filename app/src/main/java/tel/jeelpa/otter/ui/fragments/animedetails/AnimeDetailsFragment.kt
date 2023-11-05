package tel.jeelpa.otter.ui.fragments.animedetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import coil.load
import coil.size.Scale
import dagger.hilt.android.AndroidEntryPoint
import tel.jeelpa.otter.databinding.FragmentAnimeDetailsBinding
import tel.jeelpa.otter.ui.generic.ViewPageNavigatorAdapter
import tel.jeelpa.otter.ui.generic.autoCleared
import tel.jeelpa.otter.ui.generic.observeFlow
import tel.jeelpa.otter.ui.generic.setupWithBottomNav

@AndroidEntryPoint
class AnimeDetailsFragment : Fragment() {
    private val animeDetailsViewModel: AnimeDetailsViewModel by activityViewModels()
    private var binding: FragmentAnimeDetailsBinding by autoCleared()

    private val animeInfoFragment  = AnimeDetailsInfoFragment()
    private val animeWatchFragment  = AnimeDetailsWatchFragment()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentAnimeDetailsBinding.inflate(inflater, container, false)

        val viewPageNavigatorAdapter = ViewPageNavigatorAdapter(
            childFragmentManager,
            lifecycle,
            arrayOf(animeInfoFragment, animeWatchFragment)
        )

        binding.animePagerContainer.apply {
            adapter = viewPageNavigatorAdapter
            isUserInputEnabled = false
            setupWithBottomNav(binding.bottomNavigationBar)
        }

        binding.mediaTitle.text = animeDetailsViewModel.navArgs.title

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
