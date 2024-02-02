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
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import tel.jeelpa.otter.R
import tel.jeelpa.otter.databinding.MediaHomePageLayoutBinding
import tel.jeelpa.otter.ui.adapters.MediaCardPagingAdapter
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
class AnimeFragment : Fragment() {
    private var binding: MediaHomePageLayoutBinding by autoCleared()
    @Inject lateinit var editMediaStatusUseCase: EditMediaStatusUseCase
    @Inject lateinit var navigate: GetNavigationUseCase
    private val animeHomeViewModel: AnimeFragmentViewModel by viewModels()

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
        animeHomeViewModel.searchResults(query).observeFlow(viewLifecycleOwner){
            submitData(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val editMediaItem = editMediaStatusUseCase.forFragment(requireActivity())

        val searchResultsAdapter = MediaCardPagingAdapter(navigate::invoke, editMediaItem::invoke)

        binding = MediaHomePageLayoutBinding.inflate(inflater, container, false)

        binding.firstRowText.text = getString(R.string.trending_anime)
        binding.secondRowText.text = getString(R.string.recently_updated)
        binding.thirdRowText.text = getString(R.string.popular_anime)

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
            MediaCardPagingAdapter(navigate::invoke, editMediaItem::invoke),
            binding.firstRowRecycler,
            animeHomeViewModel.trendingAnime
        )

        initPagedRecycler(
            MediaCardPagingAdapter(navigate::invoke,  editMediaItem::invoke),
            binding.secondRowRecycler,
            animeHomeViewModel.recentlyUpdated
        )

        initPagedRecycler(
            MediaCardPagingAdapter(navigate::invoke,  editMediaItem::invoke),
            binding.thirdRowRecycler,
            animeHomeViewModel.popularAnime
        )

        return binding.root
    }
}
