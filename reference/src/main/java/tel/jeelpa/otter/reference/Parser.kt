package tel.jeelpa.otter.reference

import tel.jeelpa.otter.reference.models.Episode
import tel.jeelpa.otter.reference.models.ShowResponse
import tel.jeelpa.otter.reference.models.VideoServer

interface Parser : Plugin {
    val name: String
    val isNSFW: Boolean
    val dubAvailable: Boolean

    fun search(query: String): List<ShowResponse>

    fun loadEpisodes(animeLink: String): List<Episode>

    fun loadVideoServers(episodeLink: String): List<VideoServer>
}

abstract class BaseParser : Parser {
    abstract val registerUseCase: RegisterUseCase

    final override fun register() {
        registerUseCase.registerParser(this)
    }

    final override fun toString(): String {
        return this.name
    }
}