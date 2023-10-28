package tel.jeelpa.otter.ui.fragments.exoplayer

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import tel.jeelpa.otter.R
import tel.jeelpa.otter.databinding.FragmentExoplayerBinding
import tel.jeelpa.otter.reference.models.Video
import tel.jeelpa.otter.ui.generic.autoCleared
import tel.jeelpa.otter.ui.generic.getNavControllerFromHost
import tel.jeelpa.otter.ui.generic.goFullScreen
import tel.jeelpa.otter.ui.generic.hideFullScreen
import tel.jeelpa.otter.ui.generic.observeFlow
import tel.jeelpa.otter.ui.generic.showToast
import tel.jeelpa.otter.ui.generic.visibilityGone
import javax.inject.Inject


@AndroidEntryPoint
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class ExoplayerFragment : Fragment() {
    private val exoplayerViewModel: ExoPlayerViewModel by activityViewModels()
    private var binding: FragmentExoplayerBinding by autoCleared()
    private val videoSourcesLiveDataCache = MutableStateFlow(listOf<Video>())


    @Inject lateinit var exoplayer: ExoPlayer

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // use Translucent navbar Theme for mitigating layout shift in exoplayer
        context.theme.applyStyle(R.style.Theme_Otter_TranslucentNavigation, true)
        goFullScreen()
    }

    override fun onDetach() {
        super.onDetach()
        hideFullScreen()
    }

    override fun onPause() {
        super.onPause()
        exoplayer.pause()
    }

    override fun onResume() {
        super.onResume()
        exoplayer.play()
    }

    private fun showDialog() {
        val ssDialog = SourceSelectionDialog(videoSourcesLiveDataCache){
            val mediaSource = exoplayerViewModel.createMediaSourceFromVideo(it)
            exoplayer.setMediaSource(mediaSource)
        }

        ssDialog.show(childFragmentManager, tag)
    }

    private fun toggleLock() {
        val lockBtn = binding.root.findViewById<ImageButton>(R.id.exo_lock)
        val unlockBtn = binding.root.findViewById<ImageButton>(R.id.exo_unlock)
        val timeline =
            binding.root.findViewById<ExtendedTimeBar>(androidx.media3.ui.R.id.exo_progress)
        val container = binding.root.findViewById<View>(R.id.exo_controller_cont)
        val screen = binding.root.findViewById<View>(R.id.exo_black_screen)

        if (lockBtn.isVisible) {
            // IF UNLOCKED
            lockBtn.visibilityGone()
            unlockBtn.visibility = View.VISIBLE

            container.visibilityGone()
            screen.visibilityGone()
            // remove gesture handlers
            timeline.setForceDisabled(true)
        } else {
            // IF LOCKED
            unlockBtn.visibilityGone()
            lockBtn.visibility = View.VISIBLE

            container.visibility = View.VISIBLE
            screen.visibility = View.VISIBLE

            // add gesture handler
            timeline.setForceDisabled(false)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExoplayerBinding.inflate(inflater, container, false)
        binding.root.apply {
            player = exoplayer
            setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
            setShutterBackgroundColor(Color.TRANSPARENT)
        }

        exoplayer.prepare()

        // use the player itself to dictate some UI components reactively on state change
        exoplayer.addListener(object : Player.Listener {
            val playPause =
                binding.root.findViewById<ImageButton>(androidx.media3.ui.R.id.exo_play_pause)

            // animate Play/Pause Button on state change
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    playPause.load(R.drawable.anim_play_to_pause)
                } else {
                    playPause.load(R.drawable.anim_pause_to_play)
                }
            }

            // show Dialog when media cannot be played
            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                showToast("Error Occurred : $error")
                // for the user to change source
                showDialog()
            }

            // change title on media change
            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                binding.root.findViewById<TextView>(R.id.exo_anime_title).text =
                    mediaMetadata.title
            }
        })

        // Navigate Up on clicking back
        binding.root.findViewById<ImageButton>(R.id.exo_back).setOnClickListener {
            requireActivity().getNavControllerFromHost(R.id.anime_activity_container_view)
                .navigateUp()
        }

        // Source Selection Dialog
        exoplayerViewModel.extractVideos().observeFlow(viewLifecycleOwner) {
            videoSourcesLiveDataCache.value += it
        }

        // Show Source Selector on Button click
        binding.root.findViewById<ImageButton>(R.id.exo_source).setOnClickListener {
            showDialog()
        }

        // Lock Button
        binding.root.findViewById<ImageButton>(R.id.exo_lock)
            .setOnClickListener { toggleLock() }
        // Unlock Button
        binding.root.findViewById<ImageButton>(R.id.exo_unlock)
            .setOnClickListener { toggleLock() }


//        val rightGestureDetector = exoplayerRightAreaGestureListener(requireContext())
//        binding.root.findViewById<View>(R.id.exo_forward_area).setOnTouchListener { view, evt ->
//            rightGestureDetector.onTouchEvent(evt)
//            view.performClick()
//        }


//        val leftGestureDetector = exoplayerLeftAreaGestureListener(requireContext())
//        binding.root.findViewById<View>(R.id.exo_rewind_area).setOnTouchListener { view, evt ->
//            leftGestureDetector.onTouchEvent(evt)
//            view.performClick()
//        }

        return binding.root
    }

    override fun onDestroyView() {
//        requireActivity().setTheme(R.style.Theme_Otter)
        exoplayer.release()
        // restore device orientation
        super.onDestroyView()
    }
}


private fun exoplayerRightAreaGestureListener(
    context: Context
): GestureDetector {
    val gesturesListener = object : GesturesListener() {
        override fun onDoubleClick(event: MotionEvent) {
            context.showToast("Double Clicked")
        }

        override fun onScrollYClick(y: Float) {
            context.showToast("Scrolled Y")
        }

        override fun onSingleClick(event: MotionEvent) {
            context.showToast("Single Clicked")
        }
    }
    return GestureDetector(context, gesturesListener)
}


private fun exoplayerLeftAreaGestureListener(
    context: Context,
): GestureDetector {
    val gesturesListener = object : GesturesListener() {
        override fun onDoubleClick(event: MotionEvent) {
            context.showToast("Double Clicked")
        }

        override fun onScrollYClick(y: Float) {
            context.showToast("Scrolled Y")
        }

        override fun onSingleClick(event: MotionEvent) {
            context.showToast("Single Clicked")
        }
    }
    return GestureDetector(context, gesturesListener)
}