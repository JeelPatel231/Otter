package tel.jeelpa.otter.ui.fragments.exoplayer

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import tel.jeelpa.otter.databinding.FragmentExoplayerBinding
import tel.jeelpa.otter.ui.generic.asyncForEach
import tel.jeelpa.otter.ui.generic.autoCleared
import javax.inject.Inject


@AndroidEntryPoint
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class ExoplayerFragment : Fragment() {
    private val exoplayerViewModel: ExoPlayerViewModel by activityViewModels()
    private var binding: FragmentExoplayerBinding by autoCleared()

    @Inject lateinit var exoplayer: ExoPlayer
    private var bottomSheet: BottomSheetSourceSelector by autoCleared()

    // i love race conditions
    private val window by lazy { requireActivity().window }
    private val windowInsetsController by lazy { WindowCompat.getInsetsController(window, window.decorView) }

    override fun onDestroyView() {
        super.onDestroyView()
        // restore device orientation
        windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        exoplayer.release()
    }
    override fun onPause() {
        super.onPause()
        bottomSheet.dismiss()
        exoplayer.pause()
    }
    override fun onResume() {
        super.onResume()
        exoplayer.play()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExoplayerBinding.inflate(inflater, container, false)

        // hide system bars, to enter immersive mode (fullscreen)
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        // set orientation to landscape
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        binding.root.apply {
            player = exoplayer
            setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
            setShutterBackgroundColor(Color.TRANSPARENT)
        }

        exoplayer.prepare()

        val adapter = SourceSelectionAdapter {
            val mediaSource = exoplayerViewModel.createMediaSourceFromVideo(it)
            exoplayer.apply {
                stop()
                setMediaSource(mediaSource)
                prepare()
                play()
            }
        }

        lifecycleScope.launch {
            exoplayerViewModel.videoServers.asyncForEach { vidServer ->
                exoplayerViewModel.extract(vidServer).collect {
                    adapter.add(it)
                }
            }
        }

        bottomSheet = BottomSheetSourceSelector(adapter)
        bottomSheet.show(childFragmentManager, "BottomSheetSourceSelector")

        return binding.root
    }
}