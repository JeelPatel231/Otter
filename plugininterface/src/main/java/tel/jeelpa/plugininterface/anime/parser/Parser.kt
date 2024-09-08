package tel.jeelpa.plugininterface.anime.parser

import tel.jeelpa.plugininterface.models.Episode
import tel.jeelpa.plugininterface.models.ShowResponse
import tel.jeelpa.plugininterface.models.Video

interface Parser {
    val name: String
    val isNSFW: Boolean
    val dubAvailable: Boolean

    suspend fun search(query: String): List<ShowResponse>

    suspend fun loadEpisodes(animeLink: String): List<Episode>

    suspend fun loadVideoLinks(episodeLink: String): List<Video>
}

abstract class BaseParser : Parser {
    final override fun toString(): String {
        return this.name
    }
}