package tel.jeelpa.otter.ui.fragments.manga

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
import tel.jeelpa.otter.ui.generic.autoCleared
import tel.jeelpa.otter.ui.generic.initRecycler
import tel.jeelpa.otter.ui.generic.navigateToMediaDetails
import tel.jeelpa.otterlib.models.MediaCardData

@AndroidEntryPoint
class MangaFragment : Fragment(){
    private var binding : MediaHomePageLayoutBinding by autoCleared()
    private val mangaFragmentViewModel : MangaFragmentViewModel by viewModels()


    private fun navigateToDetails(mediaCardData: MediaCardData) =
        requireContext().navigateToMediaDetails(mediaCardData)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MediaHomePageLayoutBinding.inflate(inflater, container, false)
        binding.firstRowText.text = getString(R.string.trending_manga)
        binding.secondRowText.text = getString(R.string.popular_manga)
        binding.thirdRowText.text = getString(R.string.trending_novel)

        initRecycler(
            MediaCardAdapter(::navigateToDetails),
            binding.firstRowRecyclerView,
            binding.firstRowShimmerView.root,
            mangaFragmentViewModel.trendingManga
        ) { it }

        initRecycler(
            MediaCardAdapter(::navigateToDetails),
            binding.secondRowRecyclerView,
            binding.secondRowShimmerView.root,
            mangaFragmentViewModel.popularManga
        ){ it }

        initRecycler(
            MediaCardAdapter(::navigateToDetails),
            binding.thirdRowRecyclerView,
            binding.thirdRowShimmerView.root,
            mangaFragmentViewModel.trendingNovel
        ){ it }

        return binding.root
    }
}