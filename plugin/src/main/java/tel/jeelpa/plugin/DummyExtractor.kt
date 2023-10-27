package tel.jeelpa.plugin

import okhttp3.OkHttpClient
import tel.jeelpa.otter.reference.BaseExtractor
import tel.jeelpa.otter.reference.RegisterUseCase
import tel.jeelpa.otter.reference.models.Video
import tel.jeelpa.otter.reference.models.VideoServer
import tel.jeelpa.otter.reference.models.VideoType

class DummyExtractor(
    private val okHttpClient: OkHttpClient,
    override val registerUseCase: RegisterUseCase,
) : BaseExtractor() {

    override fun canExtract(domain: String): Boolean {
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