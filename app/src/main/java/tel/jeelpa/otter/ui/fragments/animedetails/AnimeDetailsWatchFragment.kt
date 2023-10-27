package tel.jeelpa.otter.ui.fragments.animedetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tel.jeelpa.otter.R
import tel.jeelpa.otter.databinding.FragmentAnimeDetailsWatchBinding
import tel.jeelpa.otter.reference.Parser
import tel.jeelpa.otter.ui.fragments.exoplayer.ExoPlayerViewModel
import tel.jeelpa.otter.ui.generic.MaterialSpinnerAdapter
import tel.jeelpa.otter.ui.generic.autoCleared
import tel.jeelpa.otter.ui.generic.getNavControllerFromHost
import tel.jeelpa.otter.ui.generic.observeFlow

class AnimeDetailsWatchFragment : Fragment() {
    private val animeDetailsViewModel: AnimeDetailsViewModel by activityViewModels()
    private val exoPlayerViewModel: ExoPlayerViewModel by activityViewModels()
    private var binding: FragmentAnimeDetailsWatchBinding by autoCleared()
    private var episodeScrapeJob: Job? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnimeDetailsWatchBinding.inflate(inflater, container, false)

        val episodesAdapter = EpisodeAdapter(lifecycleScope) {
            exoPlayerViewModel.videoServers = animeDetailsViewModel.getVideoServers(it.link)
            requireActivity().getNavControllerFromHost(R.id.anime_activity_container_view).navigate(
                AnimeDetailsFragmentDirections.toExoplayerFragment()
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
                episodeScrapeJob?.cancel()
                // clear the episodes array
                episodesAdapter.setData(emptyList())
                // start scaping with the new parser
                episodeScrapeJob = lifecycleScope.launch(Dispatchers.IO) {
                    animeDetailsViewModel.startSearch(adapterView.getItemAtPosition(idx) as Parser)
                }
            }
        }

        animeDetailsViewModel.selectedAnime.observeFlow(viewLifecycleOwner) {
            binding.selectedAnimeTitle.text = it?.name ?: "Nothing Selected"
        }

        binding.episodesRecycler.apply {
            adapter = episodesAdapter
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
        }
        animeDetailsViewModel.episodesScraped.observeFlow(viewLifecycleOwner) {
            episodesAdapter.setData(it)
        }

        return binding.root
    }

}
