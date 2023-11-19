package tel.jeelpa.plugin.extractors

import android.net.Uri
import tel.jeelpa.plugininterface.anime.extractor.Extractor
import tel.jeelpa.plugininterface.models.Video
import tel.jeelpa.plugininterface.models.VideoServer
import tel.jeelpa.plugininterface.models.VideoType

class DummyExtractor : tel.jeelpa.plugininterface.anime.extractor.Extractor {

    override fun canExtract(server: tel.jeelpa.plugininterface.models.VideoServer): Boolean {
        val domain = Uri.parse(server.embed.url).host
        return domain in domains
    }

    override suspend fun extract(server: tel.jeelpa.plugininterface.models.VideoServer, extraData: Map<String, String>): List<tel.jeelpa.plugininterface.models.Video> {
        return listOf(
            tel.jeelpa.plugininterface.models.Video(
                "Direct Url",
                1080,
                tel.jeelpa.plugininterface.models.VideoType.CONTAINER,
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
            ),
            tel.jeelpa.plugininterface.models.Video(
                "Proxy",
                720,
                tel.jeelpa.plugininterface.models.VideoType.CONTAINER,
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
            ),
        )
    }

    companion object {
        val domains = hashSetOf("example.com")
    }
}