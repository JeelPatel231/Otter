package tel.jeelpa.otter.ui.fragments.animedetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tel.jeelpa.otter.databinding.FragmentAnimeDetailsWatchBinding
import tel.jeelpa.otter.reference.Parser
import tel.jeelpa.otter.ui.generic.MaterialSpinnerAdapter
import tel.jeelpa.otter.ui.generic.autoCleared
import tel.jeelpa.otter.ui.generic.getNavParentFragment
import tel.jeelpa.otter.ui.generic.getOuterNavController
import tel.jeelpa.otter.ui.generic.observeFlow

class AnimeDetailsWatchFragment : Fragment() {
    private val animeDetailsViewModel: AnimeDetailsViewModel by viewModels({ getNavParentFragment() })
    private var binding: FragmentAnimeDetailsWatchBinding by autoCleared()
    private var episodeScrapeJob: Job? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnimeDetailsWatchBinding.inflate(inflater, container, false)


        val episodesAdapter = EpisodeAdapter(lifecycleScope) {
            val videoLinks = withContext(Dispatchers.IO) {
                animeDetailsViewModel.getVideoLinks(it.link).toList().toTypedArray()
             }
            getOuterNavController().navigate(
                AnimeDetailsFragmentDirections.toExoplayerFragment(videoLinks, fresh = true)
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
