package tel.jeelpa.otter.ui.fragments.animedetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.serialization.Serializable
import tel.jeelpa.otter.R
import tel.jeelpa.otter.databinding.FragmentAnimeDetailsWatchBinding
import tel.jeelpa.otter.ui.fragments.mediaCommon.WrongMediaSelectionBottomSheetDialog
import tel.jeelpa.otter.ui.generic.GridAutoFitLayoutManager
import tel.jeelpa.otter.ui.generic.MaterialSpinnerAdapter
import tel.jeelpa.otter.ui.generic.autoCleared
import tel.jeelpa.otter.ui.generic.getNavControllerFromHost
import tel.jeelpa.otter.ui.generic.observeFlow
import tel.jeelpa.plugininterface.anime.parser.Parser
import tel.jeelpa.plugininterface.models.VideoServer


// Try to NOT use this wrapper, passing complex data structures is an anti pattern
@Serializable
class VideoServerWrapper(val list: List<VideoServer>): java.io.Serializable

class AnimeDetailsWatchFragment : Fragment() {
    private val animeDetailsViewModel: AnimeDetailsViewModel by activityViewModels()
    private var binding: FragmentAnimeDetailsWatchBinding by autoCleared()
    private var episodeScrapeJob: Job? = null

    override fun onResume() {
        super.onResume()
        // Update Height after ViewPager makes fragment transaction
        binding.root.requestLayout()
    }

    private fun showDialog() {
        val dialog = WrongMediaSelectionBottomSheetDialog(
            animeDetailsViewModel.searchedAnimes,
            onItemClick = { animeDetailsViewModel.onSelectAnime(it) },
            onSearchClick = { animeDetailsViewModel.searchAnime(it) }
        )
        dialog.show(childFragmentManager, "WrongTitle")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnimeDetailsWatchBinding.inflate(inflater, container, false)

        binding.wrongTitle.setOnClickListener {
            showDialog()
        }

        val episodesAdapter = EpisodeAdapter(lifecycleScope) {
            val videoServers = animeDetailsViewModel.getVideoServers(it.link)
            requireActivity().getNavControllerFromHost(R.id.anime_activity_container_view).navigate(
                AnimeDetailsFragmentDirections.toExoplayerFragment(VideoServerWrapper(videoServers))
            )
        }

        binding.parserServiceSelector.apply {
            setAdapter(
                MaterialSpinnerAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    animeDetailsViewModel.parsers
                )
            )
            setOnItemClickListener { adapterView, _, idx, _ ->
                // cancel the old job
                binding.selectedAnimeTitle.text = "Searching..."
                // clear the episodes array
                episodesAdapter.setData(emptyList())
                // start scaping with the new parser
                try {
                    animeDetailsViewModel.onParserChange(adapterView.getItemAtPosition(idx) as Parser)
                } catch (e: Throwable) {
                    binding.selectedAnimeTitle.text = e.message
                }
            }
        }

        animeDetailsViewModel.selectedParser.observeFlow(viewLifecycleOwner) {
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
