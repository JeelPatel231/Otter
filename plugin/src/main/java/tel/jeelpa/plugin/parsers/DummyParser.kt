package tel.jeelpa.plugin.parsers

class DummyParser : tel.jeelpa.plugininterface.anime.parser.BaseParser() {
    override val name: String = "DummyParser"

    override val isNSFW: Boolean = false

    override val dubAvailable: Boolean = false

    override suspend fun search(query: String): List<tel.jeelpa.plugininterface.models.ShowResponse> {
        return listOf(
            tel.jeelpa.plugininterface.models.ShowResponse(
                "Mock Name",
                "https://example.com/MockLink",
                "https://example.com/cover.jpg",
            )
        )
    }

    override suspend fun loadEpisodes(animeLink: String): List<tel.jeelpa.plugininterface.models.Episode> {
        return listOf(
            tel.jeelpa.plugininterface.models.Episode(
                "Episode 1",
                "https://example.com/mocklink/episode/1",
                "Mock Title Episode 1",
                "https://example.com/mocklink/thumb.jpg",
                "Mock Description 1",
                false
            ),
            tel.jeelpa.plugininterface.models.Episode(
                "Episode 2",
                "https://example.com/mocklink/episode/2",
                "Mock Title Episode 2",
                "https://example.com/mocklink/thumb.jpg",
                "Mock Description 2",
                false
            )
        )
    }

    override suspend fun loadVideoServers(episodeLink: String): List<tel.jeelpa.plugininterface.models.VideoServer> {
        return listOf(
            tel.jeelpa.plugininterface.models.VideoServer(
                "Mock Video Server 1",
                tel.jeelpa.plugininterface.models.FileUrl("https://example.com")
            ),
            tel.jeelpa.plugininterface.models.VideoServer(
                "Mock Video Server 2",
                tel.jeelpa.plugininterface.models.FileUrl("https://example.com")
            ),
        )
    }
}