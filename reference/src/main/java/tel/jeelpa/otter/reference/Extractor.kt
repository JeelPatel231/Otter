package tel.jeelpa.otter.reference

import tel.jeelpa.otter.reference.models.Video
import tel.jeelpa.otter.reference.models.VideoServer

interface Extractor {
    fun canExtract(server: VideoServer) : Boolean

    suspend fun extract(server: VideoServer, extraData: Map<String, String> = emptyMap()): List<Video>
}
