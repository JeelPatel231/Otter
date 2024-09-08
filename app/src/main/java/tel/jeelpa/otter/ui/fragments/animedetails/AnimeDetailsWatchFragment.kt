package tel.jeelpa.otter.ui.fragments.animedetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import tel.jeelpa.aniyomi_runtime.AniyomiPluginMeta
import tel.jeelpa.otter.R
import tel.jeelpa.otter.databinding.FragmentAnimeDetailsWatchBinding
import tel.jeelpa.otter.ui.adapters.EpisodeAdapter
import tel.jeelpa.otter.ui.fragments.mediaCommon.WrongMediaSelectionBottomSheetDialog
import tel.jeelpa.otter.ui.generic.GridAutoFitLayoutManager
import tel.jeelpa.otter.ui.generic.MaterialSpinnerAdapter
import tel.jeelpa.otter.ui.generic.autoCleared
import tel.jeelpa.otter.ui.generic.getNavControllerFromHost
import tel.jeelpa.otter.ui.generic.observeFlow


class AnimeDetailsWatchFragment : Fragment() {
    private val animeDetailsViewModel: AnimeDetailsViewModel by activityViewModels()
    private var binding: FragmentAnimeDetailsWatchBinding by autoCleared()

    override fun onResume() {
        super.onResume()
        // Update Height after ViewPager makes fragment transaction
        binding.root.requestLayout()
    }

    private fun showDialog() {
        val dialog = WrongMediaSelectionBottomSheetDialog(animeDetailsViewModel.searchedAnimes,
            onItemClick = { animeDetailsViewModel.onSelectAnime(it) },
            onSearchClick = { animeDetailsViewModel.searchAnime(it) })
        dialog.show(childFragmentManager, "WrongTitle")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnimeDetailsWatchBinding.inflate(inflater, container, false)

        binding.wrongTitle.setOnClickListener {
            showDialog()
        }

        val episodesAdapter = EpisodeAdapter(lifecycleScope) {
            val selectedParser = animeDetailsViewModel.selectedParserId.first()!!
            requireActivity().getNavControllerFromHost(R.id.anime_activity_container_view).navigate(
                AnimeDetailsFragmentDirections.toExoplayerFragment(
                    episodeLink = it.link, selectedParser = selectedParser
                )
            )
        }

        binding.parserServiceSelector.setOnItemClickListener { adapterView, _, idx, _ ->
            binding.selectedAnimeTitle.text = "Searching..."
            // clear the episodes array
            episodesAdapter.setData(emptyList())
            // start scraping with the new parser
            lifecycleScope.launch {
                try {
                    animeDetailsViewModel.onParserChange(
                        (adapterView.getItemAtPosition(idx) as AniyomiPluginMeta).uniqueId
                    )
                } catch (e: Throwable) {
                    binding.selectedAnimeTitle.text = e.message
                }
            }
        }

        animeDetailsViewModel.parsers.observeFlow(this) {
            val adapter = MaterialSpinnerAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                it.map { plugin -> plugin.first }
            )
            binding.parserServiceSelector.setAdapter(adapter)
        }

        animeDetailsViewModel.selectedParserId.observeFlow(viewLifecycleOwner) {
            binding.wrongTitle.visibility = when (it) {
                null -> View.GONE
                else -> View.VISIBLE
            }
        }

        animeDetailsViewModel.selectedAnime.observeFlow(viewLifecycleOwner) {
            binding.selectedAnimeTitle.text = it?.name ?: "Nothing Selected"
        }

        binding.tabbedRecyclerView.apply {
            setAdapter(episodesAdapter)
            setLayoutManager(
                GridAutoFitLayoutManager(requireContext(), 150)
            )
        }

        animeDetailsViewModel.episodesScraped.observeFlow(viewLifecycleOwner) {
            binding.tabbedRecyclerView.setData(it)
        }

        return binding.root
    }

}
