package tel.jeelpa.otter.ui.fragments.anime

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tel.jeelpa.otter.R
import tel.jeelpa.otter.databinding.MediaHomePageLayoutBinding
import tel.jeelpa.otter.ui.adapters.MediaCardPagingAdapter
import tel.jeelpa.otter.ui.generic.GridAutoFitLayoutManager
import tel.jeelpa.otter.ui.generic.autoCleared
import tel.jeelpa.otter.ui.generic.initPagedRecycler
import tel.jeelpa.otter.ui.generic.navigateToMediaDetails
import tel.jeelpa.otter.ui.generic.nullOnBlank
import tel.jeelpa.otter.ui.generic.observeFlow
import tel.jeelpa.plugininterface.tracker.models.MediaCardData


@AndroidEntryPoint
class AnimeFragment : Fragment() {
    private var binding: MediaHomePageLayoutBinding by autoCleared()

    private val animeHomeViewModel: AnimeFragmentViewModel by viewModels()

    private fun navigateToDetails(mediaCardData: MediaCardData) =
        requireContext().navigateToMediaDetails(mediaCardData)

    private val backPressedCallback by lazy {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (binding.searchView.isShowing) {
                binding.searchView.handleBackInvoked()
            } else {
                requireActivity().finish()
            }
        }
    }

    override fun onPause() {
        backPressedCallback.isEnabled = false
        super.onPause()
    }

    override fun onResume() {
        backPressedCallback.isEnabled = true
        super.onResume()
    }

    // Do i even need to handle flow? its single producer single consumer
    // i think the memory should be released when the consumer is gone
//    private var searchJob: Job? = null
    private fun observeSearchFlow(query: String){
//        searchJob?.cancel()?.let { showToast("WAS SET, CANCELLED") }
//        searchJob =
            animeHomeViewModel.searchResults(query).observeFlow(viewLifecycleOwner){
            searchResultsAdapter.submitData(it)
        }
    }

    private val searchResultsAdapter = MediaCardPagingAdapter(::navigateToDetails)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MediaHomePageLayoutBinding.inflate(inflater, container, false)

        binding.firstRowText.text = getString(R.string.trending_anime)
        binding.secondRowText.text = getString(R.string.recently_updated)
        binding.thirdRowText.text = getString(R.string.popular_anime)

        binding.searchRecycler.apply {
            adapter = searchResultsAdapter
            layoutManager = GridAutoFitLayoutManager(requireContext(),110)
        }

        binding.searchView.setupWithSearchBar(binding.searchBar)
        binding.searchView.editText.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                textView.text.toString().nullOnBlank()?.let {
                    observeSearchFlow(it)
                    return@setOnEditorActionListener true
                }
            }
            false
        }


        initPagedRecycler(
            MediaCardPagingAdapter(::navigateToDetails),
            binding.firstRowRecyclerView,
            binding.firstRowShimmerView.root,
            animeHomeViewModel.trendingAnime
        )

        initPagedRecycler(
            MediaCardPagingAdapter(::navigateToDetails),
            binding.secondRowRecyclerView,
            binding.secondRowShimmerView.root,
            animeHomeViewModel.recentlyUpdated
        )

        initPagedRecycler(
            MediaCardPagingAdapter(::navigateToDetails),
            binding.thirdRowRecyclerView,
            binding.thirdRowShimmerView.root,
            animeHomeViewModel.popularAnime
        )

        return binding.root
    }
}