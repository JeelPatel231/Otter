package tel.jeelpa.otter.ui.fragments.animedetails

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import tel.jeelpa.otter.R
import tel.jeelpa.otter.databinding.MediaInfoLayoutBinding
import tel.jeelpa.otter.ui.adapters.CharacterCardAdapter
import tel.jeelpa.otter.ui.adapters.MediaCardAdapter
import tel.jeelpa.otter.ui.adapters.RelationsAdapter
import tel.jeelpa.otter.ui.adapters.SimpleTextRecyclerAdapter
import tel.jeelpa.otter.ui.fragments.character.CharacterActivity
import tel.jeelpa.otter.ui.fragments.mediaCommon.MediaEditorBottomSheetFactory
import tel.jeelpa.otter.ui.generic.autoCleared
import tel.jeelpa.otter.ui.generic.copyToClipboard
import tel.jeelpa.otter.ui.generic.initPagedRecycler
import tel.jeelpa.otter.ui.generic.initRecycler
import tel.jeelpa.otter.ui.generic.navigateToMediaDetails
import tel.jeelpa.otter.ui.generic.observeFlow
import tel.jeelpa.otter.ui.generic.showToast
import tel.jeelpa.otter.ui.generic.toNullString
import tel.jeelpa.otter.ui.generic.visibilityGone
import tel.jeelpa.plugininterface.tracker.models.MediaCardData
import javax.inject.Inject

@AndroidEntryPoint
class AnimeDetailsInfoFragment: Fragment() {
    private val animeDetailsViewModel: AnimeDetailsViewModel by activityViewModels()
    private var binding: MediaInfoLayoutBinding by autoCleared()
    @Inject lateinit var getMediaEditorBottomSheet: MediaEditorBottomSheetFactory
    @Inject lateinit var markwon: Markwon

    private fun navigateToDetails(mediaCardData: MediaCardData) =
        requireContext().navigateToMediaDetails(mediaCardData)

    private fun editMediaItem(mediaCardData: MediaCardData): Boolean {
        val bottomSheet = getMediaEditorBottomSheet(mediaCardData)
        bottomSheet.show(parentFragmentManager, null)
        return true
    }

    override fun onResume() {
        super.onResume()
        // Update Height after ViewPager makes fragment transaction
        binding.root.requestLayout()
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MediaInfoLayoutBinding.inflate(inflater, container, false)
        binding.totalMediaItemText.text = getString(R.string.total_media, "Episodes")
        val maxLines = binding.synopsisTextHolder.maxLines

        initRecycler(
            RelationsAdapter(::navigateToDetails),
            binding.relationRecyclerView,
            animeDetailsViewModel.animeDetails,
            flowData = { it.relations }
        )

        initRecycler(
            MediaCardAdapter(::navigateToDetails, ::editMediaItem),
            binding.recommendationRecyclerView,
            animeDetailsViewModel.animeDetails,
            flowData = { it.recommendation }
        )

        val characterAdapter = CharacterCardAdapter {
            val intent = Intent(requireActivity(), CharacterActivity::class.java)
                .putExtra("characterId", it.id)
            startActivity(intent)
        }
        initPagedRecycler(
            characterAdapter,
            binding.charactersRecyclerView,
            animeDetailsViewModel.characters
        )

        animeDetailsViewModel.animeDetails.observeFlow(viewLifecycleOwner) {
            val ctx = requireContext()
            with(binding) {
                meanScoreHolder.text = it.meanScore.toString()
                statusHolder.text = it.status.name
                totalMediaItemHolder.text = it.episodes.toNullString()
                averageDurationHolder.text = it.duration.toNullString()
                formatHolder.text = it.format
                sourceHolder.text = it.source
                studioHolder.text = it.studios.joinToString(" | ")
                seasonHolder.text = it.season
                startDateHolder.text = it.startDate.toNullString()
                endDateHolder.text = it.endDate.toNullString()

                markwon.setMarkdown(synopsisTextHolder, it.description)

                synopsisTextHolder.setOnClickListener {
                    if (synopsisTextHolder.maxLines == Int.MAX_VALUE) {
                        synopsisTextHolder.maxLines = maxLines
                    } else {
                        synopsisTextHolder.maxLines = Int.MAX_VALUE
                    }
                }

                for (i in it.synonyms) {
                    synomynsChipGroup.addView(Chip(ctx).apply { text = i })
                }

                for (i in it.genres) {
                    genresChipGroup.addView(Chip(ctx).apply { text = i })
                }

                for (i in it.tags) {
                    tagsChipGroup.addView(Chip(ctx).apply { text = i })
                }
            }
        }

        val openingsAdapter = SimpleTextRecyclerAdapter(
            onItemLongClick = {
                requireContext().copyToClipboard("Song", it)
                showToast("Copied to Clipboard", Toast.LENGTH_SHORT)
            })

        binding.openingsRecyclerView.apply {
            adapter = openingsAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        val endingsAdapter = SimpleTextRecyclerAdapter(
            onItemLongClick = {
                requireContext().copyToClipboard("Song", it)
                showToast("Copied to Clipboard", Toast.LENGTH_SHORT)
            })
        binding.endingsRecyclerView.apply {
            adapter = endingsAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        animeDetailsViewModel.mediaOpenings.observeFlow(viewLifecycleOwner) { ops ->
            if (ops.isEmpty()) binding.openings.visibilityGone()
            else openingsAdapter.setData(ops)
        }

        animeDetailsViewModel.mediaEndings.observeFlow(viewLifecycleOwner) { eds ->
            if (eds.isEmpty()) binding.endings.visibilityGone()
            else endingsAdapter.setData(eds)
        }

        return binding.root
    }
}