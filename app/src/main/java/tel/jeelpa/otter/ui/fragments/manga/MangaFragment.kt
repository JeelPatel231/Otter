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
import tel.jeelpa.otter.trackerinterface.models.MediaCardData
import tel.jeelpa.otter.ui.adapters.MediaCardAdapter
import tel.jeelpa.otter.ui.generic.GridAutoFitLayoutManager
import tel.jeelpa.otter.ui.generic.autoCleared
import tel.jeelpa.otter.ui.generic.initRecycler
import tel.jeelpa.otter.ui.generic.navigateToMediaDetails
import tel.jeelpa.otter.ui.generic.nullOnBlank
import tel.jeelpa.otter.ui.generic.observeFlow

@AndroidEntryPoint
class MangaFragment : Fragment() {
    private var binding: MediaHomePageLayoutBinding by autoCleared()
    private val mangaFragmentViewModel: MangaFragmentViewModel by viewModels()


    private fun navigateToDetails(mediaCardData: MediaCardData) =
        requireContext().navigateToMediaDetails(mediaCardData)

    private val searchResultsAdapter = MediaCardAdapter(::navigateToDetails)

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MediaHomePageLayoutBinding.inflate(inflater, container, false)
        binding.firstRowText.text = getString(R.string.trending_manga)
        binding.secondRowText.text = getString(R.string.popular_manga)
        binding.thirdRowText.text = getString(R.string.trending_novel)

        mangaFragmentViewModel.searchResults.observeFlow(viewLifecycleOwner) {
            it?.let {
                searchResultsAdapter.submitList(it)
            }
        }

        binding.searchRecycler.apply {
            adapter = searchResultsAdapter
            layoutManager = GridAutoFitLayoutManager(requireContext(),110)
        }

        binding.searchView.editText.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                textView.text.toString().nullOnBlank()?.let {
                    mangaFragmentViewModel.search(it)
                    return@setOnEditorActionListener true
                }
            }
            false
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