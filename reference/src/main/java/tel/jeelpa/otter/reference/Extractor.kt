package tel.jeelpa.otter.reference

import tel.jeelpa.otter.reference.models.Video
import tel.jeelpa.otter.reference.models.VideoServer

interface Extractor : Plugin {
    fun canExtract(domain: String) : Boolean

    suspend fun extract(server: VideoServer, extraData: Map<String, String> = emptyMap()): List<Video>
}

abstract class BaseExtractor : Extractor {
    abstract val registerUseCase: RegisterUseCase

    final override fun register() {
        registerUseCase.registerExtractor(this)
    }
}
