package tel.jeelpa.otter.ui.fragments.exoplayer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerView
import androidx.media3.ui.TrackSelectionDialogBuilder
import androidx.navigation.fragment.navArgs
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import tel.jeelpa.otter.R
import tel.jeelpa.otter.databinding.ExoPlayerControlViewBinding
import tel.jeelpa.otter.databinding.FragmentExoplayerBinding
import tel.jeelpa.otter.ui.generic.autoCleared
import tel.jeelpa.otter.ui.generic.getNavControllerFromHost
import tel.jeelpa.otter.ui.generic.goFullScreen
import tel.jeelpa.otter.ui.generic.hideFullScreen
import tel.jeelpa.otter.ui.generic.observeFlow
import tel.jeelpa.otter.ui.generic.observeUntil
import tel.jeelpa.otter.ui.generic.showToast
import tel.jeelpa.otter.ui.generic.visibilityGone
import tel.jeelpa.plugininterface.models.Video
import javax.inject.Inject


@AndroidEntryPoint
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class ExoplayerFragment : Fragment() {
    private val exoplayerViewModel: ExoPlayerViewModel by activityViewModels()
    private var binding: FragmentExoplayerBinding by autoCleared()
    private var exoplayerControllerBinding: ExoPlayerControlViewBinding by autoCleared()
    private val videoSourcesLiveDataCache = MutableStateFlow(listOf<Video>())
    private val exoNavArgs by navArgs<ExoplayerFragmentArgs>()
    private lateinit var mediaSession: MediaSession

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
            exoplayer.apply {
                setMediaSource(mediaSource)
                prepare()
                play()
            }
        }

        ssDialog.show(parentFragmentManager, tag)
    }

    private fun toggleLock() {
        val lockBtn = exoplayerControllerBinding.exoLock
        val unlockBtn = exoplayerControllerBinding.exoUnlock
        val timeline = exoplayerControllerBinding.exoProgress
        val container = exoplayerControllerBinding.exoControllerCont
        val screen = exoplayerControllerBinding.exoBlackScreen

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
        binding.root.toggleLock()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExoplayerBinding.inflate(inflater, container, false)
        exoplayerControllerBinding = ExoPlayerControlViewBinding.bind(binding.root)
        mediaSession = MediaSession.Builder(requireContext(), exoplayer).build()

        binding.root.apply {
            player = exoplayer
            setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
            setShutterBackgroundColor(Color.TRANSPARENT)
        }

        exoplayer.prepare()

        // use the player itself to dictate some UI components reactively on state change
        exoplayer.addListener(object : Player.Listener {
            // animate Play/Pause Button on state change
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    exoplayerControllerBinding.exoPlayPause.load(R.drawable.anim_play_to_pause)
                } else {
                    exoplayerControllerBinding.exoPlayPause.load(R.drawable.anim_pause_to_play)
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
                exoplayerControllerBinding.exoAnimeTitle.text = mediaMetadata.title
            }
        })

        // Navigate Up on clicking back
        exoplayerControllerBinding.exoBack.setOnClickListener {
            requireActivity().getNavControllerFromHost(R.id.anime_activity_container_view)
                .navigateUp()
        }

        exoplayerControllerBinding.exoQuality.setOnClickListener {
            binding.root.showTrackSelectionDialog()
        }

        // Source Selection Dialog
        val videoServers = exoNavArgs.videoServers.list

        // collect the flow once, cache it
        val extractedSharedFlow = exoplayerViewModel.extractVideos(videoServers).shareIn(lifecycleScope, SharingStarted.Eagerly)
        extractedSharedFlow.observeFlow(viewLifecycleOwner) {
            videoSourcesLiveDataCache.value += it
        }
        // also start playing in exoplayer when first video source arrives
        // observe only the first event from shared flow
        // apparently this works too
        extractedSharedFlow.observeUntil(viewLifecycleOwner, predicate = { true }){
            val mediaSource = exoplayerViewModel.createMediaSourceFromVideo(it)
            exoplayer.setMediaSource(mediaSource)
        }

        // THIS WORKS, TESTED
//        lifecycleScope.launch {
//            extractedSharedFlow.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collect {
//                val mediaSource = exoplayerViewModel.createMediaSourceFromVideo(it)
//                exoplayer.setMediaSource(mediaSource)
//                cancel("Stop after playing first video")
//            }
//        }


//        extractedSharedFlow.first()

        // Show Source Selector on Button click
        exoplayerControllerBinding.exoSource.setOnClickListener {
            showDialog()
        }

        // Lock Button
        exoplayerControllerBinding.exoLock
            .setOnClickListener { toggleLock() }
        // Unlock Button
        exoplayerControllerBinding.exoUnlock
            .setOnClickListener { toggleLock() }

        return binding.root
    }

    override fun onDestroyView() {
        mediaSession.release()
        exoplayer.release()
        super.onDestroyView()
    }
}

@androidx.media3.common.util.UnstableApi
class ExoplayerGestureView(
    context: Context,
    attrSet: AttributeSet? = null,
    defStyleAttr: Int,
) : PlayerView(context, attrSet, defStyleAttr){
    constructor(context: Context, attrSet: AttributeSet?) : this(context, attrSet, 0)
    private var locked = false
    private enum class ViewSide {
        LEFT,
        RIGHT
    }

    fun showTrackSelectionDialog() {
        val currentPlayer = player ?: return context.showToast("Player is null.")
        val trackSelectionDialogBuilder =
            // TODO : getString available qualities from strings.xml
            TrackSelectionDialogBuilder(
                context,"Available Qualities",currentPlayer,androidx.media3.common.C.TRACK_TYPE_VIDEO
            )
        trackSelectionDialogBuilder.setTheme(android.R.style.Theme_Material_Dialog_NoActionBar_MinWidth)
        trackSelectionDialogBuilder.build().show()
    }

    fun toggleLock(){
        locked = !locked
        if(locked){
            setOnTouchListener(null)
        } else {
            setOnTouchListener(touchListener)
        }
    }

    private fun getViewSide(evt: MotionEvent) : ViewSide {
        return when (rootView.width/2 > evt.x){
            true -> ViewSide.LEFT
            false -> ViewSide.RIGHT
        }
    }

    private val gesturesDetector = GestureDetector(context, object : SimpleOnGestureListener(){

        // TODO : play animations when triggered
        override fun onDoubleTapEvent(e: MotionEvent): Boolean {
            // if no media is playing, dont handle the event
            if (player?.mediaItemCount == 0) return false

            hideController()
            when (getViewSide(e)){
                ViewSide.LEFT -> player?.seekBack()
                ViewSide.RIGHT -> player?.seekForward()
            }
            return true
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            // since we only consider vertical scrolls,
            // we don't care if the user scrolled horizontally in the first place
            return false // TODO : HANDLE THE EVENT AND RETURN TRUE
        }

    })

    @SuppressLint("ClickableViewAccessibility")
    val touchListener = OnTouchListener { view, event ->
        if (event != null && gesturesDetector.onTouchEvent(event)){
            true
        } else {
            super.onTouchEvent(event)
        }
    }

     init {
         setOnTouchListener(touchListener)
     }

}