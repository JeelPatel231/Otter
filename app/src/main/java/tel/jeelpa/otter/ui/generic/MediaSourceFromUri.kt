package tel.jeelpa.otter.ui.generic

import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.HttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.BaseMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import okhttp3.OkHttpClient
import tel.jeelpa.otter.reference.models.VideoType

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class CreateMediaSourceFromUri(
    private val okHttpSourceFactory: OkHttpClient,
    private val simpleVideoCache: SimpleCache,
) {
    operator fun invoke(url: String, format: VideoType, headers: Map<String, String> = emptyMap()): BaseMediaSource {
        val dataSourceFactory = DataSource.Factory {
            val dataSource: HttpDataSource =
                OkHttpDataSource.Factory(okHttpSourceFactory).createDataSource()
            headers.forEach {
                dataSource.setRequestProperty(it.key, it.value)
            }
            dataSource
        }

        val cacheFactory = CacheDataSource.Factory().apply {
            setCache(simpleVideoCache)
            setUpstreamDataSourceFactory(dataSourceFactory)
        }

        val mediaItemBuilder = MediaItem.Builder().setUri(url)

        val mediaSource = when (format) {
            VideoType.M3U8 -> {
                val mediaItem = mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_M3U8).build()
                HlsMediaSource.Factory(cacheFactory).createMediaSource(mediaItem)
            }

            VideoType.DASH -> {
                val mediaItem = mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_MPD).build()
                DashMediaSource.Factory(cacheFactory).createMediaSource(mediaItem)
            }
            // mp4 just works with progressive containers
            else -> {
                val mediaItem = mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_MP4).build()
                ProgressiveMediaSource.Factory(cacheFactory).createMediaSource(mediaItem)
            }
        }

        return mediaSource
    }
}
