package tel.jeelpa.otter.maltrackerplugin.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import tel.jeelpa.otter.maltrackerplugin.models.MalResponse
import tel.jeelpa.otter.maltrackerplugin.models.MediaListNode
import tel.jeelpa.otter.maltrackerplugin.paging.MalPagingSource
import tel.jeelpa.plugininterface.helpers.parsed
import tel.jeelpa.plugininterface.tracker.models.MediaCardData

abstract class BaseMalClient(
    private val okHttpClient: OkHttpClient
) {
    protected open fun delegateToPagingSource(
        dataCallback: suspend (Int, Int) -> MalResponse<MediaListNode>,
    ): Flow<PagingData<MediaCardData>> {
        return Pager(
            config = PagingConfig(
                30,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MalPagingSource(dataCallback) }
        ).flow
    }

    protected suspend fun makeMalQuery(builderParams: HttpUrl.Builder.() -> Unit): Response {
        val httpUrl = HttpUrl.Builder()
            .scheme("https")
            .host("api.myanimelist.net")
            .addPathSegment("v2")
            .apply(builderParams)
            .build()

        val request = Request.Builder().url(httpUrl).build()

        return withContext(Dispatchers.IO) {
            okHttpClient.newCall(request).execute()
        }
    }

    fun makePagedQuery(builderParams: HttpUrl.Builder.() -> Unit) =
        delegateToPagingSource { offset, limit ->
            val pagedQueryParam: HttpUrl.Builder.() -> Unit = {
                apply(builderParams)
                addQueryParameter("limit", limit.toString())
                addQueryParameter("offset", offset.toString())
            }
            makeMalQuery(pagedQueryParam).parsed()
        }
}

