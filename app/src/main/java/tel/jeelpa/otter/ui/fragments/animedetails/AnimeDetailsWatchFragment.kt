package tel.jeelpa.otter.ui.fragments.animedetails

import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tel.jeelpa.otter.databinding.FragmentAnimeDetailsWatchBinding
import tel.jeelpa.otter.reference.Parser
import tel.jeelpa.otter.ui.adapters.SimpleTextRecyclerAdapter
import tel.jeelpa.otter.ui.generic.ViewBindingFragment
import tel.jeelpa.otter.ui.generic.getNavParentFragment
import tel.jeelpa.otter.ui.generic.getOuterNavController
import tel.jeelpa.otter.ui.generic.observeFlow

class AnimeDetailsWatchFragment :
    ViewBindingFragment<FragmentAnimeDetailsWatchBinding>(FragmentAnimeDetailsWatchBinding::inflate) {
    private val animeDetailsViewModel: AnimeDetailsViewModel by viewModels({ getNavParentFragment() })

    override fun onCreateBindingView() {
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
    }

}
