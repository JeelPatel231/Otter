package tel.jeelpa.aniyomi_runtime.adapter

import eu.kanade.tachiyomi.animesource.AnimeCatalogueSource
import eu.kanade.tachiyomi.animesource.model.AnimeFilterList
import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.model.SEpisode
import tel.jeelpa.plugininterface.anime.parser.BaseParser
import tel.jeelpa.plugininterface.models.Episode
import tel.jeelpa.plugininterface.models.FileUrl
import tel.jeelpa.plugininterface.models.ShowResponse
import tel.jeelpa.plugininterface.models.Video
import tel.jeelpa.plugininterface.models.VideoType

class AnimeCatalogueSourceToParser(
    private val animeSource: AnimeCatalogueSource
): BaseParser() {
    override val name = animeSource.name
    override val isNSFW = false

    override val dubAvailable = false

    override suspend fun search(query: String): List<ShowResponse> {
        val page = animeSource.getSearchAnime(1, query, AnimeFilterList())
        return page.animes.map {
            ShowResponse(
                name = it.title,
                link = it.url,
                coverUrl = FileUrl(it.thumbnail_url ?: ""),
            )
        }
    }

    override suspend fun loadEpisodes(animeLink: String): List<Episode> {
        val adapted = SAnime.create().apply { url = animeLink }
        val episodeList = animeSource.getEpisodeList(adapted)
        return episodeList.map {
            Episode(
                number = it.episode_number.toString(),
                link = it.url,
                title = it.name,
            )
        }
    }

    override suspend fun loadVideoLinks(episodeLink: String): List<Video> {
        val adapted = SEpisode.create().apply { url = episodeLink }
        val videoLinks = animeSource.getVideoList(adapted)
        return videoLinks.map { video ->
            // TODO: BRUH?????
            val fileUrl =
                video.headers?.toMap()
                    ?.let { FileUrl(video.videoUrl ?: video.url, it) }
                    ?: FileUrl(video.videoUrl ?: video.url)

            Video(
                title = video.quality,
                quality = null,
                format = videoTypeFromUrl(video.videoUrl ?: video.url),
                url = fileUrl,
                size = null,
                extraNote = null
            )
        }
    }

    private fun videoTypeFromUrl(url: String): VideoType = when {
        url.endsWith(".m3u8") -> VideoType.M3U8
        url.endsWith(".hls") -> VideoType.M3U8
        url.endsWith(".mpd") -> VideoType.DASH
        else -> VideoType.CONTAINER
    }
}