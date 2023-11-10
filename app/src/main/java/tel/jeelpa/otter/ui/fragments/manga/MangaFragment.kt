package tel.jeelpa.otter.ui.fragments.manga

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import tel.jeelpa.otter.R
import tel.jeelpa.otter.databinding.MediaHomePageLayoutBinding
import tel.jeelpa.otter.ui.adapters.MediaCardAdapter
import tel.jeelpa.otter.ui.generic.autoCleared
import tel.jeelpa.otter.ui.generic.initRecycler
import tel.jeelpa.otter.ui.generic.navigateToMediaDetails
import tel.jeelpa.otter.ui.generic.nullOnBlank
import tel.jeelpa.otter.ui.generic.observeFlow
import tel.jeelpa.otterlib.models.MediaCardData

@AndroidEntryPoint
class MangaFragment : Fragment(){
    private var binding : MediaHomePageLayoutBinding by autoCleared()
    private val mangaFragmentViewModel : MangaFragmentViewModel by viewModels()


    private fun navigateToDetails(mediaCardData: MediaCardData) =
        requireContext().navigateToMediaDetails(mediaCardData)

    private val searchResultsAdapter = MediaCardAdapter(::navigateToDetails)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MediaHomePageLayoutBinding.inflate(inflater, container, false)
        binding.firstRowText.text = getString(R.string.trending_manga)
        binding.secondRowText.text = getString(R.string.popular_manga)
        binding.thirdRowText.text = getString(R.string.trending_novel)

        mangaFragmentViewModel.searchResults.observeFlow(viewLifecycleOwner){ it?.let {
            searchResultsAdapter.setData(it)
        } }

        binding.searchRecycler.apply {
            adapter = searchResultsAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
        }

        binding.searchView.editText.setOnEditorActionListener { textView, actionId, keyEvent ->
            when(actionId){
                EditorInfo.IME_ACTION_SEARCH -> {
                    val query = textView.text.toString().nullOnBlank()
                    if(query != null){
                        mangaFragmentViewModel.search(query)
                        false
                    } else {
                        true
                    }
                }
                else -> true
            }
        }

        initRecycler(
            MediaCardAdapter(::navigateToDetails),
            binding.firstRowRecyclerView,
            binding.firstRowShimmerView.root,
            mangaFragmentViewModel.trendingManga
        )

        initRecycler(
            MediaCardAdapter(::navigateToDetails),
            binding.secondRowRecyclerView,
            binding.secondRowShimmerView.root,
            mangaFragmentViewModel.popularManga
        )

        initRecycler(
            MediaCardAdapter(::navigateToDetails),
            binding.thirdRowRecyclerView,
            binding.thirdRowShimmerView.root,
            mangaFragmentViewModel.trendingNovel
        )

        return binding.root
    }
}