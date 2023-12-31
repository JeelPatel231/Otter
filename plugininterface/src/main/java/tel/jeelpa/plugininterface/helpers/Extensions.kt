package tel.jeelpa.plugininterface.helpers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response


suspend fun <T, U> Collection<T>.asyncMap( transform: suspend (T) -> U ) = coroutineScope {
    map { async { transform(it) } }.map { it.await() }
}

suspend fun <T, U> Collection<T>.asyncMapNotNull( transform: suspend (T) -> U? ) = coroutineScope {
    map { async { transform(it) } }.mapNotNull { it.await() }
}

suspend fun OkHttpClient.get(url: String, referrer: String? = null, headers: Map<String, String> = emptyMap()): Response {
    val request = Request.Builder()
        .url(url)
        .get()
        .apply {
            if(referrer != null) {
                addHeader("referer", referrer)
            }
            headers.forEach {
                addHeader(it.key, it.value)
            }
        }
        .build()

    return withContext(Dispatchers.IO){ newCall(request).execute() }
}


fun OkHttpClient.post(url: String, data: RequestBody, referrer: String? = null, headers: Map<String, String> = emptyMap(), allowRedirects: Boolean = true): Response {
    val client = newBuilder()
        .followRedirects(allowRedirects)
        .followSslRedirects(allowRedirects)
        .build()

    val request = Request.Builder()
        .apply {
            if(referrer != null) {
                addHeader("referer", referrer)
            }
            headers.forEach {
                addHeader(it.key, it.value)
            }
        }
        .url(url)
        .post(data)
        .build()

    return client.newCall(request).execute()
}

fun Map<String, String>.toRequestBody(): RequestBody {
    return FormBody.Builder().apply {
        forEach {
            add(it.key, it.value)
        }
    }.build()
}

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T> Response.parsed() : T {
    val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }
    return jsonParser.decodeFromString(body!!.string())
}