package tel.jeelpa.otter.ui.fragments.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import tel.jeelpa.otter.R
import tel.jeelpa.otter.activities.SettingsActivity
import tel.jeelpa.otter.databinding.FragmentUserBinding
import tel.jeelpa.otter.ui.adapters.MediaCardPagingAdapter
import tel.jeelpa.otter.ui.fragments.mediaCommon.MediaEditorBottomSheetFactory
import tel.jeelpa.otter.ui.generic.autoCleared
import tel.jeelpa.otter.ui.generic.initPagedRecycler
import tel.jeelpa.otter.ui.generic.navigateToMediaDetails
import tel.jeelpa.otter.ui.generic.observeFlow
import tel.jeelpa.plugininterface.tracker.models.MediaCardData
import javax.inject.Inject

@AndroidEntryPoint
class UserFragment : Fragment() {

    private var binding: FragmentUserBinding by autoCleared()
    private val userViewModel: UserViewModel by viewModels()
    @Inject lateinit var getMediaEditorBottomSheet: MediaEditorBottomSheetFactory

    private fun navigateToDetails(mediaCardData: MediaCardData) =
        requireContext().navigateToMediaDetails(mediaCardData)

    private fun editMediaItem(mediaCardData: MediaCardData): Boolean {
        val bottomSheet = getMediaEditorBottomSheet(mediaCardData)
        bottomSheet.show(parentFragmentManager, null)
        return true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        binding.avatarHolder.imageTintMode = null

        binding.avatarHolder.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }

        userViewModel.userData.observeFlow(viewLifecycleOwner) {
            binding.username.text = it.username
            binding.episodesCountHolder.text =
                requireContext().getString(R.string.episodes_watched, it.episodeCount)
            binding.chaptersCountHolder.text =
                requireContext().getString(R.string.chapters_read, it.chapterCount)
            binding.avatarHolder.apply {
                binding.avatarHolder.scaleType = ImageView.ScaleType.CENTER_CROP
                load(it.profileImage)
            }
        }

        initPagedRecycler(
            MediaCardPagingAdapter(::navigateToDetails, ::editMediaItem),
            binding.animeContinue,
            userViewModel.currentAnime
        )

        initPagedRecycler(
            MediaCardPagingAdapter(::navigateToDetails, ::editMediaItem),
            binding.mangaContinue,
            userViewModel.currentManga
        )

        initPagedRecycler(
            MediaCardPagingAdapter(::navigateToDetails, ::editMediaItem),
            binding.recommendations,
            userViewModel.recommendations
        )

        return binding.root
    }
}