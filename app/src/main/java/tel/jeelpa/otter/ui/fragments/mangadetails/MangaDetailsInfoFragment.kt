package tel.jeelpa.otter.ui.fragments.mangadetails

import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import tel.jeelpa.otter.R
import tel.jeelpa.otter.databinding.MediaInfoLayoutBinding
import tel.jeelpa.otter.ui.adapters.CharacterCardAdapter
import tel.jeelpa.otter.ui.adapters.MediaCardAdapter
import tel.jeelpa.otter.ui.adapters.RelationsAdapter
import tel.jeelpa.otter.ui.generic.ViewBindingFragment
import tel.jeelpa.otter.ui.generic.getNavParentFragment
import tel.jeelpa.otter.ui.generic.getOuterNavController
import tel.jeelpa.otter.ui.generic.initRecycler
import tel.jeelpa.otter.ui.generic.observeFlow
import tel.jeelpa.otter.ui.generic.showToast
import tel.jeelpa.otter.ui.generic.visibilityGone
import tel.jeelpa.otterlib.models.AppMediaType
import tel.jeelpa.otterlib.models.MediaCardData
import javax.inject.Inject

@AndroidEntryPoint
class MangaDetailsInfoFragment :
    ViewBindingFragment<MediaInfoLayoutBinding>(MediaInfoLayoutBinding::inflate) {
    private val mangaDetailsViewModel: MangaDetailsViewModel by viewModels(ownerProducer = { getNavParentFragment() })

    @Inject lateinit var markwon: Markwon


    private fun navigateToDetails(mediaCardData: MediaCardData){
        val destination = when(mediaCardData.type) {
            AppMediaType.ANIME -> MangaDetailsFragmentDirections.toAnimeDetailsFragment(mediaCardData)
            AppMediaType.MANGA -> MangaDetailsFragmentDirections.toSelf(mediaCardData)
            else -> throw IllegalStateException("Unknown Media Type")
        }
        getOuterNavController().navigate(destination)
    }

    override fun onCreateBindingView() {
        binding.totalMediaItemText.text = getString(R.string.total_media, "Chapters")
        binding.averageDurationHolder.visibilityGone()
        binding.averageDurationText.visibilityGone()
        binding.openings.visibilityGone()
        binding.endings.visibilityGone()

        val maxLines = binding.synopsisTextHolder.maxLines

        initRecycler(
            RelationsAdapter(::navigateToDetails),
            binding.relationsRecyclerView,
            binding.relationShimmer.root,
            mangaDetailsViewModel.mangaDetails,
            flowData = { it.relations }
        )

        initRecycler(
            MediaCardAdapter(::navigateToDetails),
            binding.recommendationRecyclerView,
            binding.recommendationShimmer.root,
            mangaDetailsViewModel.mangaDetails,
            flowData = { it.recommendation }
        )

        val characterAdapter = CharacterCardAdapter {
            showToast(
                "CLICKED CHARACTER $it",
                Toast.LENGTH_SHORT
            )
        }
        binding.charactersRecyclerView.apply {
            adapter = characterAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }


        mangaDetailsViewModel.mangaDetails.observeFlow(viewLifecycleOwner) {
            it ?: return@observeFlow

            val ctx = requireContext()
            with(binding) {
                meanScoreHolder.text = it.meanScore.toString()
                statusHolder.text = it.status.name
                totalMediaItemHolder.text = it.chapters.toString()
                averageDurationHolder.text = it.duration.toString()
                formatHolder.text = it.format
                sourceHolder.text = it.source
                studioHolder.text = it.studios.joinToString(" | ")
                seasonHolder.text = it.season
                startDateHolder.text = it.startDate.toString()
                endDateHolder.text = it.endDate.toString()

                markwon.setMarkdown(synopsisTextHolder, it.description)

                synopsisTextHolder.setOnClickListener {
                    if (synopsisTextHolder.maxLines == Int.MAX_VALUE) {
                        synopsisTextHolder.maxLines = maxLines
                    } else {
                        synopsisTextHolder.maxLines = Int.MAX_VALUE
                    }
                }

                for(i in it.synonyms) {
                    synomynsChipGroup.addView( Chip(ctx).apply { text = i } )
                }

                for(i in it.genres) {
                    genresChipGroup.addView( Chip(ctx).apply { text = i } )
                }

                for(i in it.tags) {
                    tagsChipGroup.addView( Chip(ctx).apply { text = i } )
                }

                it.characters.let { characters ->
                    if (characters.isEmpty()) charactersText.visibilityGone()
                    else characterAdapter.setData(characters)
                }
            }
        }
    }
}