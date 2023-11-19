package tel.jeelpa.otter.plugins

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tel.jeelpa.plugininterface.anime.extractor.Extractor
import tel.jeelpa.plugininterface.models.Video
import tel.jeelpa.plugininterface.models.VideoServer

class ExtractorManager {
    private val _extractorsLoaded = mutableListOf<Extractor>()

    val extractors
        get() = _extractorsLoaded.toList()

    suspend fun extract(server: VideoServer): List<Video> {
        return withContext(Dispatchers.IO) {
            _extractorsLoaded
                .filter { it.canExtract(server) }
                .flatMap { it.extract(server) }
        }
    }

    fun registerExtractor(extractor: Extractor){
        _extractorsLoaded.add(extractor)
    }
}