package tel.jeelpa.otter.ui.fragments.anime

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tel.jeelpa.otter.R
import tel.jeelpa.otter.databinding.MediaHomePageLayoutBinding
import tel.jeelpa.otter.ui.adapters.MediaCardAdapter
import tel.jeelpa.otter.ui.fragments.MainFragmentDirections
import tel.jeelpa.otter.ui.generic.ViewBindingFragment
import tel.jeelpa.otter.ui.generic.getOuterNavController
import tel.jeelpa.otter.ui.generic.initRecycler
import tel.jeelpa.otterlib.models.AppMediaType


@AndroidEntryPoint
class AnimeFragment: ViewBindingFragment<MediaHomePageLayoutBinding>(MediaHomePageLayoutBinding::inflate){
    private val animeHomeViewModel : AnimeFragmentViewModel by viewModels()

    private fun navigateToAnimeDetails(id: Int, title: String, type: AppMediaType){
        val destination = when(type) {
            AppMediaType.ANIME -> MainFragmentDirections.toAnimeDetailsFragment(id, title)
            AppMediaType.MANGA -> MainFragmentDirections.toMangaDetailsFragment(id, title)
            else -> throw IllegalStateException("Unknown Media Type")
        }
        getOuterNavController().navigate(destination)
    }


    override fun onCreateBindingView() {
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

    }
}