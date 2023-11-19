package tel.jeelpa.plugininterface.anime.extractor

import tel.jeelpa.plugininterface.models.Video
import tel.jeelpa.plugininterface.models.VideoServer

interface Extractor {
    fun canExtract(server: VideoServer) : Boolean

    suspend fun extract(server: VideoServer, extraData: Map<String, String> = emptyMap()): List<Video>
}
