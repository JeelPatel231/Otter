package tel.jeelpa.otter.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import coil.size.Scale
import dagger.hilt.android.AndroidEntryPoint
import tel.jeelpa.otter.R
import tel.jeelpa.otter.databinding.FragmentUserBinding
import tel.jeelpa.otter.factories.TrackerClientFactory
import tel.jeelpa.otter.ui.adapters.MediaCardAdapter
import tel.jeelpa.otter.ui.generic.autoCleared
import tel.jeelpa.otter.ui.generic.initRecycler
import tel.jeelpa.otter.ui.generic.navigateToMediaDetails
import tel.jeelpa.otter.ui.generic.observeFlow
import tel.jeelpa.otter.ui.generic.showToast
import tel.jeelpa.otterlib.models.MediaCardData
import javax.inject.Inject

@AndroidEntryPoint
class UserFragment: Fragment() {

    private var binding: FragmentUserBinding by autoCleared()
    private val userViewModel: UserViewModel by viewModels()
    @Inject lateinit var trackerFactory: TrackerClientFactory

    private fun navigateToDetails(mediaCardData: MediaCardData) =
        requireContext().navigateToMediaDetails(mediaCardData)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        binding.avatarHolder.imageTintMode = null

        binding.avatarHolder.setOnClickListener {
            showToast("Not Implemented")
        }
//        binding.logoutButton.setOnClickListener {
//            lifecycleScope.launch(Dispatchers.IO) {
//                trackerFactory().logout()
//            }
//        }

        userViewModel.userData.observeFlow(viewLifecycleOwner){
            binding.username.text = it.username
            binding.episodesCountHolder.text = requireContext().getString(R.string.episodes_watched, it.episodeCount)
            binding.chaptersCountHolder.text = requireContext().getString(R.string.chapters_read, it.chapterCount)
            binding.avatarHolder.load(it.profileImage) {
                scale(Scale.FIT)
            }
        }

        initRecycler(
            MediaCardAdapter(::navigateToDetails),
            binding.animeContinueRecycler,
            binding.animeContinueShimmer.root,
            userViewModel.currentAnime
        )

        initRecycler(
            MediaCardAdapter(::navigateToDetails),
            binding.mangaContinueRecycler,
            binding.mangaContinueShimmer.root,
            userViewModel.currentManga
        )

        initRecycler(
            MediaCardAdapter(::navigateToDetails),
            binding.recommendationsRecycler,
            binding.recommendationsShimmer.root,
            userViewModel.recommendations
        )

//        binding.pluginsButton.setOnClickListener {
//            showToast("Not Implemented")
//            getOuterNavController().navigate(MainFragmentDirections.toPluginFragment())
//        }
        return binding.root
    }
}