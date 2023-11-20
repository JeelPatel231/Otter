package tel.jeelpa.plugin.extractors

import android.net.Uri
import tel.jeelpa.plugininterface.anime.extractor.Extractor
import tel.jeelpa.plugininterface.models.Video
import tel.jeelpa.plugininterface.models.VideoServer
import tel.jeelpa.plugininterface.models.VideoType

class DummyExtractor : Extractor {

    override fun canExtract(server: VideoServer): Boolean {
        val domain = Uri.parse(server.embed.url).host
        return domain in domains
    }

    override suspend fun extract(server: VideoServer, extraData: Map<String, String>): List<Video> {
        return listOf(
            Video(
                "Direct Url",
                1080,
                VideoType.CONTAINER,
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
            ),
            Video(
                "Proxy",
                720,
                VideoType.CONTAINER,
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
            ),
        )
    }

    companion object {
        val domains = hashSetOf("example.com")
    }
}