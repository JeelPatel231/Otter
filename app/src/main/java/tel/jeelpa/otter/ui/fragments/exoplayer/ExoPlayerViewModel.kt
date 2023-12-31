package tel.jeelpa.otter.ui.fragments.exoplayer

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.channelFlow
import tel.jeelpa.otter.plugins.ExtractorManager
import tel.jeelpa.otter.ui.generic.CreateMediaSourceFromVideo
import tel.jeelpa.otter.ui.generic.asyncForEach
import tel.jeelpa.plugininterface.models.VideoServer
import javax.inject.Inject


/*class ExoplayerStateHolder {
    private var playing = false
    private var playbackPosition = 0L

    fun saveState(exoplayer: ExoPlayer){
        playing = exoplayer.isPlaying
        playbackPosition = exoplayer.currentPosition
    }

    fun restoreState(exoplayer: ExoPlayer) {
        if(playing) exoplayer.play() else exoplayer.pause()
        exoplayer.seekTo(playbackPosition)
    }
}*/

@HiltViewModel
class ExoPlayerViewModel @Inject constructor(
    val createMediaSourceFromVideo: CreateMediaSourceFromVideo,
    private val extractorManager: ExtractorManager
) : ViewModel() {
    fun extractVideos(videoServers: Collection<VideoServer>) = channelFlow {
        videoServers.asyncForEach { vidServer ->
            extractorManager.extract(vidServer).asyncForEach { video ->
                send(video)
            }
        }
    }
}