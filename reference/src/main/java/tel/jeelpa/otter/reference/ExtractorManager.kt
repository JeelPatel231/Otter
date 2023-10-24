package tel.jeelpa.otter.reference

import android.net.Uri
import tel.jeelpa.otter.reference.models.Video

class ExtractorManager {
    private val _extractorsLoaded = mutableListOf<Extractor>()

    val extractors
        get() = _extractorsLoaded.toList()

    fun extract(uri: String): List<Video> {
        val host = Uri.parse(uri).host
            ?: throw IllegalStateException("Invalid Url, Failed to parse URI")

        return _extractorsLoaded
            .filter { it.canExtract(host) }
            .flatMap { it.extract(uri) }
    }

    // TODO : any parsers shouldn't be able to access this and register anything
    fun registerExtractor(extractor: Extractor){
        _extractorsLoaded.add(extractor)
    }
}