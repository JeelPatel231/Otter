package tel.jeelpa.otter.ui.fragments.mangadetails

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import tel.jeelpa.otter.R
import tel.jeelpa.otter.databinding.MediaInfoLayoutBinding
import tel.jeelpa.otter.trackerinterface.models.MediaCardData
import tel.jeelpa.otter.ui.adapters.CharacterCardAdapter
import tel.jeelpa.otter.ui.adapters.MediaCardAdapter
import tel.jeelpa.otter.ui.adapters.RelationsAdapter
import tel.jeelpa.otter.ui.fragments.character.CharacterActivity
import tel.jeelpa.otter.ui.generic.autoCleared
import tel.jeelpa.otter.ui.generic.initRecycler
import tel.jeelpa.otter.ui.generic.navigateToMediaDetails
import tel.jeelpa.otter.ui.generic.observeFlow
import tel.jeelpa.otter.ui.generic.visibilityGone
import javax.inject.Inject

@AndroidEntryPoint
class MangaDetailsInfoFragment : Fragment() {
    private var binding: MediaInfoLayoutBinding by autoCleared()
    private val mangaDetailsViewModel: MangaDetailsViewModel by activityViewModels()

    @Inject lateinit var markwon: Markwon

    private fun navigateToDetails(mediaCardData: MediaCardData) =
        requireContext().navigateToMediaDetails(mediaCardData)


    override fun onResume() {
        super.onResume()
        // Update Height after ViewPager makes fragment transaction
        binding.root.requestLayout()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MediaInfoLayoutBinding.inflate(inflater, container, false)

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
            val intent = Intent(requireActivity(), CharacterActivity::class.java)
                .putExtra("characterId", it.id)
            startActivity(intent)
        }
        binding.charactersRecyclerView.apply {
            adapter = characterAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }


        mangaDetailsViewModel.mangaDetails.observeFlow(viewLifecycleOwner) {
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
                    else characterAdapter.submitList(characters)
                }
            }
        }

        return binding.root
    }
}