package tel.jeelpa.plugin

import okhttp3.OkHttpClient
import tel.jeelpa.otter.reference.BaseParser
import tel.jeelpa.otter.reference.RegisterUseCase
import tel.jeelpa.otter.reference.models.Episode
import tel.jeelpa.otter.reference.models.FileUrl
import tel.jeelpa.otter.reference.models.ShowResponse
import tel.jeelpa.otter.reference.models.VideoServer

class DummyParser(
    private val okHttpClient: OkHttpClient,
    override val registerUseCase: RegisterUseCase
): BaseParser() {

    override val name: String = "DummyParser"

    override val isNSFW: Boolean = false

    override val dubAvailable: Boolean = false

    override fun search(query: String): List<ShowResponse> {
        return listOf(
            ShowResponse(
                "Mock Name",
                "https://example.com/MockLink",
                "https://example.com/cover.jpg",
            )
        )
    }

    override fun loadEpisodes(animeLink: String): List<Episode> {
        return listOf(
            Episode(
                "Episode 1",
                "https://example.com/mocklink/episode/1",
                "Mock Title Episode 1",
                "https://example.com/mocklink/thumb.jpg",
                "Mock Description 1",
                false
            ),
            Episode(
                "Episode 2",
                "https://example.com/mocklink/episode/2",
                "Mock Title Episode 2",
                "https://example.com/mocklink/thumb.jpg",
                "Mock Description 2",
                false
            )
        )
    }

    override fun loadVideoServers(episodeLink: String): List<VideoServer> {
        return listOf(
            VideoServer(
                "Mock Video Server 1",
                FileUrl("https://example.com")
            ),
            VideoServer(
                "Mock Video Server 2",
                FileUrl("https://example.com")
            ),
        )
    }
}