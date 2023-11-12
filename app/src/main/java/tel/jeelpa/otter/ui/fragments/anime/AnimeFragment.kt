package tel.jeelpa.otter.ui.fragments.anime

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.addCallback
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


    private val searchResultsAdapter = MediaCardAdapter(::navigateToDetails)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MediaHomePageLayoutBinding.inflate(inflater, container, false)

        binding.firstRowText.text = getString(R.string.trending_anime)
        binding.secondRowText.text = getString(R.string.recently_updated)
        binding.thirdRowText.text = getString(R.string.popular_anime)

        animeHomeViewModel.searchResults.observeFlow(viewLifecycleOwner) {
            it?.let {
                searchResultsAdapter.submitList(it)
            }
        }

        binding.searchRecycler.apply {
            adapter = searchResultsAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
        }

        binding.searchView.setupWithSearchBar(binding.searchBar)

        binding.searchView.editText.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                textView.text.toString().nullOnBlank()?.let {
                    animeHomeViewModel.search(it)
                    return@setOnEditorActionListener false
                }
            }
            true
        }


        initRecycler(
            MediaCardAdapter(::navigateToDetails),
            binding.firstRowRecyclerView,
            binding.firstRowShimmerView.root,
            animeHomeViewModel.trendingAnime
        )

        initRecycler(
            MediaCardAdapter(::navigateToDetails),
            binding.secondRowRecyclerView,
            binding.secondRowShimmerView.root,
            animeHomeViewModel.recentlyUpdated
        )

        initRecycler(
            MediaCardAdapter(::navigateToDetails),
            binding.thirdRowRecyclerView,
            binding.thirdRowShimmerView.root,
            animeHomeViewModel.popularAnime
        )

        return binding.root
    }
}