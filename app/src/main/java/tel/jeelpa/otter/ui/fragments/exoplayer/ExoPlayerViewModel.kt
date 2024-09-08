package tel.jeelpa.otter.ui.fragments.exoplayer

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import tel.jeelpa.aniyomi_runtime.AniyomiPluginRepo
import tel.jeelpa.otter.ui.generic.CreateMediaSourceFromVideo
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
    private val aniyomiPlugins: AniyomiPluginRepo
) : ViewModel() {

    fun getVideoLinks(episodeLink: String, selectedPlugin: String) =
        aniyomiPlugins.getAllPlugins().map { pluginList ->
            withContext(Dispatchers.IO) {
                pluginList.mapNotNull { it.getOrNull() }
                    .first { it.first.uniqueId == selectedPlugin }
                    .second
                    .loadVideoLinks(episodeLink)
            }
        }
}