package tel.jeelpa.otter.ui.fragments.anime

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tel.jeelpa.otter.R
import tel.jeelpa.otter.databinding.MediaHomePageLayoutBinding
import tel.jeelpa.otter.ui.adapters.MediaCardAdapter
import tel.jeelpa.otter.ui.fragments.MainFragmentDirections
import tel.jeelpa.otter.ui.generic.autoCleared
import tel.jeelpa.otter.ui.generic.getOuterNavController
import tel.jeelpa.otter.ui.generic.initRecycler
import tel.jeelpa.otterlib.models.AppMediaType
import tel.jeelpa.otterlib.models.MediaCardData


@AndroidEntryPoint
class AnimeFragment: Fragment(){
    private var binding: MediaHomePageLayoutBinding by autoCleared()

    private val animeHomeViewModel : AnimeFragmentViewModel by viewModels()

    private fun navigateToAnimeDetails(mediaCardData: MediaCardData){
        val destination = when(mediaCardData.type) {
            AppMediaType.ANIME -> MainFragmentDirections.toAnimeDetailsFragment(mediaCardData)
            AppMediaType.MANGA -> MainFragmentDirections.toMangaDetailsFragment(mediaCardData)
            else -> throw IllegalStateException("Unknown Media Type")
        }
        getOuterNavController().navigate(destination)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MediaHomePageLayoutBinding.inflate(inflater, container, false)

        binding.firstRowText.text = getString(R.string.trending_anime)
        binding.secondRowText.text = getString(R.string.recently_updated)
        binding.thirdRowText.text = getString(R.string.popular_anime)

        initRecycler(
            MediaCardAdapter(::navigateToAnimeDetails),
            binding.firstRowRecyclerView,
            binding.firstRowShimmerView.root,
            animeHomeViewModel.trendingAnime
        ){ it }

        initRecycler(
            MediaCardAdapter(::navigateToAnimeDetails),
            binding.secondRowRecyclerView,
            binding.secondRowShimmerView.root,
            animeHomeViewModel.recentlyUpdated
        ){ it }

        initRecycler(
            MediaCardAdapter(::navigateToAnimeDetails),
            binding.thirdRowRecyclerView,
            binding.thirdRowShimmerView.root,
            animeHomeViewModel.popularAnime
        ){ it }

        return binding.root
    }
}