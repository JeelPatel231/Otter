package tel.jeelpa.otter.ui.fragments.manga

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
class MangaFragment : Fragment() {
    private var binding: MediaHomePageLayoutBinding by autoCleared()
    private val mangaFragmentViewModel: MangaFragmentViewModel by viewModels()


    private fun navigateToDetails(mediaCardData: MediaCardData) =
        requireContext().navigateToMediaDetails(mediaCardData)

    private val searchResultsAdapter = MediaCardPagingAdapter(::navigateToDetails)

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

    private fun observeSearchFlow(query: String){
//        searchJob?.cancel()?.let { showToast("WAS SET, CANCELLED") }
//        searchJob =
        mangaFragmentViewModel.searchResults(query).observeFlow(viewLifecycleOwner){
            searchResultsAdapter.submitData(it)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MediaHomePageLayoutBinding.inflate(inflater, container, false)
        binding.firstRowText.text = getString(R.string.trending_manga)
        binding.secondRowText.text = getString(R.string.popular_manga)
        binding.thirdRowText.text = getString(R.string.trending_novel)

        binding.searchRecycler.apply {
            adapter = searchResultsAdapter
            layoutManager = GridAutoFitLayoutManager(requireContext(),110)
        }

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
            binding.firstRowRecycler,
            mangaFragmentViewModel.trendingManga
        )

        initPagedRecycler(
            MediaCardPagingAdapter(::navigateToDetails),
            binding.secondRowRecycler,
            mangaFragmentViewModel.popularManga
        )

        initPagedRecycler(
            MediaCardPagingAdapter(::navigateToDetails),
            binding.thirdRowRecycler,
            mangaFragmentViewModel.trendingNovel
        )

        return binding.root
    }
}