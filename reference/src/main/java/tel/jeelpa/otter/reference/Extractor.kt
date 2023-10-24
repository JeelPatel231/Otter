package tel.jeelpa.otter.reference

import tel.jeelpa.otter.reference.models.Video

interface Extractor : Plugin {
    fun canExtract(domain: String) : Boolean

    fun extract(uri: String): List<Video>
}

abstract class BaseExtractor : Extractor {
    abstract val registerUseCase: RegisterUseCase

    final override fun register() {
        registerUseCase.registerExtractor(this)
    }
}
