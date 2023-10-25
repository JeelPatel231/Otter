package tel.jeelpa.otter.ui.fragments.exoplayer

import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import tel.jeelpa.otter.databinding.FragmentExoplayerBinding
import tel.jeelpa.otter.reference.models.Video
import tel.jeelpa.otter.ui.generic.CreateMediaSourceFromUri
import tel.jeelpa.otter.ui.generic.ViewBindingFragment
import javax.inject.Inject

@HiltViewModel
class ExoplayerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val exoPlayer: ExoPlayer,
    val createMediaSourceFromUri: CreateMediaSourceFromUri,
): ViewModel(){
    val args = ExoplayerFragmentArgs.fromSavedStateHandle(savedStateHandle)

    fun createMediaSource(video: Video): MediaSource {
        return createMediaSourceFromUri(
            video.url.url,
            video.format,
            video.url.headers
        )
    }
}

@AndroidEntryPoint
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class ExoplayerFragment : ViewBindingFragment<FragmentExoplayerBinding>(FragmentExoplayerBinding::inflate) {

    private val exoplayerViewModel: ExoplayerViewModel by viewModels()
    private val exoplayer
        get() = exoplayerViewModel.exoPlayer

    override fun onDestroyBindingView() {
        if(exoplayerViewModel.args.fresh) {
            exoplayer.stop()
        }
        binding.root.player = null
    }

    override fun onCreateBindingView() {
        // clear the exoplayer instance
        exoplayer.apply {
            if (exoplayerViewModel.args.fresh) {
                stop()
                clearMediaItems()
            }
            prepare()
            play()
        }

        binding.root.player = exoplayer

        val firstVideoSource = exoplayerViewModel.args.videos.first()
        exoplayer.setMediaSource(exoplayerViewModel.createMediaSource(firstVideoSource))
    }
}