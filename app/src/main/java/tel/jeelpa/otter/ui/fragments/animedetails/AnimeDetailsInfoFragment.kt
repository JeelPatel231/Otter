package tel.jeelpa.otter.ui.fragments.animedetails

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
import tel.jeelpa.otter.ui.adapters.SimpleTextRecyclerAdapter
import tel.jeelpa.otter.ui.generic.ViewBindingFragment
import tel.jeelpa.otter.ui.generic.copyToClipboard
import tel.jeelpa.otter.ui.generic.getNavParentFragment
import tel.jeelpa.otter.ui.generic.getOuterNavController
import tel.jeelpa.otter.ui.generic.initRecycler
import tel.jeelpa.otter.ui.generic.observeFlow
import tel.jeelpa.otter.ui.generic.showToast
import tel.jeelpa.otter.ui.generic.visibilityGone
import tel.jeelpa.otterlib.models.AppMediaType
import javax.inject.Inject

@AndroidEntryPoint
class AnimeDetailsInfoFragment :
    ViewBindingFragment<MediaInfoLayoutBinding>(MediaInfoLayoutBinding::inflate) {
    private val animeDetailsViewModel: AnimeDetailsViewModel by viewModels(ownerProducer = { getNavParentFragment() })

    @Inject lateinit var markwon: Markwon

    private fun navigateToDetails(id: Int, type: AppMediaType) {
        val destination = when (type) {
            AppMediaType.ANIME -> AnimeDetailsFragmentDirections.toSelf(id)
            AppMediaType.MANGA -> AnimeDetailsFragmentDirections.toMangaDetailsFragment(id)
            else -> throw IllegalStateException("Unknown Media Type")
        }
        getOuterNavController().navigate(destination)
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    override fun onCreateBindingView() {
        binding.totalMediaItemText.text = getString(R.string.total_media, "Episodes")
        val maxLines = binding.synopsisTextHolder.maxLines
        binding.videoView.setFullscreenButtonClickListener {
            getOuterNavController().navigate(
                AnimeDetailsFragmentDirections.toExoplayerFragment(cleanExit = false)
            )
        }

        initRecycler(
            RelationsAdapter(::navigateToDetails),
            binding.relationsRecyclerView,
            binding.relationShimmer.root,
            animeDetailsViewModel.animeDetails,
            flowData = { it.relations }
        )

        initRecycler(
            MediaCardAdapter(::navigateToDetails),
            binding.recommendationRecyclerView,
            binding.recommendationShimmer.root,
            animeDetailsViewModel.animeDetails,
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
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        ///
        // TODO : fix exoplayer scoping issues
        binding.videoView.visibilityGone()


        animeDetailsViewModel.animeDetails.observeFlow(viewLifecycleOwner) {
            it ?: return@observeFlow

            val ctx = requireContext()
            with(binding) {
                meanScoreHolder.text = it.meanScore.toString()
                statusHolder.text = it.status.name
                totalMediaItemHolder.text = it.episodes.toString()
                averageDurationHolder.text = it.duration.toString()
                formatHolder.text = it.format
                sourceHolder.text = it.source
                studioHolder.text = it.studios.joinToString(" | ")
                seasonHolder.text = it.season
                startDateHolder.text = it.startDate.toString()
                endDateHolder.text = it.endDate.toString()

                markwon.setMarkdown(synopsisTextHolder, it.description)

//                if (it.trailer == null) {
//                    videoView.visibilityGone()
//                } else {
//                    exoplayer.apply {
//                        val sources = listOf(
//                            "https://iv.nboeck.de/latest_version?id=${it.trailer!!.id}",
//                            "https://iv.nboeck.de/latest_version?id=${it.trailer!!.id}&local=true",
//                        )
//                        val mediaSources = sources.map { link ->
//                            createMediaSourceFromUri(link, VideoType.CONTAINER)
//                        }.listIterator()
//                        videoView.player = this
//                        exoplayer.setMediaSource(mediaSources.next())
//                    }
//                }

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

                characterAdapter.setData(it.characters)
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
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        val endingsAdapter = SimpleTextRecyclerAdapter(
            onItemLongClick = {
                requireContext().copyToClipboard("Song", it)
                showToast("Copied to Clipboard", Toast.LENGTH_SHORT)
            })
        binding.endingsRecyclerView.apply {
            adapter = endingsAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        animeDetailsViewModel.mediaOpenings.observeFlow(viewLifecycleOwner) { ops ->
            ops ?: return@observeFlow

            if (ops.isEmpty()) binding.openings.visibilityGone()
            else openingsAdapter.setData(ops)
        }

        animeDetailsViewModel.mediaEndings.observeFlow(viewLifecycleOwner) { eds ->
            eds ?: return@observeFlow

            if (eds.isEmpty()) binding.endings.visibilityGone()
            else endingsAdapter.setData(eds)
        }
    }
}