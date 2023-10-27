package tel.jeelpa.otter.reference

import android.net.Uri
import tel.jeelpa.otter.reference.models.Video
import tel.jeelpa.otter.reference.models.VideoServer

class ExtractorManager {
    private val _extractorsLoaded = mutableListOf<Extractor>()

    val extractors
        get() = _extractorsLoaded.toList()

    suspend fun extract(server: VideoServer): List<Video> {
        val host = Uri.parse(server.embed.url).host
            ?: throw IllegalStateException("Invalid Url, Failed to parse URI")

        return _extractorsLoaded
            .also { println("DEBUG : LOADED EXTRACTORS : $it") }
            .filter { it.canExtract(host) }
            .also { println("DEBUG: FILTERED EXTRACTORS : $it") }
            .flatMap { it.extract(server) }
            .also { println("DEBUG: FINAL EXTRACTED : $it") }
    }

    // TODO : any parsers shouldn't be able to access this and register anything
    fun registerExtractor(extractor: Extractor){
        _extractorsLoaded.add(extractor)
    }
}