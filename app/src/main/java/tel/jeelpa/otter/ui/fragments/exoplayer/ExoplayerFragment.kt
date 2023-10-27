package tel.jeelpa.otter.ui.fragments.exoplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import tel.jeelpa.otter.databinding.FragmentExoplayerBinding
import tel.jeelpa.otter.ui.generic.CreateMediaSourceFromVideo
import tel.jeelpa.otter.ui.generic.autoCleared
import javax.inject.Inject


@AndroidEntryPoint
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class ExoplayerFragment : Fragment() {
    private var binding: FragmentExoplayerBinding by autoCleared()
    private var bottomSheet: BottomSheetSourceSelector by autoCleared()
    private val args: ExoplayerFragmentArgs by navArgs()

    @Inject lateinit var exoplayer: ExoPlayer
    @Inject lateinit var createMediaSourceFromVideo : CreateMediaSourceFromVideo

    override fun onDestroyView() {
        super.onDestroyView()
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

        binding.root.player = exoplayer
        exoplayer.prepare()

        val adapter = SourceSelectionAdapter(args.videos.toList()){
            val mediaSource = createMediaSourceFromVideo(it)
            exoplayer.setMediaSource(mediaSource)
        }

        bottomSheet = BottomSheetSourceSelector(adapter)
        bottomSheet.show(childFragmentManager, "BottomSheetSourceSelector")

        return binding.root
    }
}