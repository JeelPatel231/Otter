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
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import tel.jeelpa.otter.R
import tel.jeelpa.otter.databinding.MediaHomePageLayoutBinding
import tel.jeelpa.otter.ui.adapters.MediaCardPagingAdapter
import tel.jeelpa.otter.ui.fragments.mediaCommon.MediaEditorBottomSheetFactory
import tel.jeelpa.otter.ui.generic.GridAutoFitLayoutManager
import tel.jeelpa.otter.ui.generic.autoCleared
import tel.jeelpa.otter.ui.generic.initPagedRecycler
import tel.jeelpa.otter.ui.generic.nullOnBlank
import tel.jeelpa.otter.ui.generic.observeFlow
import tel.jeelpa.otter.usecases.EditMediaStatusUseCase
import tel.jeelpa.otter.usecases.GetNavigationUseCase
import tel.jeelpa.plugininterface.tracker.models.MediaCardData
import javax.inject.Inject

@AndroidEntryPoint
class MangaFragment : Fragment() {
    private var binding: MediaHomePageLayoutBinding by autoCleared()
    private val mangaFragmentViewModel: MangaFragmentViewModel by viewModels()
    @Inject lateinit var getMediaEditorBottomSheet: MediaEditorBottomSheetFactory
    @Inject lateinit var navigate: GetNavigationUseCase
    @Inject lateinit var editMediaItemUseCase: EditMediaStatusUseCase


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

    private fun <VH : RecyclerView.ViewHolder> PagingDataAdapter<MediaCardData, VH>.observeSearchFlow(query: String){
        mangaFragmentViewModel.searchResults(query).observeFlow(viewLifecycleOwner){
            submitData(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val editMedia = editMediaItemUseCase.forFragment(requireActivity())
        val searchResultsAdapter = MediaCardPagingAdapter(navigate::invoke, editMedia::invoke)

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
                    searchResultsAdapter.observeSearchFlow(it)
                    return@setOnEditorActionListener true
                }
            }
            false
        }

        initPagedRecycler(
            MediaCardPagingAdapter(navigate::invoke, editMedia::invoke),
            binding.firstRowRecycler,
            mangaFragmentViewModel.trendingManga
        )

        initPagedRecycler(
            MediaCardPagingAdapter(navigate::invoke, editMedia::invoke),
            binding.secondRowRecycler,
            mangaFragmentViewModel.popularManga
        )

        initPagedRecycler(
            MediaCardPagingAdapter(navigate::invoke, editMedia::invoke),
            binding.thirdRowRecycler,
            mangaFragmentViewModel.trendingNovel
        )

        return binding.root
    }
}