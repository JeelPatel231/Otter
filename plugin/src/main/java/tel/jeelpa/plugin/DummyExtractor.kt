package tel.jeelpa.plugin

import okhttp3.OkHttpClient
import tel.jeelpa.otter.reference.BaseExtractor
import tel.jeelpa.otter.reference.RegisterUseCase
import tel.jeelpa.otter.reference.models.Video
import tel.jeelpa.otter.reference.models.VideoType

class DummyExtractor(
    private val okHttpClient: OkHttpClient,
    override val registerUseCase: RegisterUseCase,
) : BaseExtractor() {

    override fun canExtract(domain: String): Boolean {
        return domains.contains(domain)
    }

    override fun extract(uri: String): List<Video> {
        return listOf(
            Video(
                1080,
                VideoType.CONTAINER,
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
            ),
            Video(
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