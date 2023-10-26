package tel.jeelpa.otter.ui.fragments.animedetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tel.jeelpa.otter.databinding.FragmentAnimeDetailsWatchBinding
import tel.jeelpa.otter.reference.Parser
import tel.jeelpa.otter.ui.adapters.SimpleTextRecyclerAdapter
import tel.jeelpa.otter.ui.generic.autoCleared
import tel.jeelpa.otter.ui.generic.getNavParentFragment
import tel.jeelpa.otter.ui.generic.getOuterNavController
import tel.jeelpa.otter.ui.generic.observeFlow

class AnimeDetailsWatchFragment : Fragment() {
    private val animeDetailsViewModel: AnimeDetailsViewModel by viewModels({ getNavParentFragment() })
    private var binding: FragmentAnimeDetailsWatchBinding by autoCleared()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnimeDetailsWatchBinding.inflate(inflater, container, false)
        binding.parserServiceSelector.apply {
            setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    animeDetailsViewModel.parsers
                )
            )
            setOnItemClickListener { adapterView, _, idx, _ ->
                lifecycleScope.launch(Dispatchers.IO) {
                    animeDetailsViewModel.startSearch(adapterView.getItemAtPosition(idx) as Parser)
                }
            }
        }

        animeDetailsViewModel.selectedAnime.observeFlow(viewLifecycleOwner){
            binding.selectedAnimeTitle.text = it?.name ?: "Nothing Selected"
        }

        val episodesAdapter = SimpleTextRecyclerAdapter(
            onItemClick = {
                val videoLinks = animeDetailsViewModel.getVideoLinks(it)
                getOuterNavController().navigate(
                    AnimeDetailsFragmentDirections.toExoplayerFragment(videoLinks.toTypedArray(), fresh = true)
                )
            }
        )

        binding.episodesRecycler.apply {
            adapter = episodesAdapter
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
        }
        animeDetailsViewModel.selectedAnime.observeFlow(viewLifecycleOwner){
            it?.let {
                episodesAdapter.setData(animeDetailsViewModel.episodesScraped.value.map { ep -> ep.link })
            }
        }

        return binding.root
    }

}
